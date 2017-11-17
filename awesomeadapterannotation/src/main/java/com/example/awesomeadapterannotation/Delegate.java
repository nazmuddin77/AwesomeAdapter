package com.example.awesomeadapterannotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by nazmuddinmavliwala on 23/08/16.
 */

@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface Delegate {
    Class<?> viewHolder() default NONE.class;

    int layout() default 0;

    Class<?> viewHolderInteractor() default NONE.class;

    Class<?> dataObject()default Object.class;

    boolean useContext() default true;
}
