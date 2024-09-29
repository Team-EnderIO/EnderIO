package com.enderio.base.client.paint.model;

import com.enderio.base.common.paint.PaintUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Function;

public class PaintedBlockGeometry implements IUnbakedGeometry<PaintedBlockGeometry> {
    private final Block reference;

    @Nullable
    private final Direction rotateItemTo;

    public PaintedBlockGeometry(Block reference, @Nullable Direction rotateItemTo) {
        this.reference = reference;
        this.rotateItemTo = rotateItemTo;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState,
        ItemOverrides overrides) {
        return new PaintedBlockModel(reference, rotateItemTo);
    }

    public static class Loader implements IGeometryLoader<PaintedBlockGeometry> {
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
}
