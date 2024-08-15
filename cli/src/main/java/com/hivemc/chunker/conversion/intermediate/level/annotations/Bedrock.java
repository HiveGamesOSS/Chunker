package com.hivemc.chunker.conversion.intermediate.level.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates a field is present for Bedrock edition.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Bedrock {
    /**
     * The name of the field in Bedrock.
     *
     * @return the name or empty if it should use the field name.
     */
    String value() default "";

    /**
     * The type to use for Bedrock.
     *
     * @return the type.
     */
    Class<?> type() default Object.class;
}
