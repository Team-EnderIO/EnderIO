package com.enderio.base.client.model.composite;

import com.enderio.base.EnderIO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.resources.ResourceLocation;

public record CompositeModelComponent(ResourceLocation model, Vector3f translation, boolean particleProvider) {
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("model", model.toString());

        if (translation != Vector3f.ZERO) {
            JsonArray array = new JsonArray();
            array.add(translation.x());
            array.add(translation.y());
            array.add(translation.z());
            json.add("translation", array);
        }

        if (particleProvider) {
            json.addProperty("particle_provider", true);
        }

        return json;
    }

    public Transformation getTransformation() {
        return new Transformation(translation, Quaternion.ONE, new Vector3f(1, 1, 1), Quaternion.ONE);
    }

    public static CompositeModelComponent fromJson(JsonObject object) {
        ResourceLocation model = new ResourceLocation(object.get("model").getAsString());

        Vector3f translation = Vector3f.ZERO;
        if (object.has("translation")) {
            JsonArray translationJson = object.get("translation").getAsJsonArray();
            if (translationJson.size() == 3) {
                translation = new Vector3f(translationJson.get(0).getAsFloat(), translationJson.get(1).getAsFloat(), translationJson.get(2).getAsFloat());
            } else {
                EnderIO.LOGGER.warning("Composite model component has invalid translation!");
            }
        }

        boolean particleProvider = false;
        if (object.has("particle_provider"))
            particleProvider = object.get("particle_provider").getAsBoolean();

        return new CompositeModelComponent(model, translation, particleProvider);
    }
}
