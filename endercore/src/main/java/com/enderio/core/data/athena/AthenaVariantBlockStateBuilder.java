package com.enderio.core.data.athena;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.IGeneratedBlockState;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom block state builder which supports Athena CTM properties.
 */
public class AthenaVariantBlockStateBuilder implements IGeneratedBlockState {
    private final VariantBlockStateBuilder variants;
    private ResourceLocation loader = ResourceLocation.parse("athena:ctm");
    private final Map<String, ResourceLocation> textures = new LinkedHashMap<>();
    private String renderType;
    private Integer tint;
    @Nullable
    private AthenaConnection connectTo = null;

    public AthenaVariantBlockStateBuilder(VariantBlockStateBuilder variants) {
        this.variants = variants;
    }

    public VariantBlockStateBuilder variants() {
        return variants;
    }

    public AthenaVariantBlockStateBuilder loader(ResourceLocation loader) {
        this.loader = loader;
        return this;
    }

    public AthenaVariantBlockStateBuilder texture(String name, ResourceLocation texture) {
        textures.put(name, texture);
        return this;
    }

    public AthenaVariantBlockStateBuilder renderType(String renderType) {
        this.renderType = renderType;
        return this;
    }

    public AthenaVariantBlockStateBuilder tint(int tint) {
        this.tint = tint;
        return this;
    }

    public AthenaVariantBlockStateBuilder connection(@Nullable AthenaConnection connectTo) {
        this.connectTo = connectTo;
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = variants.toJson();
        json.addProperty("athena:loader", loader.toString());
        JsonObject ctmTextures = new JsonObject();
        textures.forEach((name, texture) -> ctmTextures.addProperty(name, texture.toString()));
        json.add("ctm_textures", ctmTextures);
        if (renderType != null) {
            json.addProperty("render_type", renderType);
        }
        if (tint != null) {
            json.addProperty("tint", tint);
        }
        if (connectTo != null) {
            json.add("connect_to", connectTo.toJson());

        }
        return json;
    }
}
