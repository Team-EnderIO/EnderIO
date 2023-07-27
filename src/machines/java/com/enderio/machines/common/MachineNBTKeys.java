package com.enderio.machines.common;

import com.enderio.base.EIONBTKeys;

/**
 * Common NBT Keys.
 * This helps us keep consistency.
 */
public class MachineNBTKeys extends EIONBTKeys {
    public static final String TASK = "Task";
    
    public static final String IO_CONFIG = "IoConfig";
    public static final String REDSTONE_CONTROL = "RedstoneControl";

    // TODO: The next two should maybe go back into AlloySmelterBlockEntity.
    public static final String MACHINE_MODE = "Mode";
    public static final String PROCESSED_INPUTS = "ProcessedInputs";

    // TODO: If the previous TODO is carried out, these should probably be moved to PrimitiveAlloySmelterBlockEntity
    public static final String BURN_TIME = "BurnTime";
    public static final String BURN_DURATION = "BurnDuration";
    public static final String RANGE = "Range";
    public static final String RANGE_VISIBLE = "RangeVisible";
}
