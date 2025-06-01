package ru.otus.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.Log;

public class LogInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(LogInvocationHandler.class);
    private final Object target;
    private final Set<String> loggedMethodSignatures;

    public LogInvocationHandler(Object target) {
        this.target = target;
        this.loggedMethodSignatures = new HashSet<>();
        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Log.class)) {
                loggedMethodSignatures.add(getMethodSignature(method));
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (loggedMethodSignatures.contains(getMethodSignature(method))) {
            logMethodCall(method, args);
        }
        return method.invoke(target, args);
    }

    private void logMethodCall(Method method, Object[] args) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("executed method: ").append(method.getName());

        if (args != null && args.length > 0) {
            if (args.length == 1) {
                logMessage.append(", param: ").append(args[0]);
            } else {
                logMessage.append(", params: ");
                logMessage.append(Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(", ")));
            }
        }
        logger.info(logMessage.toString());
    }

    private String getMethodSignature(Method method) {
        return method.getName()
                + Arrays.stream(method.getParameterTypes())
                        .map(Class::getName)
                        .collect(Collectors.joining(",", "(", ")"));
    }
}
