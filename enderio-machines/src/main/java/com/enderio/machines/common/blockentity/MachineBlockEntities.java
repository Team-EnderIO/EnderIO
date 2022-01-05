package com.enderio.machines.common.blockentity;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.block.MachineBlocks;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class MachineBlockEntities {
    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final BlockEntityEntry<FluidTankBlockEntity> FLUID_TANK = REGISTRATE
        .blockEntity("fluid_tank", FluidTankBlockEntity::new)
        .validBlocks(MachineBlocks.FLUID_TANK)
        .register();
    
    public static final BlockEntityEntry<EnchanterBlockEntity> ENCHANTER = REGISTRATE
        .blockEntity("enchanter", EnchanterBlockEntity::new)
        .validBlocks(MachineBlocks.ENCHANTER)
        .register();

    public static final BlockEntityEntry<AlloySmelterBlockEntity.Furnace> SIMPLE_POWERED_FURNACE = REGISTRATE
        .blockEntity("simple_powered_furnace", AlloySmelterBlockEntity.Furnace::new)
        .validBlocks(MachineBlocks.SIMPLE_POWERED_FURNACE)
        .register();

    public static final BlockEntityEntry<AlloySmelterBlockEntity.Simple> SIMPLE_ALLOY_SMELTER = REGISTRATE
        .blockEntity("simple_alloy_smelter", AlloySmelterBlockEntity.Simple::new)
        .validBlocks(MachineBlocks.SIMPLE_ALLOY_SMELTER)
        .register();

    public static final BlockEntityEntry<AlloySmelterBlockEntity.Standard> ALLOY_SMELTER = REGISTRATE
        .blockEntity("alloy_smelter", AlloySmelterBlockEntity.Standard::new)
        .validBlocks(MachineBlocks.ALLOY_SMELTER)
        .register();

    public static void register() {}
}
