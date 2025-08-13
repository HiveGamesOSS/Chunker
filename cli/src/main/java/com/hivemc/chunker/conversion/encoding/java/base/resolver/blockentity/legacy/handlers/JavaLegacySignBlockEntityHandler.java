package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.google.gson.JsonElement;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.sign.SignBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.JsonTextUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for Sign Block Entities.
 */
public class JavaLegacySignBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, SignBlockEntity> {
    public JavaLegacySignBlockEntityHandler() {
        super("Sign", SignBlockEntity.class, SignBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull SignBlockEntity value) {
        // Read text
        SignBlockEntity.SignFace face = value.getFront();
        List<JsonElement> lines = new ArrayList<>(4);
        for (int i = 1; i <= 4; i++) {
            lines.add(input.getOptionalValue("Text" + i, String.class)
                    .map(JsonTextUtil::fromJSON)
                    .orElse(JsonTextUtil.EMPTY_TEXT_TAG));
        }
        face.setLines(lines);
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull SignBlockEntity value) {
        SignBlockEntity.SignFace face = value.getFront();
        for (int i = 0; i < 4; i++) {
            output.put("Text" + (i + 1), JsonTextUtil.toJSON(face.getLines().size() > i ? face.getLines().get(i) : JsonTextUtil.EMPTY_TEXT_TAG));
        }
    }
}
