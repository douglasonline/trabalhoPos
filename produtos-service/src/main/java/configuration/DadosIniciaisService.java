package configuration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;
import java.util.Set;

import static controller.ProdutoController.*;


//@ApplicationScoped
public class DadosIniciaisService {

    @Inject
    EntityManager entityManager;

    //@PostConstruct
    //@Transactional
    public void inicializarBancoDeDados() {


        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            if (!checkIfDatabaseExists(connection, nomeBanco)) {
                createDatabase(connection, nomeBanco);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //inicializarTabelasESequencias();
    }

    private boolean checkIfDatabaseExists(Connection connection, String dbName) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'")) {
            return rs.next();
        }
    }

    private void createDatabase(Connection connection, String dbName) throws SQLException {
        connection.createStatement().executeUpdate("CREATE DATABASE " + dbName);
    }

    private void inicializarTabelasESequencias() {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        for (EntityType<?> entityType : entities) {
            Long quantidadeRegistros = (Long) entityManager.createQuery("select count(*) from " + entityType.getName()).getSingleResult();

            if (quantidadeRegistros == 0) {
                try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("import.sql");
                     Scanner scanner = new Scanner(inputStream).useDelimiter("\\A")) {
                    if (scanner.hasNext()) {
                        String content = scanner.next();
                        entityManager.createNativeQuery(content).executeUpdate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}