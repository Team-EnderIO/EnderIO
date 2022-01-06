package com.enderio.machines.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

public class IOOverlayModelLoader implements IModelLoader<IOOverlayModelGeometry> {
    @Override
    public IOOverlayModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return new IOOverlayModelGeometry();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }
}
