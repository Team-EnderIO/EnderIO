package com.enderio.base;

import com.enderio.core.CoreNBTKeys;

/**
 * Common NBT Keys.
 * This helps us keep consistency.
 * Names are purposely generic, but shouldn't conflict.
 * NOTE: If you have a highly specific NBT tag, store the keys in the class.
 * For example LootCapacitorData does this.
 */
public class EIONBTKeys extends CoreNBTKeys {

    // region Capability Serialized Names

    public static final String CAPACITOR_DATA = "CapacitorData";
    public static final String OWNER = "Owner";
    public static final String ENTITY_STORAGE = "EntityStorage";
    public static final String TOGGLE_STATE = "ToggleState";
    public static final String COORDINATE_SELECTION = "CoordinateSelection";
    public static final String DARK_STEEL_UPGRADEABLE = "DarkSteelUpgradable";

    public static final String PAINT = "Paint";
    public static final String PAINT_2 = "Paint2";

    // endregion

    // region Misc task

    public static final String ACTIVE = "Active";

    // endregion
}
