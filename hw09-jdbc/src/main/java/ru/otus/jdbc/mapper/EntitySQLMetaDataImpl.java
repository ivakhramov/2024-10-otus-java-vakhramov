package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData<T> {

    private final EntityClassMetaData<T> entityClassMetaData;
    private final String tableName;
    private final String idFieldName;
    private final String fieldsWithoutIdJoined;
    private final String insertPlaceholders;
    private final String updateSetClauses;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
        this.tableName = entityClassMetaData.getName();
        this.idFieldName = entityClassMetaData.getIdField().getName();

        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        this.fieldsWithoutIdJoined =
                fieldsWithoutId.stream().map(Field::getName).collect(Collectors.joining(", "));

        this.insertPlaceholders = fieldsWithoutId.stream().map(field -> "?").collect(Collectors.joining(", "));

        this.updateSetClauses =
                fieldsWithoutId.stream().map(field -> field.getName() + " = ?").collect(Collectors.joining(", "));
    }

    @Override
    public String getSelectAllSql() {
        return String.format("SELECT * FROM %s", tableName);
    }

    @Override
    public String getSelectByIdSql() {
        return String.format("SELECT * FROM %s WHERE %s = ?", tableName, idFieldName);
    }

    @Override
    public String getInsertSql() {
        if (fieldsWithoutIdJoined.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot generate insert SQL for entity with no fields other than ID: " + tableName);
        }
        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, fieldsWithoutIdJoined, insertPlaceholders);
    }

    @Override
    public String getUpdateSql() {
        if (updateSetClauses.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot generate update SQL for entity with no fields to update: " + tableName);
        }
        return String.format("UPDATE %s SET %s WHERE %s = ?", tableName, updateSetClauses, idFieldName);
    }
}
