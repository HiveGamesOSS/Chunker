package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.google.gson.JsonElement;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.java.util.FontUtil;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.sign.HangingSignBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.sign.SignBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.JsonTextUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for signs.
 */
public class BedrockSignBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, SignBlockEntity> {
    public static final int SIGN_MAX_WIDTH = 88;
    public static final int HANGING_SIGN_MAX_WIDTH = 60;

    public BedrockSignBlockEntityHandler() {
        super("Sign", SignBlockEntity.class, SignBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull SignBlockEntity value) {
        value.setWaxed(input.getByte("IsWaxed", (byte) 0) == (byte) 1);

        // Use FrontText for the front face, otherwise default to the root
        readSignFace(input.getCompound("FrontText", input), value.getFront(), value instanceof HangingSignBlockEntity);

        // Only read back text if it's present
        CompoundTag backText = input.getCompound("BackText");
        if (backText != null) {
            readSignFace(backText, value.getBack(), value instanceof HangingSignBlockEntity);
        }
    }

    protected void readSignFace(@NotNull CompoundTag input, SignBlockEntity.SignFace face, boolean hanging) {
        // Read text
        if (input.contains("Text")) {
            String[] textLines = input.getString("Text", "").split("\n");

            // Split any long lines where possible
            textLines = FontUtil.split(hanging ? HANGING_SIGN_MAX_WIDTH : SIGN_MAX_WIDTH, 4, textLines);

            // Turn the lines into JSON
            List<JsonElement> lines = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                JsonElement text = textLines.length > i ? JsonTextUtil.fromText(textLines[i]) : JsonTextUtil.EMPTY_TEXT_TAG;
                lines.add(text);
            }
            face.setLines(lines);
        } else {
            List<JsonElement> lines = new ArrayList<>(4);
            for (int i = 1; i <= 4; i++) {
                lines.add(input.getOptionalValue("Text" + i, String.class)
                        .map(JsonTextUtil::fromText)
                        .orElse(JsonTextUtil.EMPTY_TEXT_TAG));
            }
            face.setLines(lines);
        }

        // Read lit
        face.setLit(input.getByte("IgnoreLighting", (byte) 0) == (byte) 1);

        // Read color
        face.setColor(input.getInt("SignTextColor", 0xff000000));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull SignBlockEntity value) {
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 19, 80)) {
            output.put("IsWaxed", value.isWaxed() ? (byte) 1 : (byte) 0);

            // Write faces
            writeSignFace(resolvers, output.getOrCreateCompound("FrontText"), value.getFront());
            writeSignFace(resolvers, output.getOrCreateCompound("BackText"), value.getBack());
        } else {
            // Write text using root
            writeSignFace(resolvers, output, value.getFront());
        }
    }

    protected void writeSignFace(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, SignBlockEntity.SignFace face) {
        // Write text (do a contains check first in the case the original text has been retained)
        if (!output.contains("Text")) {
            StringBuilder text = new StringBuilder();
            for (JsonElement line : face.getLines()) {
                text.append(JsonTextUtil.toLegacy(line, true));
                text.append("\n");
            }
            output.put("Text", text.toString());
        }

        // Write lit
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 17, 0)) {
            output.put("IgnoreLighting", face.isLit() ? (byte) 1 : (byte) 0);

            // Enable fix for glowing text
            if (resolvers.dataVersion().getVersion().isLessThanOrEqual(1, 19, 70)) {
                output.put("TextIgnoreLegacyBugResolved", (byte) 1);
            }
        }

        // Write color
        output.put("SignTextColor", face.getColor());
    }
}
