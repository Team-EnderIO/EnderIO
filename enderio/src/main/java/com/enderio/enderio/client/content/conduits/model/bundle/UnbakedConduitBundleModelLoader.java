package com.enderio.enderio.client.content.conduits.model.bundle;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;

public class UnbakedConduitBundleModelLoader implements UnbakedModelLoader<UnbakedConduitBundleModelLoader.UnbakedConduitBundleModel> {

    public static UnbakedConduitBundleModelLoader INSTANCE = new UnbakedConduitBundleModelLoader();

    private UnbakedConduitBundleModelLoader() {
    }

    @Override
    public UnbakedConduitBundleModel read(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new UnbakedConduitBundleModel();
    }

    public class UnbakedConduitBundleModel implements UnbakedModel {

        @Override
        public BakedModel bake(TextureSlots textureSlots, ModelBaker modelBaker, ModelState modelState, boolean b, boolean b1, ItemTransforms itemTransforms) {
            return new ConduitBundleModel();
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            // TODO: Should we do this instead of additional models?
//            resolver.resolve(ConduitAdditionalModels.CONDUIT_CONNECTION);
//            resolver.resolve(ConduitAdditionalModels.CONDUIT_FACADE_OVERLAY);
//            resolver.resolve(ConduitAdditionalModels.CONDUIT_CONNECTOR);
//            resolver.resolve(ConduitAdditionalModels.CONDUIT_CORE);
//            resolver.resolve(ConduitAdditionalModels.BOX);
//            resolver.resolve(ConduitAdditionalModels.CONDUIT_IO_IN);
//            resolver.resolve(ConduitAdditionalModels.CONDUIT_IO_IN_OUT);
//            resolver.resolve(ConduitAdditionalModels.CONDUIT_IO_OUT);
//            resolver.resolve(ConduitAdditionalModels.CONDUIT_IO_REDSTONE);
        }
    }
}
