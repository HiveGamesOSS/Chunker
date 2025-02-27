package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.google.gson.JsonElement;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.sign.SignBlockEntity;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import com.hivemc.chunker.util.JsonTextUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for Sign Block Entities.
 */
public class JavaSignBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, SignBlockEntity> {
    public JavaSignBlockEntityHandler() {
        super("minecraft:sign", SignBlockEntity.class, SignBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull SignBlockEntity value) {
        value.setWaxed(input.getByte("is_waxed", (byte) 0) == (byte) 1);

        // Use FrontText for the front face, otherwise default to the root
        readSignFace(input.getCompound("front_text", input), value.getFront());

        // Only read back text if it's present
        CompoundTag backText = input.getCompound("back_text");
        if (backText != null) {
            readSignFace(backText, value.getBack());
        }
    }

    protected void readSignFace(@NotNull CompoundTag input, SignBlockEntity.SignFace face) {
        // Read text
        if (input.contains("messages")) {
            ListTag<?, ?> textLines = input.get("messages", ListTag.class);
            if (textLines != null) {
                List<JsonElement> lines = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    JsonElement text = textLines.size() > i ? JsonTextUtil.fromNBT(textLines.get(i)) : JsonTextUtil.EMPTY_TEXT_TAG;
                    lines.add(text);
                }
                face.setLines(lines);
            }
        } else {
            List<JsonElement> lines = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                lines.add(input.getOptional("Text" + i, Tag.class)
                        .map(JsonTextUtil::fromNBT)
                        .orElse(JsonTextUtil.EMPTY_TEXT_TAG));
            }
            face.setLines(lines);
        }

        // Read lit
        if (input.contains("has_glowing_text")) {
            face.setLit(input.getByte("has_glowing_text", (byte) 0) == (byte) 1);
        } else {
            face.setLit(input.getByte("GlowingText", (byte) 0) == (byte) 1);
        }

        // Read color
        if (input.contains("has_glowing_text")) {
            face.setColor(JsonTextUtil.COLOR_TO_BEDROCK_HEX.getOrDefault(input.getString("color", "black"), 0xff000000));
        } else {
            face.setColor(JsonTextUtil.COLOR_TO_BEDROCK_HEX.getOrDefault(input.getString("Color", "black"), 0xff000000));
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull SignBlockEntity value) {
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 0)) {
            output.put("is_waxed", value.isWaxed() ? (byte) 1 : (byte) 0);

            // Write faces
            writeSignFace(resolvers, output.getOrCreateCompound("front_text"), value.getFront());
            writeSignFace(resolvers, output.getOrCreateCompound("back_text"), value.getBack());
        } else {
            // Write text using root
            writeSignFace(resolvers, output, value.getFront());
        }
    }

    protected void writeSignFace(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, SignBlockEntity.SignFace face) {
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 0)) {
            output.put("messages", JsonTextUtil.toNBT(face.getLines(), 4, resolvers.dataVersion()));

            // Write lit
            output.put("has_glowing_text", face.isLit() ? (byte) 1 : (byte) 0);

            // Write color
            output.put("color", JsonTextUtil.COLOR_TO_BEDROCK_HEX.inverse().getOrDefault(face.getColor(), "black"));
        } else {
            for (int i = 0; i < 4; i++) {
                output.put("Text" + (i + 1), JsonTextUtil.toNBT(
                        face.getLines().size() > i ? face.getLines().get(i) : JsonTextUtil.EMPTY_TEXT_TAG,
                        resolvers.dataVersion()
                ));
            }

            // Write lit
            if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 17, 0)) {
                output.put("GlowingText", face.isLit() ? (byte) 1 : (byte) 0);
            }

            // Write color
            output.put("Color", JsonTextUtil.COLOR_TO_BEDROCK_HEX.inverse().getOrDefault(face.getColor(), "black"));
        }
    }
}
