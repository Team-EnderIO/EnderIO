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

    public static final BlockEntityEntry<AlloySmelterBlockEntity.SimpleSmelter> SIMPLE_POWERED_FURNACE = REGISTRATE
        .blockEntity("simple_powered_furnace", AlloySmelterBlockEntity.SimpleSmelter::new)
        .validBlocks(MachineBlocks.SIMPLE_POWERED_FURNACE)
        .register();

    public static final BlockEntityEntry<AlloySmelterBlockEntity.SimpleAlloySmelter> SIMPLE_ALLOY_SMELTER = REGISTRATE
        .blockEntity("simple_alloy_smelter", AlloySmelterBlockEntity.SimpleAlloySmelter::new)
        .validBlocks(MachineBlocks.SIMPLE_ALLOY_SMELTER)
        .register();

    public static final BlockEntityEntry<AlloySmelterBlockEntity.AlloySmelter> ALLOY_SMELTER = REGISTRATE
        .blockEntity("alloy_smelter", AlloySmelterBlockEntity.AlloySmelter::new)
        .validBlocks(MachineBlocks.ALLOY_SMELTER)
        .register();

    public static void register() {}
}
