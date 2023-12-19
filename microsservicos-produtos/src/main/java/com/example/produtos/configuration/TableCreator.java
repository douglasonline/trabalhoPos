package com.example.produtos.configuration;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.math.BigDecimal;
import java.util.Set;

public class TableCreator {

    private final EntityManager entityManager;

    public TableCreator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String createTable(Class<?> entityClass) {
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType<?> entityType = metamodel.entity(entityClass);

        // Obtendo o nome da tabela da anotação @Table
        String tableName = getTableName(entityType);

        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + tableName + " (");

        // Adicionando coluna 'id' autoincrementável como chave primária
        createTableQuery.append("id SERIAL PRIMARY KEY,");

        // Adicionando outras colunas com seus tipos
        Set<? extends Attribute<?, ?>> attributes = entityType.getDeclaredAttributes();
        for (Attribute<?, ?> attribute : attributes) {
            if (!"id".equals(attribute.getName())) {
                String columnName = attribute.getName();
                String columnType = getColumnType(attribute);

                createTableQuery.append(columnName).append(" ").append(columnType).append(",");
            }
        }

        createTableQuery.setLength(createTableQuery.length() - 1); // Remover a última vírgula
        createTableQuery.append(");");

        return createTableQuery.toString();
    }

    private String getTableName(EntityType<?> entityType) {
        Table table = entityType.getJavaType().getAnnotation(Table.class);
        return table != null ? table.name() : entityType.getName();
    }

    private String getColumnType(Attribute<?, ?> attribute) {
        // Lógica para mapear o tipo de atributo para um tipo de coluna do banco de dados
        // Você pode adicionar sua lógica de mapeamento aqui

        // Exemplo: Mapeamento simples de tipos Java para tipos SQL
        Class<?> attributeType = attribute.getJavaType();
        if (attributeType == String.class) {
            return "VARCHAR(255)";
        } else if (attributeType == Long.class || attributeType == Integer.class) {
            return "INTEGER";
        } else if (attributeType == BigDecimal.class) {
            return "NUMERIC(10,2)";
        }

        return "VARCHAR(255)";
    }
}