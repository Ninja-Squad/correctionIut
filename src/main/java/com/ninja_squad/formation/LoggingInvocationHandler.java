package com.ninja_squad.formation;

import com.google.common.reflect.AbstractInvocationHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * This is the actual implementation of the dynamic proxy which logs the calls to an object based on the annotation
 * present on the method
 * @author JB
 */
public class LoggingInvocationHandler extends AbstractInvocationHandler {

    /**
     * The actual object to call, i.e. the object for which a proxy is created
     */
    private final Object delegate;

    public LoggingInvocationHandler(Object delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        Method delegateMethod = delegate.getClass().getMethod(method.getName(), method.getParameterTypes());
        if (!delegateMethod.isAnnotationPresent(Log.class)) {
            return delegateMethod.invoke(delegate, args);
        }

        Log log = delegateMethod.getAnnotation(Log.class);
        System.out.println("Calling method " + delegateMethod);
        if (log.arguments()) {
            System.out.println("\tArguments: " + Arrays.toString(args));
        }
        long t0 = System.nanoTime();
        try {
            Object result = delegateMethod.invoke(delegate, args);
            if (log.returnValue()) {
                System.out.println("\tReturn value: " + result);
            }
            return result;
        }
        finally {
            if (log.executionTime()) {
                System.out.println("\tThe call took " + (System.nanoTime() - t0) + " nanoseconds");
            }
        }
    }
}
