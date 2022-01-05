package com.enderio.machines.data.model;

import com.enderio.machines.EIOMachines;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashSet;
import java.util.Set;

public class MachineModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    private final Set<ResourceLocation> components = new HashSet<>();

    public static <T extends ModelBuilder<T>> MachineModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new MachineModelBuilder<>(parent, existingFileHelper);
    }

    protected MachineModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(EIOMachines.loc("machine_model"), parent, existingFileHelper);
    }

    public MachineModelBuilder<T> component(ResourceLocation component) {
        components.add(component);
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);

        JsonArray components = new JsonArray();
        for (ResourceLocation model : this.components) {
            components.add(model.toString());
        }

        json.add("components", components);
        return json;
    }
}
