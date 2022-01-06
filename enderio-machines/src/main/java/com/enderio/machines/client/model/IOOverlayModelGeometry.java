package com.enderio.machines.client.model;

import com.enderio.machines.EIOMachines;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class IOOverlayModelGeometry implements IModelGeometry<IOOverlayModelGeometry> {
    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
        ItemOverrides overrides, ResourceLocation modelLocation) {
        return new IOOverlayBakedModel();
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter,
        Set<Pair<String, String>> missingTextureErrors) {
        return List.of(
            new Material(TextureAtlas.LOCATION_BLOCKS, EIOMachines.loc("block/overlay/disabled")),
            new Material(TextureAtlas.LOCATION_BLOCKS, EIOMachines.loc("block/overlay/pull")),
            new Material(TextureAtlas.LOCATION_BLOCKS, EIOMachines.loc("block/overlay/push")),
            new Material(TextureAtlas.LOCATION_BLOCKS, EIOMachines.loc("block/overlay/push_pull"))
        );
    }
}
