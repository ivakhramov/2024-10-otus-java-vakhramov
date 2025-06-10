package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData<T> {

    // Оставил поля только для финального результата, (и tableName для ошибок) остальные вынес в конструктор
    private final String tableName;
    private final String selectAllSql;
    private final String selectByIdSql;
    private final String insertSql;
    private final String updateSql;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.tableName = entityClassMetaData.getName();
        String idFieldName = entityClassMetaData.getIdField().getName();

        // Перечисление всех полей для SELECT вместо *
        String allFieldsJoined =
                entityClassMetaData.getAllFields().stream().map(Field::getName).collect(Collectors.joining(", "));

        // Генерирую запросы один раз в конструкторе и кэширую
        this.selectAllSql = String.format("SELECT %s FROM %s", allFieldsJoined, tableName);
        this.selectByIdSql = String.format("SELECT %s FROM %s WHERE %s = ?", allFieldsJoined, tableName, idFieldName);

        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();

        if (fieldsWithoutId.isEmpty()) {
            this.insertSql = null;
            this.updateSql = null;
        } else {
            String fieldsWithoutIdJoined =
                    fieldsWithoutId.stream().map(Field::getName).collect(Collectors.joining(", "));
            String insertPlaceholders =
                    fieldsWithoutId.stream().map(field -> "?").collect(Collectors.joining(", "));
            String updateSetClauses = fieldsWithoutId.stream()
                    .map(field -> field.getName() + " = ?")
                    .collect(Collectors.joining(", "));

            this.insertSql = String.format(
                    "INSERT INTO %s (%s) VALUES (%s)", tableName, fieldsWithoutIdJoined, insertPlaceholders);
            this.updateSql = String.format("UPDATE %s SET %s WHERE %s = ?", tableName, updateSetClauses, idFieldName);
        }
    }

    @Override
    public String getSelectAllSql() {
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        if (insertSql == null) {
            throw new IllegalStateException(
                    "Cannot generate insert SQL for entity with no fields other than ID: " + tableName);
        }
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        if (updateSql == null) {
            throw new IllegalStateException(
                    "Cannot generate update SQL for entity with no fields to update: " + tableName);
        }
        return updateSql;
    }
}
