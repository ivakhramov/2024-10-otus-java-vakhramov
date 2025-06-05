package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> clazz;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;
    private final Constructor<T> constructor;
    private final String classNameInLowerCase;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
        this.classNameInLowerCase = clazz.getSimpleName().toLowerCase();

        this.allFields = Arrays.stream(clazz.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toList());

        this.idField = findIdFieldInternal();

        this.fieldsWithoutId = this.allFields.stream()
                .filter(field -> !field.equals(this.idField))
                .collect(Collectors.toList());

        this.constructor = findConstructorInternal();
        if (this.constructor != null) {
            this.constructor.setAccessible(true);
        }
    }

    private Field findIdFieldInternal() {
        List<Field> idFields = allFields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .collect(Collectors.toList());
        if (idFields.isEmpty()) {
            throw new IllegalArgumentException("No field annotated with @Id found in class " + clazz.getSimpleName());
        }
        if (idFields.size() > 1) {
            throw new IllegalArgumentException(
                    "Multiple fields annotated with @Id found in class " + clazz.getSimpleName());
        }
        return idFields.get(0);
    }

    private Constructor<T> findConstructorInternal() {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "No-arg constructor not found for class " + clazz.getSimpleName() + ". Please ensure it exists.",
                    e);
        }
    }

    @Override
    public String getName() {
        return this.classNameInLowerCase;
    }

    @Override
    public Constructor<T> getConstructor() {
        if (constructor == null) {
            throw new IllegalStateException("Constructor was not initialized for class " + clazz.getSimpleName());
        }
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}
