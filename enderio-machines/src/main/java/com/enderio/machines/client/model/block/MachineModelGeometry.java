package com.enderio.machines.client.model.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.*;
import java.util.function.Function;

public class MachineModelGeometry implements IModelGeometry<MachineModelGeometry> {

    private final List<ResourceLocation> components;

    public MachineModelGeometry(List<ResourceLocation> components) {
        this.components = components;
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
        ItemOverrides overrides, ResourceLocation modelLocation) {
        // Get all dependencies and bake them in
        List<BakedModel> componentModels = new ArrayList<>(components.size());
        for (ResourceLocation component : components) {
            componentModels.add(bakery.bake(component, modelTransform, spriteGetter));
        }
        return new MachineBakedModel(componentModels);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter,
        Set<Pair<String, String>> missingTextureErrors) {
        // Return dependent textures
        List<Material> materials = new ArrayList<>();
        for (ResourceLocation component : components) {
            materials.addAll(modelGetter.apply(component).getMaterials(modelGetter, missingTextureErrors));
        }
        return materials;
    }
}
