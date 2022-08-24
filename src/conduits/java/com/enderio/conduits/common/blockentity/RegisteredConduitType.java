package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.init.ConduitItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class RegisteredConduitType implements IConduitType {


    private final ResourceLocation texture;
    private final int activeLightLevel;

    public RegisteredConduitType(ResourceLocation texture) {
        this(texture, 0);
    }

    public RegisteredConduitType(ResourceLocation texture, int activeLightLevel) {
        this.texture = texture;
        this.activeLightLevel = activeLightLevel;
    }
    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public Item getConduitItem() {
        return ConduitItems.CONDUITS.get(this).get();
    }

    @Override
    public int getLightLevel(boolean isActive) {
        return isActive ? activeLightLevel : 0;
    }

}
