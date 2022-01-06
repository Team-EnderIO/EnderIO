package com.enderio.base.data.model;

import com.enderio.base.EnderIO;
import com.enderio.base.client.model.composite.CompositeModelComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
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

    public CompositeModelBuilder<T> component(ResourceLocation component) {
        components.add(new CompositeModelComponent(component, Vector3f.ZERO, false));
        return this;
    }

    public CompositeModelBuilder<T> component(ResourceLocation component, boolean particleProvider) {
        components.add(new CompositeModelComponent(component, Vector3f.ZERO, particleProvider));
        return this;
    }

    public CompositeModelBuilder<T> component(ResourceLocation component, Vector3f translation) {
        components.add(new CompositeModelComponent(component, translation, false));
        return this;
    }

    public CompositeModelBuilder<T> component(ResourceLocation component, Vector3f translation, boolean particleProvider) {
        components.add(new CompositeModelComponent(component, translation, particleProvider));
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
