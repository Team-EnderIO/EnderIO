package com.enderio.core.client.gui.model.composite;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import java.util.ArrayList;
import java.util.List;

public class CompositeGeometryLoader implements IGeometryLoader<CompositeUnbakedGeometry> {

    @Override
    public CompositeUnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
        // Load all model components
        JsonArray modelComponents = jsonObject.get("components").getAsJsonArray();
        List<CompositeModelComponent> compositeModelComponents = new ArrayList<>(modelComponents.size());
        for (int i = 0; i < modelComponents.size(); i++) {
            compositeModelComponents.add(CompositeModelComponent.fromJson(modelComponents.get(i).getAsJsonObject()));
        }

        return new CompositeUnbakedGeometry(compositeModelComponents);
    }
}
