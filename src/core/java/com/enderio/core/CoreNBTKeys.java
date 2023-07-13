package com.enderio.core;

import net.minecraft.world.item.BlockItem;

public class CoreNBTKeys {
    // region Standard Keys

    public static final String LEVEL = "Level";
    public static final String BLOCK_POS = "BlockPos";
    public static final String ITEM = "Item";
    public static final String ITEMS = "Items";
    public static final String FLUID = "Fluid";
    public static final String ENERGY = "Energy";
    public static final String BLOCK_ENTITY_TAG = BlockItem.BLOCK_ENTITY_TAG;

    // endregion

    // region Energy Storage

    public static final String ENERGY_STORED = "EnergyStored";
    public static final String ENERGY_MAX_STORED = "MaxEnergyStored";
    public static final String ENERGY_MAX_USE = "MaxEnergyUse";
    public static final String ENERGY_MAX_RECEIVE = "MaxEnergyUse";
    public static final String ENERGY_MAX_EXTRACT = "MaxEnergyUse";

    // endregion

    // region Sync

    public static final String SYNC_DATA_SLOT_INDEX = "DataSlotIndex";
    public static final String SYNC_DATA = "Data";

    // endregion
}
