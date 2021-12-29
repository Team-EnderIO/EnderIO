package com.enderio.machines.common.blockentity;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.block.MachineBlocks;
import com.enderio.registrate.Registrate;
import com.enderio.registrate.util.entry.BlockEntityEntry;

public class MachineBlockEntities {
    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final BlockEntityEntry<SimpleSmelterBlockEntity> SMELTER = REGISTRATE
        .blockEntity("smelter", SimpleSmelterBlockEntity::new)
        .validBlocks(MachineBlocks.SIMPLE_POWERED_FURNACE)
        .register();

    public static final BlockEntityEntry<FluidTankBlockEntity> FLUID_TANK = REGISTRATE
        .blockEntity("fluid_tank", FluidTankBlockEntity::new)
        .validBlocks(MachineBlocks.FLUID_TANK)
        .register();
    
    public static final BlockEntityEntry<EnchanterBlockEntity> ENCHANTER = REGISTRATE
        .blockEntity("enchanter", EnchanterBlockEntity::new)
        .validBlocks(MachineBlocks.ENCHANTER)
        .register();

    public static void register() {}
}
