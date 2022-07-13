package com.enderio.decoration.client.model.painted;

import com.enderio.decoration.common.util.PaintUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PaintedBlockLoader implements IGeometryLoader<PaintedBlockGeometry> {
    @Override
    public PaintedBlockGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new PaintedBlockGeometry(PaintUtils.getBlockFromRL(jsonObject.get("reference").getAsString()), getItemTextureRotation(jsonObject));
    }

    @Nullable
    private static Direction getItemTextureRotation(JsonObject jsonObject) {
        if (jsonObject.has("item_texture_rotation")) {
            return Arrays.stream(Direction.values())
                .filter(dir -> dir.getName().equals(jsonObject.get("item_texture_rotation").getAsString()))
                .findFirst()
                .orElse(null);
        }
        return null;
    }
}
