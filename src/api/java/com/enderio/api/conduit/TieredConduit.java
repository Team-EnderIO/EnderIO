package com.enderio.api.conduit;

import com.enderio.api.UseOnly;
import com.enderio.api.misc.Vector2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

public abstract class TieredConduit<T extends IExtendedConduitData<T>> implements IConduitType<T> {
    private final ResourceLocation texture;
    private final ResourceLocation type;
    private final int tier;

    @UseOnly(LogicalSide.CLIENT) protected IClientConduitData<T> clientConduitData;

    /**
     * @param texture
     * @param type
     * @param tier    The tier of the conduit. For Energy this should be it's transfer rate to easily add and compare conduit strength
     */

    public TieredConduit(ResourceLocation texture, ResourceLocation type, int tier, ResourceLocation iconTexture, Vector2i iconTexturePos) {
        this.texture = texture;
        this.type = type;
        this.tier = tier;
        clientConduitData = new IClientConduitData.Simple<>(iconTexture, iconTexturePos);
    }

    @Override
    public ResourceLocation getTexture(T data) {
        return texture;
    }

    @Override
    public ResourceLocation getItemTexture() {
        return texture;
    }


    @Override
    public boolean canBeReplacedBy(IConduitType<?> other) {
        if (!(other instanceof TieredConduit<?> tieredOther))
            return false;

        if (type.equals(tieredOther.getType())) {
            return tier < tieredOther.getTier();
        }
        return false;
    }

    @Override
    public boolean canBeInSameBlock(IConduitType<?> other) {
        if (!(other instanceof TieredConduit<?> tieredOther))
            return true;
        // if they have the same type they can't be in the same block, their tier doesn't matter as canBeReplacedBy is checked first
        return !type.equals(tieredOther.getType());
    }

    public ResourceLocation getType() {
        return type;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public IClientConduitData<T> getClientData() {
        return clientConduitData;
    }
}
