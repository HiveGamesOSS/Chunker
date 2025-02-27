package com.hivemc.chunker.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.*;
import com.hivemc.chunker.conversion.encoding.java.JavaDataVersion;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.array.IntArrayTag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.FloatTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
     * Convert NBT (Java 1.21.5+) to a JSON element (below Java 1.21.5).
     *
     * @param tag the input NBT tag.
     * @return the json element that was parsed or the original string if it failed to parse.
     */
    public static JsonElement fromNBT(@Nullable Tag<?> tag) {
        try {
            if (tag == null) return EMPTY_TEXT_TAG;

            // If it's a StringTag, it's either text or JSON, if the world isn't fully upgraded it might be JSON.
            if (tag instanceof StringTag stringTag) {
                return fromJSON(stringTag.getValue());
            }
            if (tag instanceof CompoundTag compoundTag) {
                // Special case for mixed tag type handling, use just the empty tag
                if (compoundTag.contains("")) {
                    return fromNBT(compoundTag.get(""));
                }

                // Handle it as a compound tag
                JsonObject converted = compoundTag.contains("text") ? fromText(compoundTag.getString("text", "")) : new JsonObject();

                // Style components
                compoundTag.getOptionalValue("color", String.class)
                        .ifPresent(value -> converted.addProperty("color", value));
                compoundTag.getOptional("shadow_color", Tag.class).ifPresent(shadowColorTag -> {
                    // This is either an int or a list
                    if (shadowColorTag instanceof IntTag intTag) {
                        converted.addProperty("shadow_color", intTag.getValue());
                    } else if (shadowColorTag instanceof ListTag<?, ?>) {
                        JsonArray array = new JsonArray();
                        List<Float> values = compoundTag.getListValues("shadow_color", FloatTag.class);
                        for (Float value : values) {
                            array.add(value);
                        }
                        converted.add("shadow_color", array);
                    }
                });
                compoundTag.getOptionalValue("bold", Byte.class)
                        .ifPresent(value -> converted.addProperty("bold", value == 1));
                compoundTag.getOptionalValue("italic", Byte.class)
                        .ifPresent(value -> converted.addProperty("italic", value == 1));
                compoundTag.getOptionalValue("underlined", Byte.class)
                        .ifPresent(value -> converted.addProperty("underlined", value == 1));
                compoundTag.getOptionalValue("strikethrough", Byte.class)
                        .ifPresent(value -> converted.addProperty("strikethrough", value == 1));
                compoundTag.getOptionalValue("obfuscated", Byte.class)
                        .ifPresent(value -> converted.addProperty("obfuscated", value == 1));

                // Selector component
                compoundTag.getOptionalValue("selector", String.class)
                        .ifPresent(value -> converted.addProperty("selector", value));
                compoundTag.getOptional("separator", Tag.class)
                        .ifPresent(value -> converted.add("separator", fromNBT(value)));

                // NBT component
                compoundTag.getOptionalValue("nbt", String.class)
                        .ifPresent(value -> converted.addProperty("nbt", value));
                compoundTag.getOptionalValue("source", String.class)
                        .ifPresent(value -> converted.addProperty("source", value));
                compoundTag.getOptionalValue("interpret", Byte.class)
                        .ifPresent(value -> converted.addProperty("interpret", value == 1));
                // Also uses separator

                // Translate component
                compoundTag.getOptionalValue("translate", String.class)
                        .ifPresent(value -> converted.addProperty("translate", value));
                compoundTag.getOptional("fallback", Tag.class)
                        .ifPresent(value -> converted.add("fallback", fromNBT(value)));

                // Other components
                compoundTag.getOptionalValue("keybind", String.class)
                        .ifPresent(value -> converted.addProperty("keybind", value));
                compoundTag.getOptionalValue("insertion", String.class)
                        .ifPresent(value -> converted.addProperty("insertion", value));
                compoundTag.getOptionalValue("font", String.class)
                        .ifPresent(value -> converted.addProperty("font", value));

                // Score component
                compoundTag.getOptional("score", CompoundTag.class).ifPresent(value -> {
                    JsonObject score = new JsonObject();
                    value.getOptionalValue("objective", String.class)
                            .ifPresent(value2 -> score.addProperty("objective", value2));
                    value.getOptionalValue("name", String.class)
                            .ifPresent(value2 -> score.addProperty("name", value2));
                    converted.add("score", score);
                });

                // Hover event component
                compoundTag.getOptional("hover_event", CompoundTag.class).ifPresent(value -> {
                    JsonObject hoverEvent = new JsonObject();
                    String action = value.getString("action", "");
                    if (action.isEmpty()) return;

                    // Add the action
                    hoverEvent.addProperty("action", action);
                    switch (action) {
                        case "show_text":
                            value.getOptional("value", Tag.class)
                                    .ifPresent(value2 -> hoverEvent.add("contents", fromNBT(value2)));
                            break;
                        case "show_item":
                            JsonObject itemContents = new JsonObject();
                            value.getOptionalValue("id", String.class)
                                    .ifPresent(value2 -> itemContents.addProperty("id", value2));
                            value.getOptionalValue("count", Integer.class)
                                    .ifPresent(value2 -> itemContents.addProperty("count", value2));
                            // Note: components aren't supported as we don't have a serializer/deserializer for that

                            // Add contents if found any tags
                            if (!itemContents.isEmpty()) {
                                hoverEvent.add("contents", itemContents);
                            }
                            break;
                        case "show_entity":
                            JsonObject entityContents = new JsonObject();
                            value.getOptional("name", Tag.class)
                                    .ifPresent(value2 -> entityContents.add("name", fromNBT(value2)));
                            value.getOptionalValue("id", String.class)
                                    .ifPresent(value2 -> entityContents.addProperty("type", value2));
                            value.getOptional("uuid", Tag.class).ifPresent(value2 -> {
                                if (value2 instanceof StringTag stringTag) {
                                    entityContents.addProperty("id", stringTag.getValue());
                                } else if (value2 instanceof IntArrayTag intArrayTag) {
                                    JsonArray ids = new JsonArray();
                                    for (int i : Objects.requireNonNull(intArrayTag.getValue())) {
                                        ids.add(i);
                                    }
                                    entityContents.add("id", ids);
                                } else {
                                    throw new IllegalArgumentException("Unknown UUID type " + value2);
                                }
                            });

                            // Add contents if found any tags
                            if (!entityContents.isEmpty()) {
                                hoverEvent.add("contents", entityContents);
                            }
                            break;
                    }
                    converted.add("hoverEvent", hoverEvent);
                });

                // Click event component
                compoundTag.getOptional("click_event", CompoundTag.class).ifPresent(value -> {
                    JsonObject clickEvent = new JsonObject();
                    String action = value.getString("action", "");
                    if (action.isEmpty()) return;

                    // Add the action
                    clickEvent.addProperty("action", action);
                    switch (action) {
                        case "open_url":
                            value.getOptionalValue("url", String.class)
                                    .ifPresent(value2 -> clickEvent.addProperty("value", value2));
                            break;
                        case "open_file":
                            value.getOptionalValue("path", String.class)
                                    .ifPresent(value2 -> clickEvent.addProperty("value", value2));
                            break;
                        case "run_command":
                        case "suggest_command":
                            value.getOptionalValue("command", String.class)
                                    .ifPresent(value2 -> clickEvent.addProperty("value", value2));
                            break;
                        case "change_page":
                            value.getOptionalValue("page", Integer.class)
                                    .ifPresent(value2 -> clickEvent.addProperty("value", String.valueOf(value2)));
                            break;
                        default:
                            value.getOptionalValue("value", String.class)
                                    .ifPresent(value2 -> clickEvent.addProperty("value", value2));
                    }
                    converted.add("clickEvent", clickEvent);
                });

                // Deserialize the extra tag
                if (compoundTag.contains("extra")) {
                    converted.add("extra", fromNBT(compoundTag.get("extra")));
                }

                // Return the tag
                return converted;
            }
            if (tag instanceof ListTag<?, ?> listTag) {
                // Convert all the children to JSON
                JsonArray jsonArray = new JsonArray();
                for (Tag<?> child : listTag) {
                    jsonArray.add(fromNBT(child));
                }
                return jsonArray;
            }
        } catch (Throwable throwable) {
            throw new IllegalArgumentException("Unable to parse Text component " + tag, throwable);
        }

        // Unknown component
        throw new IllegalArgumentException("Unknown type for Text component " + tag);
    }

    /**
     * Convert a JSON element (below Java 1.21.5) to NBT (Java 1.21.5+).
     *
     * @param element the input json element.
     * @return the output NBT.
     */
    public static Tag<?> toNBT(@Nullable JsonElement element) {
        try {
            if (element == null) return new StringTag("");

            // Turn JSON string into a string tag
            if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if (primitive.isString()) {
                    return new StringTag(primitive.getAsString());
                }
                throw new IllegalArgumentException("Unknown type for Text component " + element);
            }
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();

                // Handle it as a compound tag
                CompoundTag converted = new CompoundTag();

                // Add text
                if (object.has("text")) {
                    converted.put("text", object.get("text").getAsString());
                }

                // Style components
                if (object.has("color")) {
                    converted.put("color", object.get("color").getAsString());
                }
                if (object.has("shadow_color")) {
                    JsonElement shadowColor = object.get("shadow_color");
                    // This is either an int or a list
                    if (shadowColor.isJsonPrimitive()) {
                        converted.put("shadow_color", shadowColor.getAsInt());
                    } else {
                        List<Float> values = new ArrayList<>(4);
                        for (JsonElement v : shadowColor.getAsJsonArray()) {
                            values.add(v.getAsFloat());
                        }
                        converted.put("shadow_color", ListTag.fromValues(TagType.FLOAT, values));
                    }
                }
                if (object.has("bold")) {
                    converted.put("bold", object.get("bold").getAsBoolean() ? (byte) 1 : (byte) 0);
                }
                if (object.has("italic")) {
                    converted.put("italic", object.get("italic").getAsBoolean() ? (byte) 1 : (byte) 0);
                }
                if (object.has("underlined")) {
                    converted.put("underlined", object.get("underlined").getAsBoolean() ? (byte) 1 : (byte) 0);
                }
                if (object.has("strikethrough")) {
                    converted.put("strikethrough", object.get("strikethrough").getAsBoolean() ? (byte) 1 : (byte) 0);
                }
                if (object.has("obfuscated")) {
                    converted.put("obfuscated", object.get("obfuscated").getAsBoolean() ? (byte) 1 : (byte) 0);
                }

                // Selector component
                if (object.has("selector")) {
                    converted.put("selector", object.get("selector").getAsString());
                }
                if (object.has("separator")) {
                    converted.put("separator", toNBT(object.get("separator")));
                }

                // NBT component
                if (object.has("nbt")) {
                    converted.put("nbt", object.get("nbt").getAsString());
                }
                if (object.has("source")) {
                    converted.put("source", object.get("source").getAsString());
                }
                if (object.has("interpret")) {
                    converted.put("interpret", object.get("interpret").getAsBoolean() ? (byte) 1 : (byte) 0);
                }
                // Also uses separator

                // Translate component
                if (object.has("translate")) {
                    converted.put("translate", object.get("translate").getAsString());
                }
                if (object.has("fallback")) {
                    converted.put("fallback", toNBT(object.get("fallback")));
                }

                // Other components
                if (object.has("keybind")) {
                    converted.put("keybind", object.get("keybind").getAsString());
                }
                if (object.has("insertion")) {
                    converted.put("insertion", object.get("insertion").getAsString());
                }
                if (object.has("font")) {
                    converted.put("font", object.get("font").getAsString());
                }

                // Score component
                if (object.has("score")) {
                    JsonObject scoreElement = object.getAsJsonObject("score");
                    CompoundTag scoreTag = new CompoundTag();
                    if (scoreElement.has("objective")) {
                        scoreTag.put("objective", scoreElement.get("objective").getAsString());
                    }
                    if (scoreElement.has("name")) {
                        scoreTag.put("name", scoreElement.get("name").getAsString());
                    }
                    converted.put("score", scoreTag);
                }

                // Hover event component
                if (object.has("hoverEvent")) {
                    JsonObject hoverEvent = object.getAsJsonObject("hoverEvent");

                    // Create the tag for the event
                    CompoundTag hoverEventTag = new CompoundTag();

                    // Parse the action
                    String action = hoverEvent.has("action") ? hoverEvent.get("action").getAsString() : "";
                    if (!action.isEmpty()) {
                        // Add the action
                        hoverEventTag.put("action", action);
                        switch (action) {
                            case "show_text":
                                if (hoverEvent.has("contents")) {
                                    hoverEventTag.put("value", toNBT(hoverEvent.get("contents")));
                                }
                                if (hoverEvent.has("value")) {
                                    hoverEventTag.put("value", toNBT(hoverEvent.get("value")));
                                }
                                break;
                            case "show_item":
                                if (hoverEvent.has("contents")) {
                                    JsonObject contents = hoverEvent.getAsJsonObject("contents");
                                    if (contents.has("id")) {
                                        hoverEventTag.put("id", contents.get("id").getAsString());
                                    }
                                    if (contents.has("count")) {
                                        hoverEventTag.put("count", contents.get("count").getAsInt());
                                    }
                                    // Note: components aren't supported as we don't have a serializer/deserializer for that
                                }
                                break;
                            case "show_entity":
                                if (hoverEvent.has("contents")) {
                                    JsonObject contents = hoverEvent.getAsJsonObject("contents");
                                    if (contents.has("type")) {
                                        hoverEventTag.put("id", contents.get("type").getAsString());
                                    }
                                    if (contents.has("name")) {
                                        hoverEventTag.put("name", toNBT(contents.get("type")));
                                    }
                                    if (contents.has("id")) {
                                        JsonElement uuid = contents.get("id");
                                        if (uuid.isJsonPrimitive()) {
                                            hoverEventTag.put("uuid", contents.get("id").getAsString());
                                        } else if (uuid.isJsonArray()) {
                                            int[] ids = new int[4];
                                            int i = 0;
                                            for (JsonElement id : uuid.getAsJsonArray()) {
                                                ids[i] = id.getAsInt();
                                            }
                                            hoverEventTag.put("uuid", ids);
                                        } else {
                                            throw new IllegalArgumentException("Unknown UUID type " + uuid);
                                        }
                                    }
                                    // Note: components aren't supported as we don't have a serializer/deserializer for that
                                }
                                break;
                        }
                        converted.put("hover_event", hoverEventTag);
                    }
                }

                // Click event component
                if (object.has("clickEvent")) {
                    JsonObject clickEvent = object.getAsJsonObject("clickEvent");

                    // Create the tag for the event
                    CompoundTag clickEventTag = new CompoundTag();

                    // Parse the action
                    String action = clickEvent.has("action") ? clickEvent.get("action").getAsString() : "";
                    if (!action.isEmpty()) {
                        // Add the action
                        clickEventTag.put("action", action);
                        switch (action) {
                            case "open_url":
                                if (clickEvent.has("value")) {
                                    clickEventTag.put("url", clickEvent.get("value").getAsString());
                                }
                                break;
                            case "open_file":
                                if (clickEvent.has("value")) {
                                    clickEventTag.put("path", clickEvent.get("value").getAsString());
                                }
                                break;
                            case "run_command":
                            case "suggest_command":
                                if (clickEvent.has("value")) {
                                    clickEventTag.put("command", clickEvent.get("value").getAsString());
                                }
                                break;
                            case "change_page":
                                if (clickEvent.has("value")) {
                                    clickEventTag.put("page", Integer.parseInt(clickEvent.get("value").getAsString()));
                                }
                                break;
                            default:
                                if (clickEvent.has("value")) {
                                    clickEventTag.put("value", clickEvent.get("value").getAsString());
                                }
                                break;
                        }
                        converted.put("click_event", clickEventTag);
                    }
                }

                // Deserialize the extra tag
                if (object.has("extra")) {
                    converted.put("extra", toNBT(object.get("extra")));
                }

                // Return the tag
                return converted;
            }
            if (element.isJsonArray()) {
                // Convert all the children to JSON (they all need to be compound tags)
                ListTag<CompoundTag, Map<String, Tag<?>>> listTag = new ListTag<>(TagType.COMPOUND);
                JsonArray array = element.getAsJsonArray();

                // Iterate through each element and convert it
                for (JsonElement child : array) {
                    Tag<?> converted = toNBT(child);
                    if (converted instanceof CompoundTag compoundTag) {
                        listTag.add(compoundTag);
                    } else {
                        // Nest into a CompoundTag
                        CompoundTag holder = new CompoundTag();
                        holder.put("", converted);
                        listTag.add(holder);
                    }
                }
                return listTag;
            }
        } catch (Throwable throwable) {
            throw new IllegalArgumentException("Unable to parse Text JSON " + element, throwable);
        }

        // Unknown component
        throw new IllegalArgumentException("Unknown type for Text JSON " + element);
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

    /**
     * Convert a JsonElement to NBT/StringTag for chat input depending on if the output is 1.21.5 or newer.
     *
     * @param jsonElement     the input.
     * @param javaDataVersion the java data version.
     * @return the element serialized dependant on version.
     */
    public static Tag<?> toNBT(JsonElement jsonElement, JavaDataVersion javaDataVersion) {
        // 1.21.5+ uses NBT for JSON
        if (javaDataVersion.getVersion().isGreaterThanOrEqual(1, 21, 5)) {
            return toNBT(jsonElement);
        } else {
            return new StringTag(toJSON(jsonElement));
        }
    }

    /**
     * Convert a JsonElement List to a ListTag for chat input depending on if the output is 1.21.5 or newer.
     *
     * @param jsonElements    the input list.
     * @param minimumSize     the minimum size of the output list (inserting blank elements to pad).
     * @param javaDataVersion the java data version.
     * @return the element serialized dependant on version.
     */
    public static ListTag<?, ?> toNBT(List<JsonElement> jsonElements, int minimumSize, JavaDataVersion javaDataVersion) {
        // 1.21.5+ uses NBT for JSON
        if (javaDataVersion.getVersion().isGreaterThanOrEqual(1, 21, 5)) {
            boolean mixedListTagType = false;
            TagType<?, ?> tagType = null;

            List<Tag<?>> tags = new ArrayList<>(Math.max(jsonElements.size(), minimumSize));
            // Add each element
            for (JsonElement jsonElement : jsonElements) {
                Tag<?> tag = toNBT(jsonElement, javaDataVersion);

                // Check if we need to record the first tag / if the type differs from the rest of the tags
                if (tagType == null) {
                    tagType = tag.getType();
                } else if (tagType != tag.getType()) {
                    mixedListTagType = true;
                }

                // Add to the list
                tags.add(tag);
            }

            // Ensure we reach the minimum size so pad it
            while (tags.size() < minimumSize) {
                tags.add(new StringTag(toJSON(EMPTY_TEXT_TAG)));
            }

            // Rebalance the list to COMPOUND otherwise use the original type
            if (mixedListTagType) {
                return castNBTList(TagType.COMPOUND, tags);
            } else {
                return castNBTList(tagType, tags);
            }
        } else {
            ListTag<StringTag, String> list = new ListTag<>(TagType.STRING);

            // Add each element
            for (JsonElement jsonElement : jsonElements) {
                list.add(new StringTag(toJSON(jsonElement)));
            }

            // Ensure we reach the minimum size so pad it
            while (list.size() < minimumSize) {
                list.add(new StringTag(toJSON(EMPTY_TEXT_TAG)));
            }

            // Return our list
            return list;
        }
    }

    /**
     * Utility method to cast NBT tags to a type, if they are not the right tag and the target is compound it will wrap
     * them.
     *
     * @param target the target type of all the elements (if it is not compound and a type doesn't match it will throw
     *               an exception).
     * @param tags   the list of tags to rebalance.
     * @param <T>    the type of the tag.
     * @param <U>    the value of the tag.
     * @return a list tag of all the tags with the same type.
     */
    @SuppressWarnings("unchecked")
    private static <T extends Tag<U>, U> ListTag<T, U> castNBTList(TagType<T, U> target, List<Tag<?>> tags) {
        ListTag<T, U> listTag = new ListTag<>(target);

        // Loop through and wrap everything in a compound tag
        for (Tag<?> tag : tags) {
            if (tag.getType() != target) {
                if (tag.getType() != TagType.COMPOUND) {
                    CompoundTag holder = new CompoundTag();
                    holder.put("", tag);
                    tag = holder;
                } else {
                    throw new IllegalArgumentException("Unable to convert " + tag + " to " + target);
                }
            }

            // Add the tag which is now the right type
            listTag.add((T) tag);
        }
        return listTag;
    }
}
