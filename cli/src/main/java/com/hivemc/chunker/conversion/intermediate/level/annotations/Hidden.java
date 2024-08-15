package com.hivemc.chunker.conversion.intermediate.level.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates a field is not to be serialized.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Hidden {
}
