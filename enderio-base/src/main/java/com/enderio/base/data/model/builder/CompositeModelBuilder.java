package com.enderio.base.data.model.builder;

import com.enderio.base.EnderIO;
import com.enderio.base.client.model.composite.CompositeModelComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashSet;
import java.util.Set;

public class CompositeModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    private final Set<CompositeModelComponent> components = new HashSet<>();

    public static <T extends ModelBuilder<T>> CompositeModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new CompositeModelBuilder<>(parent, existingFileHelper);
    }

    protected CompositeModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(EnderIO.loc("composite_model"), parent, existingFileHelper);
    }

    public CompositeModelBuilder<T> component(ModelFile component) {
        return component(component, false);
    }

    public CompositeModelBuilder<T> component(ModelFile component, boolean particleProvider) {
        return this.component(component, Vector3f.ZERO, particleProvider);
    }

    public CompositeModelBuilder<T> component(ModelFile component, Vector3f translation) {
        return this.component(component, translation, false);
    }

    public CompositeModelBuilder<T> component(ModelFile component, Vector3f translation, boolean particleProvider) {
        return component(component, translation, Vector3f.ZERO, particleProvider);
    }

    public CompositeModelBuilder<T> component(ModelFile component, Vector3f translation, Vector3f rotation) {
        return component(component, translation, rotation, false);
    }

    public CompositeModelBuilder<T> component(ModelFile component, Vector3f translation, Vector3f rotation, boolean particleProvider) {
        components.add(new CompositeModelComponent(component.getUncheckedLocation(), translation, rotation, particleProvider));
        return this;
    }

    public CompositeModelBuilder<T> component(ResourceLocation component) {
        return component(component, false);
    }

    public CompositeModelBuilder<T> component(ResourceLocation component, boolean particleProvider) {
        return component(component, Vector3f.ZERO, particleProvider);
    }

    public CompositeModelBuilder<T> component(ResourceLocation component, Vector3f translation) {
        return component(component, translation, false);
    }

    public CompositeModelBuilder<T> component(ResourceLocation component, Vector3f translation, boolean particleProvider) {
        return component(component, translation, Vector3f.ZERO, particleProvider);
    }

    public CompositeModelBuilder<T> component(ResourceLocation component, Vector3f translation, Vector3f rotation) {
        return component(component, translation, rotation, false);
    }

    public CompositeModelBuilder<T> component(ResourceLocation component, Vector3f translation, Vector3f rotation, boolean particleProvider) {
        components.add(new CompositeModelComponent(component, translation, rotation, particleProvider));
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);

        JsonArray components = new JsonArray();
        for (CompositeModelComponent model : this.components) {
            components.add(model.toJson());
        }

        json.add("components", components);
        return json;
    }
}
