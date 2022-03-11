package com.enderio.machines.common.init;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.blockentity.EnchanterBlockEntity;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.blockentity.VacuumChestBlockEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class MachineBlockEntities {
    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final BlockEntityEntry<FluidTankBlockEntity.Standard> FLUID_TANK = REGISTRATE
        .blockEntity("fluid_tank", FluidTankBlockEntity.Standard::new)
        .validBlocks(MachineBlocks.FLUID_TANK)
        .register();

    public static final BlockEntityEntry<FluidTankBlockEntity.Enhanced> PRESSURIZED_FLUID_TANK = REGISTRATE
        .blockEntity("pressurized_fluid_tank", FluidTankBlockEntity.Enhanced::new)
        .validBlocks(MachineBlocks.PRESSURIZED_FLUID_TANK)
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

    public static final BlockEntityEntry<AlloySmelterBlockEntity.Enhanced> ENHANCED_ALLOY_SMELTER = REGISTRATE
        .blockEntity("enhanced_alloy_smelter", AlloySmelterBlockEntity.Enhanced::new)
        .validBlocks(MachineBlocks.ENHANCED_ALLOY_SMELTER)
        .register();
    
    public static final BlockEntityEntry<VacuumChestBlockEntity> VACUUM_CHEST = REGISTRATE
            .blockEntity("vacuum_chest", VacuumChestBlockEntity::new)
            .validBlocks(MachineBlocks.VACUUM_CHEST)
            .register();

    public static void register() {}
}
