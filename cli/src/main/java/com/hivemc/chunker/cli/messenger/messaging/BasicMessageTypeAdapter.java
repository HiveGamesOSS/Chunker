package com.hivemc.chunker.cli.messenger.messaging;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Locale;

/**
 * GSON adapter for turning BasicMessages to JSON and back.
 */
public class BasicMessageTypeAdapter implements JsonDeserializer<BasicMessage>, JsonSerializer<BasicMessage> {
    private static final Gson GSON = new Gson();

    @Override
    public BasicMessage deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        if (object.has("type")) {
            String nameString = object.get("type").getAsString();
            BasicMessage.BasicMessageType basicMessageType = BasicMessage.BasicMessageType.valueOf(nameString.toUpperCase(Locale.ROOT));

            // Deserialize
            return GSON.fromJson(json, basicMessageType.getMessageClass());
        }
        return null;
    }

    @Override
    public JsonElement serialize(BasicMessage src, Type type, JsonSerializationContext context) {
        return GSON.toJsonTree(src);
    }
}
