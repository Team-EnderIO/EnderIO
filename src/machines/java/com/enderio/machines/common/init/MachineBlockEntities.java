package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.machines.common.blockentity.*;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorTier;
import com.enderio.machines.common.blockentity.solar.SolarPanelBlockEntity;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.google.common.collect.ImmutableMap;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MachineBlockEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntityEntry<FluidTankBlockEntity.Standard> FLUID_TANK = register("fluid_tank", FluidTankBlockEntity.Standard::new,
        MachineBlocks.FLUID_TANK);

    public static final BlockEntityEntry<FluidTankBlockEntity.Enhanced> PRESSURIZED_FLUID_TANK = register("pressurized_fluid_tank",
        FluidTankBlockEntity.Enhanced::new, MachineBlocks.PRESSURIZED_FLUID_TANK);

    public static final BlockEntityEntry<EnchanterBlockEntity> ENCHANTER = register("enchanter", EnchanterBlockEntity::new, MachineBlocks.ENCHANTER);

    public static final BlockEntityEntry<PrimitiveAlloySmelterBlockEntity> PRIMITIVE_ALLOY_SMELTER = register("primitive_alloy_smelter", PrimitiveAlloySmelterBlockEntity::new,
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

    public static final BlockEntityEntry<ImpulseHopperBlockEntity> IMPULSE_HOPPER = register("impulse_hopper", ImpulseHopperBlockEntity::new,
        MachineBlocks.IMPULSE_HOPPER);
    public static final BlockEntityEntry<VacuumChestBlockEntity> VACUUM_CHEST = register("vacuum_chest", VacuumChestBlockEntity::new,
        MachineBlocks.VACUUM_CHEST);
    public static final BlockEntityEntry<XPVacuumBlockEntity> XP_VACUUM = register("xp_vacuum", XPVacuumBlockEntity::new, MachineBlocks.XP_VACUUM);

    public static final BlockEntityEntry<CrafterBlockEntity> CRAFTER = register("crafter", CrafterBlockEntity::new, MachineBlocks.CRAFTER);

    public static final BlockEntityEntry<SoulBinderBlockEntity> SOUL_BINDER = register("soul_binder", SoulBinderBlockEntity::new, MachineBlocks.SOUL_BINDER);

    public static final BlockEntityEntry<WiredChargerBlockEntity> WIRED_CHARGER = register("wired_charger",
       WiredChargerBlockEntity::new, MachineBlocks.WIRED_CHARGER);

    public static final BlockEntityEntry<PaintingMachineBlockEntity> PAINTING_MACHINE = register("painting_machine", PaintingMachineBlockEntity::new, MachineBlocks.PAINTING_MACHINE);

    public static final BlockEntityEntry<PoweredSpawnerBlockEntity> POWERED_SPAWNER = register("powered_spawner", PoweredSpawnerBlockEntity::new, MachineBlocks.POWERED_SPAWNER);

    public static final Map<SolarPanelTier, BlockEntityEntry<SolarPanelBlockEntity>> SOLAR_PANELS = Util.make(() -> {
       Map<SolarPanelTier, BlockEntityEntry<SolarPanelBlockEntity>> map = new HashMap<>();
       for (SolarPanelTier tier : SolarPanelTier.values()) {
           map.put(tier, register(tier.name().toLowerCase(Locale.ROOT) + "_photovoltaic_cell", (type, worldPosition,
               blockState) -> new SolarPanelBlockEntity(type, worldPosition, blockState, tier), () -> MachineBlocks.SOLAR_PANELS.get(tier).get()));
       }
       return ImmutableMap.copyOf(map);
    });
    public static final Map<CapacitorTier, BlockEntityEntry<CapacitorBankBlockEntity>> CAPACITOR_BANKS = Util.make(() -> {
       Map<CapacitorTier, BlockEntityEntry<CapacitorBankBlockEntity>> map = new HashMap<>();
       for (CapacitorTier tier : CapacitorTier.values()) {
           map.put(tier, register(tier.name().toLowerCase(Locale.ROOT) + "_capacitor_bank", (type, worldPosition,
               blockState) -> new CapacitorBankBlockEntity(type, worldPosition, blockState, tier), () -> MachineBlocks.CAPACITOR_BANKS.get(tier).get()));
       }
       return ImmutableMap.copyOf(map);
    });

    @SafeVarargs
    private static <B extends BlockEntity> BlockEntityEntry<B> register(String name, BlockEntityBuilder.BlockEntityFactory<B> beFactory,
        NonNullSupplier<? extends Block>... blocks) {
        return REGISTRATE.blockEntity(name, beFactory).validBlocks(blocks).register();
    }

    public static void register() {}
}
