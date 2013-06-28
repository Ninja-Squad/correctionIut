package com.ninja_squad.formation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating to the loggind dynamic proxy if and how a method call must be logged
 * @author JB
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * Indicates if the arguments of the method call must be logged
     */
    boolean arguments() default false;

    /**
     * Indicates if the result of the method call must be logged
     */
    boolean returnValue() default false;

    /**
     * Indicates if the execution time of the method call must be logged
     */
    boolean executionTime() default false;
}
