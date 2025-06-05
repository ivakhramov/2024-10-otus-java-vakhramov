package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData<T> entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData<T> entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return createEntityFromResultSet(rs);
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), List.of(), rs -> {
                    var objectList = new ArrayList<T>();
                    try {
                        while (rs.next()) {
                            objectList.add(createEntityFromResultSet(rs));
                        }
                        return objectList;
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    }
                })
                .orElseGet(ArrayList::new);
    }

    @Override
    public long insert(Connection connection, T object) {
        try {
            List<Object> params = entityClassMetaData.getFieldsWithoutId().stream()
                    .map(field -> {
                        try {
                            return field.get(object);
                        } catch (IllegalAccessException e) {
                            throw new DataTemplateException(e);
                        }
                    })
                    .collect(Collectors.toList());
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T object) {
        try {
            List<Object> params = new ArrayList<>();
            for (Field field : entityClassMetaData.getFieldsWithoutId()) {
                params.add(field.get(object));
            }
            params.add(entityClassMetaData.getIdField().get(object));

            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private T createEntityFromResultSet(ResultSet rs) {
        try {
            T entity = entityClassMetaData.getConstructor().newInstance();
            for (Field field : entityClassMetaData.getAllFields()) {
                Object value = rs.getObject(field.getName().toLowerCase());
                field.set(entity, value);
            }
            return entity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | SQLException e) {
            throw new DataTemplateException(e);
        }
    }
}
