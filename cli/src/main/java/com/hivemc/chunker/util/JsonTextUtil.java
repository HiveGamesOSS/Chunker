package com.hivemc.chunker.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * Util for converting JSON formatted text.
 */
public class JsonTextUtil {
    /**
     * The color code used in string form for legacy colors.
     */
    public static final char CHAT_COLOR = '§';
    /**
     * Pattern which matches legacy formatting using CHAT_COLOR.
     */
    public static final Pattern LEGACY_FORMAT_PATTERN = Pattern.compile("(?i)" + CHAT_COLOR + "[0-9A-FK-OR]");
    /**
     * Map containing the color name and the color to use for Bedrock edition.
     */
    public static final BiMap<String, Integer> COLOR_TO_BEDROCK_HEX = ImmutableBiMap.<String, Integer>builder()
            .put("black", 0xff000000) // Doesn't follow the pattern of the rest
            .put("red", 0xffB02E26)
            .put("green", 0xff5E7C16)
            .put("brown", 0xff835432)
            .put("blue", 0xff3C44AA)
            .put("purple", 0xff8932B8)
            .put("cyan", 0xff169C9C)
            .put("light_gray", 0xff9D9D97)
            .put("gray", 0xff474F52)
            .put("pink", 0xffF38BAA)
            .put("lime", 0xff80C71F)
            .put("yellow", 0xffFED83D)
            .put("light_blue", 0xff3AB3DA)
            .put("magenta", 0xffC74EBD)
            .put("orange", 0xffF9801D)
            .put("white", 0xfff0f0f0)
            .build();
    /**
     * Map containing the color name to the character which is used with legacy colors.
     */
    public static final BiMap<String, Character> COLOR_TO_CHARACTER = ImmutableBiMap.<String, Character>builder()
            .put("black", '0')
            .put("dark_blue", '1')
            .put("dark_green", '2')
            .put("dark_aqua", '3')
            .put("dark_red", '4')
            .put("dark_purple", '5')
            .put("gold", '6')
            .put("gray", '7')
            .put("dark_gray", '8')
            .put("blue", '9')
            .put("green", 'a')
            .put("aqua", 'b')
            .put("red", 'c')
            .put("light_purple", 'd')
            .put("yellow", 'e')
            .put("white", 'f')
            .put("obfuscated", 'k')
            .put("bold", 'l')
            .put("strikethrough", 'm')
            .put("underline", 'n')
            .put("italic", 'o')
            .put("reset", 'r')
            .build();
    /**
     * An empty text tag, used for empty inputs.
     */
    public static final JsonObject EMPTY_TEXT_TAG = fromText("");
    private static final Gson GSON = new Gson();

    /**
     * Create a JSON Object from literal text input.
     *
     * @param text the literal text to use.
     * @return a json object wrapping the text.
     */
    public static JsonObject fromText(String text) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", text);
        return jsonObject;
    }

    /**
     * Convert a JSON input to legacy text.
     *
     * @param input   the input JSON.
     * @param bedrock whether this is bedrock, if it is underline and strikethrough are ignored.
     * @return the legacy text form.
     */
    public static String toLegacy(JsonElement input, boolean bedrock) {
        if (input == null || input.isJsonNull()) return "";
        if (input.isJsonPrimitive()) {
            JsonPrimitive primitive = input.getAsJsonPrimitive();
            if (primitive.isString()) {
                return primitive.getAsString();
            }
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean() ? "true" : "false";
            }
            if (primitive.isNumber()) {
                return primitive.getAsNumber().toString();
            }
            throw new IllegalArgumentException("Unable to turn JSON into legacy " + input);
        }
        if (input.isJsonArray()) {
            StringBuilder output = new StringBuilder();
            for (JsonElement element : input.getAsJsonArray()) {
                output.append(toLegacy(element, bedrock));
            }

            return output.toString();
        }
        if (input.isJsonObject()) {
            JsonObject jsonObject = input.getAsJsonObject();
            StringBuilder output = new StringBuilder();

            // Apply colour (if present, as this can be 1.16 hex codes)
            if (jsonObject.has("color") && COLOR_TO_CHARACTER.containsKey(jsonObject.get("color").getAsString())) {
                output.append(CHAT_COLOR).append(COLOR_TO_CHARACTER.get(jsonObject.get("color").getAsString()));
            }

            // Bold
            if (jsonObject.has("bold") && jsonObject.get("bold").getAsBoolean()) {
                output.append(CHAT_COLOR).append(COLOR_TO_CHARACTER.get("bold"));
            }

            // Italic
            if (jsonObject.has("italic") && jsonObject.get("italic").getAsBoolean()) {
                output.append(CHAT_COLOR).append(COLOR_TO_CHARACTER.get("italic"));
            }

            // Underline
            if (jsonObject.has("underline") && jsonObject.get("underline").getAsBoolean() && !bedrock) {
                output.append(CHAT_COLOR).append(COLOR_TO_CHARACTER.get("underline"));
            }

            // Strikethrough
            if (jsonObject.has("strikethrough") && jsonObject.get("strikethrough").getAsBoolean() && !bedrock) {
                output.append(CHAT_COLOR).append(COLOR_TO_CHARACTER.get("strikethrough"));
            }

            // Obfuscated
            if (jsonObject.has("obfuscated") && jsonObject.get("obfuscated").getAsBoolean()) {
                output.append(CHAT_COLOR).append(COLOR_TO_CHARACTER.get("obfuscated"));
            }

            // Print text
            if (jsonObject.has("text")) {
                output.append(toLegacy(jsonObject.get("text"), bedrock));
            }

            // Print translate key
            if (jsonObject.has("translate")) {
                output.append(toLegacy(jsonObject.get("translate"), bedrock));
            }

            if (jsonObject.has("extra")) {
                output.append(toLegacy(jsonObject.get("extra"), bedrock));
            }

            return output.toString();
        }
        throw new IllegalArgumentException("Unable to turn JSON into legacy " + input);
    }

    /**
     * Convert a JSON input to text with no formatting.
     * Note: Translatable text will throw an IllegalArgumentException.
     *
     * @param input the input JSON.
     * @return the text without any formatting.
     */
    public static String toStripped(JsonElement input) {
        if (input == null || input.isJsonNull()) return "";
        if (input.isJsonPrimitive()) {
            JsonPrimitive primitive = input.getAsJsonPrimitive();
            if (primitive.isString()) {
                // Remove any legacy formatting
                return LEGACY_FORMAT_PATTERN.matcher(primitive.getAsString()).replaceAll("");
            }
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean() ? "true" : "false";
            }
            if (primitive.isNumber()) {
                return primitive.getAsNumber().toString();
            }
            throw new IllegalArgumentException("Unable to turn JSON into legacy " + input);
        }
        if (input.isJsonArray()) {
            StringBuilder output = new StringBuilder();
            for (JsonElement element : input.getAsJsonArray()) {
                output.append(toStripped(element));
            }

            return output.toString();
        }
        if (input.isJsonObject()) {
            JsonObject jsonObject = input.getAsJsonObject();
            StringBuilder output = new StringBuilder();

            // Print text
            if (jsonObject.has("text")) {
                output.append(toStripped(jsonObject.get("text")));
            }

            // Print translate key
            if (jsonObject.has("translate")) {
                throw new IllegalArgumentException("Unable to turn JSON into legacy " + jsonObject);
            }

            if (jsonObject.has("extra")) {
                output.append(toStripped(jsonObject.get("extra")));
            }

            return output.toString();
        }
        throw new IllegalArgumentException("Unable to turn JSON into legacy " + input);
    }

    /**
     * Convert a JSON string to an element.
     *
     * @param jsonString the input JSON string.
     * @return the json element that was parsed or the original string if it failed to parse.
     */
    public static JsonElement fromJSON(@Nullable String jsonString) {
        if (jsonString == null) return EMPTY_TEXT_TAG;
        try {
            JsonElement jsonElement = GSON.fromJson(jsonString, JsonElement.class);

            // Handle null
            if (jsonElement.isJsonNull()) return EMPTY_TEXT_TAG;

            // Ensure strings are turned into json objects
            if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                return fromText(jsonElement.getAsJsonPrimitive().getAsString());
            }

            // Otherwise return the JSON element
            return jsonElement;
        } catch (Exception e) {
            // Fallback to basic text
            return fromText(jsonString);
        }
    }

    /**
     * Convert a JsonElement to a string for chat input.
     *
     * @param jsonElement the input.
     * @return the element as a string representation.
     */
    public static String toJSON(JsonElement jsonElement) {
        return GSON.toJson(jsonElement);
    }
}
