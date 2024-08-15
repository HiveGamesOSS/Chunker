package com.hivemc.chunker.cli;

import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JsonObject which can be the literal object or a path to a JSON file which should be loaded.
 */
public class JsonObjectOrFile {
    private final String json;

    /**
     * Load a JSON string from a file.
     *
     * @param path the path to the file.
     * @throws IOException if it failed to read the file.
     */
    public JsonObjectOrFile(Path path) throws IOException {
        json = Files.readString(path);
    }

    /**
     * Load a JSON string.
     *
     * @param json the json string.
     */
    public JsonObjectOrFile(String json) {
        this.json = json;
    }

    /**
     * Get the JSON string which was loaded.
     *
     * @return the JSON string (not validated).
     */
    public String getJSONObjectString() {
        return json;
    }

    /**
     * A converter which constructs the JsonObjectOrFile based on if the input string starts with the open bracket
     * character.
     */
    public static class Converter implements CommandLine.ITypeConverter<JsonObjectOrFile> {
        @Override
        public JsonObjectOrFile convert(String s) {
            if (s.trim().startsWith("{")) {
                return new JsonObjectOrFile(s);
            }

            // Try loading as a file
            try {
                return new JsonObjectOrFile(Path.of(s));
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
