package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.machines.common.blockentity.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MachineBlockEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntityEntry<FluidTankBlockEntity.Standard> FLUID_TANK = register("fluid_tank", FluidTankBlockEntity.Standard::new,
        MachineBlocks.FLUID_TANK);

    public static final BlockEntityEntry<FluidTankBlockEntity.Enhanced> PRESSURIZED_FLUID_TANK = register("pressurized_fluid_tank",
        FluidTankBlockEntity.Enhanced::new, MachineBlocks.PRESSURIZED_FLUID_TANK);

    public static final BlockEntityEntry<EnchanterBlockEntity> ENCHANTER = register("enchanter", EnchanterBlockEntity::new, MachineBlocks.ENCHANTER);

    public static final BlockEntityEntry<AlloySmelterBlockEntity.Primitive> PRIMITIVE_ALLOY_SMELTER = register("primitive_alloy_smelter", AlloySmelterBlockEntity.Primitive::new,
        MachineBlocks.PRIMITIVE_ALLOY_SMELTER);

    public static final BlockEntityEntry<AlloySmelterBlockEntity> ALLOY_SMELTER = register("alloy_smelter", AlloySmelterBlockEntity::new,
        MachineBlocks.ALLOY_SMELTER);

    public static final BlockEntityEntry<CreativePowerBlockEntity> CREATIVE_POWER = register("creative_power", CreativePowerBlockEntity::new,
        MachineBlocks.CREATIVE_POWER);
    public static final BlockEntityEntry<StirlingGeneratorBlockEntity> STIRLING_GENERATOR = register("stirling_generator",
        StirlingGeneratorBlockEntity::new, MachineBlocks.STIRLING_GENERATOR);
    public static final BlockEntityEntry<SagMillBlockEntity> SAG_MILL = register("sag_mill", SagMillBlockEntity::new, MachineBlocks.SAG_MILL);

    public static final BlockEntityEntry<SlicerBlockEntity> SLICE_AND_SPLICE = register("slice_and_splice", SlicerBlockEntity::new,
        MachineBlocks.SLICE_AND_SPLICE);

    public static final BlockEntityEntry<VatBlockEntity> THE_VAT = register("the_vat", VatBlockEntity::new, MachineBlocks.THE_VAT);
    public static final BlockEntityEntry<ImpulseHopperBlockEntity> IMPULSE_HOPPER = register("impulse_hopper", ImpulseHopperBlockEntity::new,
        MachineBlocks.IMPULSE_HOPPER);
    public static final BlockEntityEntry<VacuumChestBlockEntity> VACUUM_CHEST = register("vacuum_chest", VacuumChestBlockEntity::new,
        MachineBlocks.VACUUM_CHEST);
    public static final BlockEntityEntry<XPVacuumBlockEntity> XP_VACUUM = register("xp_vacuum", XPVacuumBlockEntity::new, MachineBlocks.XP_VACUUM);

    public static final BlockEntityEntry<CrafterBlockEntity> CRAFTER = register("crafter", CrafterBlockEntity::new, MachineBlocks.CRAFTER);

    public static final BlockEntityEntry<SoulBinderBlockEntity> SOUL_BINDER = register("soul_binder", SoulBinderBlockEntity::new, MachineBlocks.SOUL_BINDER);

    public static final BlockEntityEntry<PoweredSpawnerBlockEntity> POWERED_SPAWNER = register("powered_spanwer", PoweredSpawnerBlockEntity::new, MachineBlocks.POWERED_SPAWNER);

    @SafeVarargs
    private static <B extends BlockEntity> BlockEntityEntry<B> register(String name, BlockEntityBuilder.BlockEntityFactory<B> beFactory,
        NonNullSupplier<? extends Block>... blocks) {
        return REGISTRATE.blockEntity(name, beFactory).validBlocks(blocks).register();
    }

    public static void register() {}
}
