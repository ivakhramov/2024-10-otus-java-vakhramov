package ru.otus.appcontainer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
        Arrays.stream(initialConfigClasses)
                .sorted(Comparator.comparingInt(clazz ->
                        clazz.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEach(this::processConfig);
    }

    public AppComponentsContainerImpl(String packageName) {
        Reflections reflections = new Reflections(packageName);
        reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class).stream()
                .sorted(Comparator.comparingInt(clazz ->
                        clazz.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEach(this::processConfig);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        // You code here...
        try {
            Object configInstance = configClass.getDeclaredConstructor().newInstance();
            List<Method> componentMethods = Arrays.stream(configClass.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(AppComponent.class))
                    .sorted(Comparator.comparingInt(
                            m -> m.getAnnotation(AppComponent.class).order()))
                    .toList();

            for (Method method : componentMethods) {
                String componentName = method.getAnnotation(AppComponent.class).name();
                if (appComponentsByName.containsKey(componentName)) {
                    throw new RuntimeException("Component with name '" + componentName + "' already exists.");
                }

                Object[] args = Arrays.stream(method.getParameters())
                        .map(Parameter::getType)
                        .map(this::getAppComponent)
                        .toArray();

                Object component = method.invoke(configInstance, args);
                appComponents.add(component);
                appComponentsByName.put(componentName, component);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process config", e);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> foundComponents = appComponents.stream()
                .filter(component -> componentClass.isAssignableFrom(component.getClass()))
                .toList();

        if (foundComponents.size() > 1) {
            throw new RuntimeException("Found more than one component of type " + componentClass.getName());
        }
        if (foundComponents.isEmpty()) {
            throw new RuntimeException("Component not found by class " + componentClass.getName());
        }

        return (C) foundComponents.get(0);
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        C component = (C) appComponentsByName.get(componentName);
        if (component == null) {
            throw new RuntimeException("Component not found by name: " + componentName);
        }
        return component;
    }
}
