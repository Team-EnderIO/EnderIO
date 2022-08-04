package com.enderio.conduits.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitType;
import net.minecraft.resources.ResourceLocation;

public enum ConduitType implements IConduitType {
    POWER(EnderIO.loc("blocks/conduits/power"));

    ConduitType(ResourceLocation texture) {
        this.texture = texture;
    }
    private final ResourceLocation texture;

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

}
