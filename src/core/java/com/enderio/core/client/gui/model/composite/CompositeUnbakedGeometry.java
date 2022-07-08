package com.enderio.core.client.gui.model.composite;

import com.enderio.core.EnderCore;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompositeUnbakedGeometry implements IUnbakedGeometry<CompositeUnbakedGeometry> {

    private final List<CompositeModelComponent> components;

    public CompositeUnbakedGeometry(List<CompositeModelComponent> components) {
        this.components = components;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
        ItemOverrides overrides, ResourceLocation modelLocation) {
        // Get all dependencies and bake them in
        List<BakedModel> componentModels = new ArrayList<>(components.size());
        Supplier<TextureAtlasSprite> particleSupplier = null;
        for (CompositeModelComponent component : components) {
            if (component.model() == modelLocation) {
                EnderCore.LOGGER.warn("Model " + modelLocation + " referenced itself!");
                continue;
            }

            ModelState componentState = new ModelState() {
                @Override
                public Transformation getRotation() {
                    return modelTransform.getRotation().compose(component.getTransformation());
                }

                @Override
                public boolean isUvLocked() {
                    return modelTransform.isUvLocked();
                }
            };

            // Bake and add
            BakedModel baked = bakery.bake(component.model(), componentState, spriteGetter);
            componentModels.add(baked);

            // If we don't yet have the particle model, save
            if (particleSupplier == null && baked != null && component.particleProvider()) {
                particleSupplier = baked::getParticleIcon;
            } else if (particleSupplier != null && component.particleProvider()) {
                EnderCore.LOGGER.warn("Multiple particle suppliers found on model " + modelLocation);
            }
        }
        return new CompositeBakedModel(componentModels, particleSupplier);
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter,
        Set<Pair<String, String>> missingTextureErrors) {
        return components.stream()
            .filter(component -> !component.model().equals(new ResourceLocation(context.getModelName()))) // ignore self referencing
            .map(component -> modelGetter.apply(component.model()).getMaterials(modelGetter, missingTextureErrors))
            .flatMap(Collection::stream).toList();
    }
}
