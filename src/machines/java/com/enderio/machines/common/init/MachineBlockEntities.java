package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.machines.common.blockentity.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Supplier;

public class MachineBlockEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntityEntry<FluidTankBlockEntity.Standard> FLUID_TANK = register("fluid_tank", FluidTankBlockEntity.Standard::new,
        MachineBlocks.FLUID_TANK);

    public static final BlockEntityEntry<FluidTankBlockEntity.Enhanced> PRESSURIZED_FLUID_TANK = register("pressurized_fluid_tank",
        FluidTankBlockEntity.Enhanced::new, MachineBlocks.PRESSURIZED_FLUID_TANK);

    public static final BlockEntityEntry<EnchanterBlockEntity> ENCHANTER = register("enchanter", EnchanterBlockEntity::new, MachineBlocks.ENCHANTER);

    public static final BlockEntityEntry<AlloySmelterBlockEntity.Furnace> SIMPLE_POWERED_FURNACE = register("simple_powered_furnace",
        AlloySmelterBlockEntity.Furnace::new, MachineBlocks.SIMPLE_POWERED_FURNACE);

    public static final BlockEntityEntry<AlloySmelterBlockEntity.Simple> SIMPLE_ALLOY_SMELTER = register("simple_alloy_smelter",
        AlloySmelterBlockEntity.Simple::new, MachineBlocks.SIMPLE_ALLOY_SMELTER);
    public static final BlockEntityEntry<AlloySmelterBlockEntity.Standard> ALLOY_SMELTER = register("alloy_smelter", AlloySmelterBlockEntity.Standard::new,
        MachineBlocks.ALLOY_SMELTER);
    public static final BlockEntityEntry<AlloySmelterBlockEntity.Enhanced> ENHANCED_ALLOY_SMELTER = register("enhanced_alloy_smelter",
        AlloySmelterBlockEntity.Enhanced::new, MachineBlocks.ENHANCED_ALLOY_SMELTER);

    public static final BlockEntityEntry<CreativePowerBlockEntity> CREATIVE_POWER = register("creative_power", CreativePowerBlockEntity::new,
        MachineBlocks.CREATIVE_POWER);

    public static final BlockEntityEntry<StirlingGeneratorBlockEntity.Simple> SIMPLE_STIRLING_GENERATOR = register("simple_stirling_generator",
        StirlingGeneratorBlockEntity.Simple::new, MachineBlocks.SIMPLE_STIRLING_GENERATOR);
    public static final BlockEntityEntry<StirlingGeneratorBlockEntity.Standard> STIRLING_GENERATOR = register("stirling_generator",
        StirlingGeneratorBlockEntity.Standard::new, MachineBlocks.STIRLING_GENERATOR);

    public static final BlockEntityEntry<SagMillBlockEntity.Simple> SIMPLE_SAG_MILL = register("simple_sag_mill", SagMillBlockEntity.Simple::new,
        MachineBlocks.SIMPLE_SAG_MILL);
    public static final BlockEntityEntry<SagMillBlockEntity.Standard> SAG_MILL = register("sag_mill", SagMillBlockEntity.Standard::new, MachineBlocks.SAG_MILL);
    public static final BlockEntityEntry<SagMillBlockEntity.Enhanced> ENHANCED_SAG_MILL = register("enhanced_sag_mill", SagMillBlockEntity.Enhanced::new,
        MachineBlocks.ENHANCED_SAG_MILL);

    public static final BlockEntityEntry<SlicerBlockEntity> SLICE_AND_SPLICE = register("slice_and_splice", SlicerBlockEntity::new,
        MachineBlocks.SLICE_AND_SPLICE);

    @SafeVarargs
    private static <B extends BlockEntity> BlockEntityEntry<B> register(String name, BlockEntityBuilder.BlockEntityFactory<B> beFactory,
        NonNullSupplier<? extends Block>... blocks) {
        return REGISTRATE.blockEntity(name, beFactory).validBlocks(blocks).register();
    }

    public static void register() {}
}
