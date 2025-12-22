package com.enderio.core.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.context.ContextMap;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jetbrains.annotations.Nullable;

/**
 * A geometry loader to change the backed model depending on the mods loaded. Thanks to ThatGravyBoat from the Athena dev team for this idea and the code. It's really appreciated
 */
// TODO: 1.21.4: kill in favour of conditional built-in resource packs.
public class EitherModelLoader implements UnbakedModelLoader<EitherModelLoader.Unbaked> {

    @Override
    public Unbaked read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        final String id = GsonHelper.getAsString(json, "mod");
        final JsonElement element = GsonHelper.getAsJsonObject(json, ModList.get().isLoaded(id) ? "if" : "else");
        return new Unbaked(context.deserialize(element, BlockModel.class));
    }

    public record Unbaked(BlockModel model) implements UnbakedModel {

        @Override
        public BakedModel bake(TextureSlots textureSlots, ModelBaker modelBaker, ModelState modelState, boolean b, boolean b1, ItemTransforms itemTransforms) {
            return model.bake(textureSlots, modelBaker, modelState, !b, b1, itemTransforms);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            model.resolveDependencies(resolver);
        }

        @Override
        public @Nullable Boolean getAmbientOcclusion() {
            return model.getAmbientOcclusion();
        }

        @Override
        public @Nullable GuiLight getGuiLight() {
            return model.getGuiLight();
        }

        @Override
        public @Nullable ItemTransforms getTransforms() {
            return model.getTransforms();
        }

        @Override
        public TextureSlots.Data getTextureSlots() {
            return model.getTextureSlots();
        }

        @Override
        public @Nullable UnbakedModel getParent() {
            return model.getParent();
        }

        @Override
        public BakedModel bake(TextureSlots textures, ModelBaker baker, ModelState modelState, boolean useAmbientOcclusion, boolean usesBlockLight,
            ItemTransforms itemTransforms, ContextMap additionalProperties) {
            return model.bake(textures, baker, modelState, useAmbientOcclusion, usesBlockLight, itemTransforms, additionalProperties);
        }

        @Override
        public void fillAdditionalProperties(ContextMap.Builder propertiesBuilder) {
            model.fillAdditionalProperties(propertiesBuilder);
        }
    }
}
