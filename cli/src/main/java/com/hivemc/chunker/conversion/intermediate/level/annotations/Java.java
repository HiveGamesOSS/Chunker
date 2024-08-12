package com.hivemc.chunker.conversion.intermediate.level.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates a field is present for Java edition.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Java {
    /**
     * The name of the field in Java.
     *
     * @return the name or empty if it should use the field name.
     */
    String value() default "";

    /**
     * The type to use for Java.
     *
     * @return the type.
     */
    Class<?> type() default Object.class;
}
