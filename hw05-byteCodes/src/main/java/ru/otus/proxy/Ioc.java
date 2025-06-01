package ru.otus.proxy;

import java.lang.reflect.Proxy;

public class Ioc {

    private Ioc() {}

    @SuppressWarnings("unchecked")
    public static <T> T createLoggedInstance(Object implementation, Class<T> interfaceClazz) {
        if (!interfaceClazz.isInterface()) {
            throw new IllegalArgumentException(interfaceClazz.getCanonicalName() + " is not an interface");
        }
        if (!interfaceClazz.isAssignableFrom(implementation.getClass())) {
            throw new IllegalArgumentException(
                    "Implementation class " + implementation.getClass().getCanonicalName()
                            + " does not implement interface " + interfaceClazz.getCanonicalName());
        }

        LogInvocationHandler handler = new LogInvocationHandler(implementation);

        return (T) Proxy.newProxyInstance(Ioc.class.getClassLoader(), new Class<?>[] {interfaceClazz}, handler);
    }
}
