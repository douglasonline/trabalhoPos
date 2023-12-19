package com.example.produtos;


import com.example.produtos.configuration.TableCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;
import java.util.Set;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.produtos.repository")
@EntityScan(basePackages = "com.example.produtos.model") // Outra tipo de anotação, para indicar ao Spring onde encontrar as entidades que estajam em outro pacote
public class ProdutosApplication {

	public static final String nomeBanco = "microsservicos_produtos";

	//Banco de dados padrão do PostgreSQL (postgres)
	public static final String URL1 = "jdbc:postgresql://localhost:5432/postgres";
	public static final String URL = "jdbc:postgresql://localhost:5432/microsservicos_produtos" ;
	public static final String USERNAME = "postgres";
	public static final String PASSWORD = "1234";

	@Autowired
	EntityManager entityManager;

	public static void main(String[] args) {

		try (Connection connection = DriverManager.getConnection(URL1, USERNAME, PASSWORD)) {
			if (!checkIfDatabaseExists(connection, nomeBanco)) {
				createDatabase(connection, nomeBanco);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		SpringApplication.run(ProdutosApplication.class, args);

	}

	@Bean
	public CommandLineRunner createDatabaseIfNotExists() {
		return args -> {

			inicializarTabelas();

		};
	}

	private static boolean checkIfDatabaseExists(Connection connection, String dbName) throws SQLException {
		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'")) {
			return rs.next();
		}
	}

	private static void createDatabase(Connection connection, String dbName) throws SQLException {
		connection.createStatement().executeUpdate("CREATE DATABASE " + dbName);
	}

	private void inicializarTabelas() {
		try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
			DatabaseMetaData metaData = connection.getMetaData();
			Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

			for (EntityType<?> entityType : entities) {
				String tableName = getTableName(entityType);

				if (tableName != null && !tableExists(metaData, tableName)) {
					createTableFromEntity(entityType);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Segundo: " + e.getMessage());
		}
	}

	private void createTableFromEntity(EntityType<?> entityType) {
		Class<?> entityClass = entityType.getJavaType();

		// Cria um TabelaCriador usando o EntityManager
		TableCreator tabelaCriador = new TableCreator(entityManager);

		// Obtém o script SQL para criar a tabela da entidade
		String createTableQuery = tabelaCriador.createTable(entityClass);

		// Execute a query para criar a tabela
		try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			 PreparedStatement preparedStatement = connection.prepareStatement(createTableQuery)) {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Terceiro: " + e.getMessage());
		}
	}

	private String getTableName(EntityType<?> entityType) {
		Class<?> entityClass = entityType.getJavaType();

		// Lógica para obter o nome da tabela diretamente da anotação @Table na classe da entidade
		Table table = entityClass.getAnnotation(Table.class);
		if (table != null) {
			return table.name();
		}

		// Se a anotação @Table não estiver presente, retorna null
		return null;
	}

	private boolean tableExists(DatabaseMetaData metaData, String tableName) throws SQLException {
		try (ResultSet tables = metaData.getTables(null, null, tableName, null)) {
			return tables.next();
		}
	}
}