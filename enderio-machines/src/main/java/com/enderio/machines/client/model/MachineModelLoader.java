package com.enderio.machines.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

import java.util.ArrayList;
import java.util.List;

public class MachineModelLoader implements IModelLoader<MachineModelGeometry> {

    @Override
    public MachineModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {

        // Load all model components
        JsonArray modelComponents = modelContents.get("components").getAsJsonArray();
        List<ResourceLocation> modelRLs = new ArrayList<>(modelComponents.size());
        for (int i = 0; i < modelComponents.size(); i++) {
            modelRLs.add(new ResourceLocation(modelComponents.get(i).getAsString()));
        }

        return new MachineModelGeometry(modelRLs);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }
}
