package com.enderio.core.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.context.ContextMap;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jspecify.annotations.Nullable;

/**
 * A geometry loader to change the backed model depending on the mods loaded. Thanks to ThatGravyBoat from the Athena dev team for this idea and the code. It's really appreciated
 */
// TODO: 1.21.4: kill in favour of conditional built-in resource packs.
public class EitherModelLoader implements UnbakedModelLoader<EitherModelLoader.Unbaked> {

    @Override
    public Unbaked read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        final String id = GsonHelper.getAsString(json, "mod");
        final JsonElement element = GsonHelper.getAsJsonObject(json, ModList.get().isLoaded(id) ? "if" : "else");
        return new Unbaked(context.deserialize(element, CuboidModel.class));
    }

    public record Unbaked(CuboidModel model) implements UnbakedModel {
        @Override
        public @Nullable Boolean ambientOcclusion() {
            return model.ambientOcclusion();
        }

        @Override
        public @Nullable GuiLight guiLight() {
            return model.guiLight();
        }

        @Override
        public @Nullable ItemTransforms transforms() {
            return model.transforms();
        }

        @Override
        public TextureSlots.Data textureSlots() {
            return model.textureSlots();
        }

        @Override
        public @Nullable UnbakedGeometry geometry() {
            return model.geometry();
        }

        @Override
        public @Nullable Identifier parent() {
            return model.parent();
        }

        @Override
        public void fillAdditionalProperties(ContextMap.Builder propertiesBuilder) {
            model.fillAdditionalProperties(propertiesBuilder);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            model.resolveDependencies(resolver);
        }
    }
}
