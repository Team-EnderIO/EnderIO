package com.enderio.machines.common;

import com.enderio.base.EIONBTKeys;

/**
 * Common NBT Keys.
 * This helps us keep consistency.
 */
public class MachineNBTKeys extends EIONBTKeys {

    // TODO: The next two should maybe go back into AlloySmelterBlockEntity.
    public static final String MACHINE_MODE = "Mode";
    public static final String PROCESSED_INPUTS = "ProcessedInputs";

    public static final String REDSTONE_CONTROL = "RedstoneControl";
    public static final String IO_CONFIG = "IOConfig";
    public static final String ACTION_RANGE = "ActionRange";
    public static final String IS_RANGE_VISIBLE = "IsRangeVisible";

    public static final String BURN_TIME = "BurnTime";
    public static final String BURN_DURATION = "BurnDuration";
}
