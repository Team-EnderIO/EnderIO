package com.enderio.api.conduit;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public abstract class TieredConduit<T extends ConduitData<T>> extends ConduitType<T> {
    private final ResourceLocation type;
    private final int tier;
    protected final ResourceLocation tierName;

    /**
     * @param type
     * @param tier    The tier of the conduit. For Energy this should be it's transfer rate to easily add and compare conduit strength
     */
    public TieredConduit(ResourceLocation type, ResourceLocation tierName, int tier) {
        this.type = type;
        this.tier = tier;
        this.tierName = tierName;
    }

    @Override
    public boolean canBeReplacedBy(ConduitType<?> other) {
        if (!(other instanceof TieredConduit<?> tieredOther)) {
            return false;
        }

        if (type.equals(tieredOther.getType())) {
            return tier < tieredOther.getTier();
        }

        return false;
    }

    @Override
    public boolean canBeInSameBlock(ConduitType<?> other) {
        if (!(other instanceof TieredConduit<?> tieredOther)) {
            return true;
        }

        // if they have the same type they can't be in the same block, their tier doesn't matter as canBeReplacedBy is checked first
        return !type.equals(tieredOther.getType());
    }

    @Override
    public Item getConduitItem() {
        return BuiltInRegistries.ITEM.get(tierName);
    }

    public ResourceLocation getType() {
        return type;
    }

    public int getTier() {
        return tier;
    }
}
