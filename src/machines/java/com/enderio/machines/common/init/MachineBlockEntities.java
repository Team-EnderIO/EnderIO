package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.machines.client.rendering.blockentity.FluidTankBER;
import com.enderio.machines.client.rendering.blockentity.XPObeliskBER;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.blockentity.CrafterBlockEntity;
import com.enderio.machines.common.blockentity.CreativePowerBlockEntity;
import com.enderio.machines.common.blockentity.DrainBlockEntity;
import com.enderio.machines.common.blockentity.EnchanterBlockEntity;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.blockentity.ImpulseHopperBlockEntity;
import com.enderio.machines.common.blockentity.PaintingMachineBlockEntity;
import com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity;
import com.enderio.machines.common.blockentity.PrimitiveAlloySmelterBlockEntity;
import com.enderio.machines.common.blockentity.SagMillBlockEntity;
import com.enderio.machines.common.blockentity.SlicerBlockEntity;
import com.enderio.machines.common.blockentity.SoulBinderBlockEntity;
import com.enderio.machines.common.blockentity.SoulEngineBlockEntity;
import com.enderio.machines.common.blockentity.StirlingGeneratorBlockEntity;
import com.enderio.machines.common.blockentity.TravelAnchorBlockEntity;
import com.enderio.machines.common.blockentity.VacuumChestBlockEntity;
import com.enderio.machines.common.blockentity.WiredChargerBlockEntity;
import com.enderio.machines.common.blockentity.XPObeliskBlockEntity;
import com.enderio.machines.common.blockentity.XPVacuumBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorTier;
import com.enderio.machines.common.blockentity.solar.SolarPanelBlockEntity;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.enderio.regilite.registry.BlockEntityRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class MachineBlockEntities {
    private static final BlockEntityRegistry BLOCK_ENTITY_REGISTRY = BlockEntityRegistry.create(EnderIO.MODID);

    public static final RegiliteBlockEntity<FluidTankBlockEntity.Standard> FLUID_TANK =
        register("fluid_tank", FluidTankBlockEntity.Standard::new, MachineBlocks.FLUID_TANK)
            .setRenderer(() -> FluidTankBER::new);

    public static final RegiliteBlockEntity<FluidTankBlockEntity.Enhanced> PRESSURIZED_FLUID_TANK =
        register("pressurized_fluid_tank", FluidTankBlockEntity.Enhanced::new, MachineBlocks.PRESSURIZED_FLUID_TANK)
            .setRenderer(() -> FluidTankBER::new);

    public static final RegiliteBlockEntity<EnchanterBlockEntity> ENCHANTER =
        register("enchanter", EnchanterBlockEntity::new, MachineBlocks.ENCHANTER);

    public static final RegiliteBlockEntity<PrimitiveAlloySmelterBlockEntity> PRIMITIVE_ALLOY_SMELTER =
        register("primitive_alloy_smelter", PrimitiveAlloySmelterBlockEntity::new, MachineBlocks.PRIMITIVE_ALLOY_SMELTER);

    public static final RegiliteBlockEntity<AlloySmelterBlockEntity> ALLOY_SMELTER =
        register("alloy_smelter", AlloySmelterBlockEntity::factory, MachineBlocks.ALLOY_SMELTER);

    public static final RegiliteBlockEntity<CreativePowerBlockEntity> CREATIVE_POWER =
        register("creative_power", CreativePowerBlockEntity::new, MachineBlocks.CREATIVE_POWER);

    public static final RegiliteBlockEntity<StirlingGeneratorBlockEntity> STIRLING_GENERATOR =
        register("stirling_generator", StirlingGeneratorBlockEntity::new, MachineBlocks.STIRLING_GENERATOR);

    public static final RegiliteBlockEntity<SagMillBlockEntity> SAG_MILL =
        register("sag_mill", SagMillBlockEntity::new, MachineBlocks.SAG_MILL);

    public static final RegiliteBlockEntity<SlicerBlockEntity> SLICE_AND_SPLICE =
        register("slice_and_splice", SlicerBlockEntity::new, MachineBlocks.SLICE_AND_SPLICE);

    public static final RegiliteBlockEntity<ImpulseHopperBlockEntity> IMPULSE_HOPPER =
        register("impulse_hopper", ImpulseHopperBlockEntity::new, MachineBlocks.IMPULSE_HOPPER);

    public static final RegiliteBlockEntity<VacuumChestBlockEntity> VACUUM_CHEST =
        register("vacuum_chest", VacuumChestBlockEntity::new, MachineBlocks.VACUUM_CHEST);

    public static final RegiliteBlockEntity<XPVacuumBlockEntity> XP_VACUUM =
        register("xp_vacuum", XPVacuumBlockEntity::new, MachineBlocks.XP_VACUUM);

    public static final RegiliteBlockEntity<TravelAnchorBlockEntity> TRAVEL_ANCHOR =
        register("travel_anchor", TravelAnchorBlockEntity::new, MachineBlocks.TRAVEL_ANCHOR);

    public static final RegiliteBlockEntity<CrafterBlockEntity> CRAFTER =
        register("crafter", CrafterBlockEntity::new, MachineBlocks.CRAFTER);

    public static final RegiliteBlockEntity<DrainBlockEntity> DRAIN =
        register("drain", DrainBlockEntity::new, MachineBlocks.DRAIN);

    public static final RegiliteBlockEntity<SoulBinderBlockEntity> SOUL_BINDER =
        register("soul_binder", SoulBinderBlockEntity::new, MachineBlocks.SOUL_BINDER);

    public static final RegiliteBlockEntity<WiredChargerBlockEntity> WIRED_CHARGER =
        register("wired_charger", WiredChargerBlockEntity::new, MachineBlocks.WIRED_CHARGER);

    public static final RegiliteBlockEntity<PaintingMachineBlockEntity> PAINTING_MACHINE =
        register("painting_machine", PaintingMachineBlockEntity::new, MachineBlocks.PAINTING_MACHINE);

    public static final RegiliteBlockEntity<PoweredSpawnerBlockEntity> POWERED_SPAWNER =
        register("powered_spawner", PoweredSpawnerBlockEntity::new, MachineBlocks.POWERED_SPAWNER);

    public static final Map<SolarPanelTier, RegiliteBlockEntity<SolarPanelBlockEntity>> SOLAR_PANELS = Util.make(() -> {
       Map<SolarPanelTier, RegiliteBlockEntity<SolarPanelBlockEntity>> map = new HashMap<>();
       for (SolarPanelTier tier : SolarPanelTier.values()) {
           map.put(tier, register(tier.name().toLowerCase(Locale.ROOT) + "_photovoltaic_cell", (worldPosition,
               blockState) -> new SolarPanelBlockEntity(worldPosition, blockState, tier), () -> MachineBlocks.SOLAR_PANELS.get(tier).get()));
       }
       return ImmutableMap.copyOf(map);
    });

    public static final Map<CapacitorTier, RegiliteBlockEntity<CapacitorBankBlockEntity>> CAPACITOR_BANKS = Util.make(() -> {
       Map<CapacitorTier, RegiliteBlockEntity<CapacitorBankBlockEntity>> map = new HashMap<>();
       for (CapacitorTier tier : CapacitorTier.values()) {
           map.put(tier, register(tier.name().toLowerCase(Locale.ROOT) + "_capacitor_bank", (worldPosition,
               blockState) -> new CapacitorBankBlockEntity(worldPosition, blockState, tier), () -> MachineBlocks.CAPACITOR_BANKS.get(tier).get()));
       }
       return ImmutableMap.copyOf(map);
    });

    public static final RegiliteBlockEntity<SoulEngineBlockEntity> SOUL_ENGINE =
        register("soul_engine", SoulEngineBlockEntity::new, MachineBlocks.SOUL_ENGINE);

    public static final RegiliteBlockEntity<XPObeliskBlockEntity> XP_OBELISK =
        register("xp_obelisk", XPObeliskBlockEntity::new, MachineBlocks.XP_OBELISK)
            .setRenderer(() -> XPObeliskBER::new);;

    @SafeVarargs
    private static <B extends BlockEntity> RegiliteBlockEntity<B> register(String name, BlockEntityType.BlockEntitySupplier<B> beFactory,
        Supplier<? extends Block>... blocks) {
        return BLOCK_ENTITY_REGISTRY
            .registerBlockEntity(name, beFactory, blocks);
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_REGISTRY.register(bus);
    }
}
