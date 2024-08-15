package com.hivemc.chunker.conversion.intermediate.level.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates a field uses a custom type that needs a level specific handler.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomType {
}
