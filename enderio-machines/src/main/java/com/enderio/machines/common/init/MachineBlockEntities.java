package com.enderio.machines.common.init;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.blockentity.*;
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

    public static final BlockEntityEntry<CreativePowerBlockEntity> CREATIVE_POWER = REGISTRATE
        .blockEntity("creative_power", CreativePowerBlockEntity::new)
        .validBlocks(MachineBlocks.CREATIVE_POWER)
        .register();

    public static final BlockEntityEntry<StirlingGeneratorBlockEntity.Simple> SIMPLE_STIRLING_GENERATOR = REGISTRATE
        .blockEntity("simple_stirling_generator", StirlingGeneratorBlockEntity.Simple::new)
        .validBlocks(MachineBlocks.SIMPLE_STIRLING_GENERATOR)
        .register();

    public static final BlockEntityEntry<StirlingGeneratorBlockEntity.Standard> STIRLING_GENERATOR = REGISTRATE
        .blockEntity("stirling_generator", StirlingGeneratorBlockEntity.Standard::new)
        .validBlocks(MachineBlocks.STIRLING_GENERATOR)
        .register();

    public static final BlockEntityEntry<SagMillBlockEntity.Simple> SIMPLE_SAG_MILL = REGISTRATE
        .blockEntity("simple_sag_mill", SagMillBlockEntity.Simple::new)
        .validBlocks(MachineBlocks.SIMPLE_SAG_MILL)
        .register();

    public static final BlockEntityEntry<SagMillBlockEntity.Standard> SAG_MILL = REGISTRATE
        .blockEntity("sag_mill", SagMillBlockEntity.Standard::new)
        .validBlocks(MachineBlocks.SAG_MILL)
        .register();

    public static final BlockEntityEntry<SagMillBlockEntity.Enhanced> ENHANCED_SAG_MILL = REGISTRATE
        .blockEntity("enhanced_sag_mill", SagMillBlockEntity.Enhanced::new)
        .validBlocks(MachineBlocks.ENHANCED_SAG_MILL)
        .register();

    public static final BlockEntityEntry<SlicerBlockEntity> SLICE_AND_SPLICE = REGISTRATE
        .blockEntity("slice_and_splice", SlicerBlockEntity::new)
        .validBlocks(MachineBlocks.SLICE_AND_SPLICE)
        .register();

    public static void classload() {}
}
