package com.enderio.core.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.fml.ModList;

import java.util.function.Function;

/**
 * A geometry loader to change the backed model depending on the mods loaded. Thanks to ThatGravyBoat from the Athena dev team for this idea and the code. It's really appreciated
 */
public class EitherModelLoader implements IGeometryLoader<EitherModelLoader.Unbaked> {

        @Override
    public Unbaked read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        final String id = GsonHelper.getAsString(json, "mod");
        final JsonElement element = GsonHelper.getAsJsonObject(json, ModList.get().isLoaded(id) ? "if" : "else");
        return new Unbaked(context.deserialize(element, BlockModel.class));
    }

    public record Unbaked(BlockModel model) implements IUnbakedGeometry<Unbaked> {

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation id) {
            return model.bake(baker, model, spriteGetter, modelState, id, true);
        }

        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
            model.resolveParents(modelGetter);
        }
    }
}

