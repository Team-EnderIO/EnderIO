package com.enderio.conduits.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class ConduitGeometry implements IUnbakedGeometry<ConduitGeometry> {

    public ConduitGeometry() {
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState,
        ItemOverrides overrides, ResourceLocation modelLocation) {
        return new ConduitBlockModel();
    }

    public static class Loader implements IGeometryLoader<ConduitGeometry> {
        @Override
        public ConduitGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            return new ConduitGeometry();
        }
    }
}