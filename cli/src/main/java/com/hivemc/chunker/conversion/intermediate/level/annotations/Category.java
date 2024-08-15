package com.hivemc.chunker.conversion.intermediate.level.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Sets the category of a field
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Category {
    /**
     * The category for this field.
     *
     * @return the category.
     */
    Type value();

    /**
     * The different categories of fields
     */
    enum Type {
        WORLD_SETTINGS("World Settings"),
        GAME_RULES("Game Rules"),
        RESTRICTIONS("Restrictions"),
        WEATHER("Weather"),
        MISC("Misc"),
        UNKNOWN("Unknown");
        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
