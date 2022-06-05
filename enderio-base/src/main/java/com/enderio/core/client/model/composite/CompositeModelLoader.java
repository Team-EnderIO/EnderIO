package com.enderio.core.client.model.composite;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

import java.util.ArrayList;
import java.util.List;

public class CompositeModelLoader implements IModelLoader<CompositeModelGeometry> {

    @Override
    public CompositeModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {

        // Load all model components
        JsonArray modelComponents = modelContents.get("components").getAsJsonArray();
        List<CompositeModelComponent> compositeModelComponents = new ArrayList<>(modelComponents.size());
        for (int i = 0; i < modelComponents.size(); i++) {
            compositeModelComponents.add(CompositeModelComponent.fromJson(modelComponents.get(i).getAsJsonObject()));
        }

        return new CompositeModelGeometry(compositeModelComponents);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }
}
