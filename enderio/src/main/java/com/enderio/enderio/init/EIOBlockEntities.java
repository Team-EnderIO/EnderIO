package com.enderio.enderio.init;

import com.enderio.core.common.registries.BlockEntityTypeDeferredRegister;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.content.dev_tools.CreativePowerBlockEntity;
import com.enderio.enderio.content.enchanter.EnchanterBlockEntity;
import com.enderio.enderio.content.enderface.EnderfaceBlockEntity;
import com.enderio.enderio.content.machines.alloy.AlloySmelterBlockEntity;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankBlockEntity;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorTier;
import com.enderio.enderio.content.machines.drain.DrainBlockEntity;
import com.enderio.enderio.content.machines.farming_station.FarmingStationBlockEntity;
import com.enderio.enderio.content.machines.impulse_hopper.ImpulseHopperBlockEntity;
import com.enderio.enderio.content.machines.niard.NiardBlockEntity;
import com.enderio.enderio.content.machines.obelisks.attractor.AttractorObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.aversion.AversionObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.inhibitor.InhibitorObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.relocator.RelocatorObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.weather.WeatherObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.xp.XPObeliskBlockEntity;
import com.enderio.enderio.content.machines.painting.PaintingMachineBlockEntity;
import com.enderio.enderio.content.machines.powered_spawner.MindKillerBlockEntity;
import com.enderio.enderio.content.machines.powered_spawner.PoweredSpawnerBlockEntity;
import com.enderio.enderio.content.machines.sag_mill.SagMillBlockEntity;
import com.enderio.enderio.content.machines.slicer.SlicerBlockEntity;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelBlockEntity;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelTier;
import com.enderio.enderio.content.machines.soul_binder.SoulBinderBlockEntity;
import com.enderio.enderio.content.machines.soul_engine.SoulEngineBlockEntity;
import com.enderio.enderio.content.machines.stirling_generator.StirlingGeneratorBlockEntity;
import com.enderio.enderio.content.machines.vacuum.chest.VacuumChestBlockEntity;
import com.enderio.enderio.content.machines.vacuum.xp.XPVacuumBlockEntity;
import com.enderio.enderio.content.machines.vat.VatBlockEntity;
import com.enderio.enderio.content.machines.wired_charger.WiredChargerBlockEntity;
import com.enderio.enderio.content.machines.wireless_charger.WirelessChargerBlockEntity;
import com.enderio.enderio.content.paint.block.entity.DoublePaintedBlockEntity;
import com.enderio.enderio.content.paint.block.entity.SinglePaintedBlockEntity;
import com.enderio.enderio.content.storage.crafter.CrafterBlockEntity;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.enderio.enderio.content.travel.travel_anchor.PaintedTravelAnchorBlockEntity;
import com.enderio.enderio.content.travel.travel_anchor.TravelAnchorBlockEntity;
import com.enderio.enderio.foundation.attachment.FluidTankUser;
import com.enderio.enderio.foundation.block.entity.EnderSkullBlockEntity;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.legacy.LegacyMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.legacy.LegacyPoweredMachineBlockEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EIOBlockEntities {
    public static final BlockEntityTypeDeferredRegister BLOCK_ENTITY_TYPES = BlockEntityTypeDeferredRegister.create(EnderIO.MOD_ID);

    public static final Map<CapacitorTier, DeferredHolder<BlockEntityType<?>, BlockEntityType<CapacitorBankBlockEntity>>> CAPACITOR_BANKS = Util
        .make(() -> {
            Map<CapacitorTier, DeferredHolder<BlockEntityType<?>, BlockEntityType<CapacitorBankBlockEntity>>> map = new HashMap<>();
            for (CapacitorTier tier : CapacitorTier.values()) {
                map.put(tier, BLOCK_ENTITY_TYPES
                    .builder(tier.name().toLowerCase(Locale.ROOT) + "_capacitor_bank",
                        (worldPosition, blockState) -> new CapacitorBankBlockEntity(worldPosition, blockState, tier),
                        () -> MachineBlocks.CAPACITOR_BANKS.get(tier).get())
                    .apply(EIOBlockEntities::legacyPoweredMachineBlockEntityCapabilities)
                    .build()
                );
            }
            return ImmutableMap.copyOf(map);
        });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConduitBundleBlockEntity>> CONDUIT = BLOCK_ENTITY_TYPES.register("conduit",
        ConduitBundleBlockEntity::new, ConduitBlocks.CONDUIT_BUNDLE::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativePowerBlockEntity>> CREATIVE_POWER = BLOCK_ENTITY_TYPES
        .builder("creative_power", CreativePowerBlockEntity::new, MachineBlocks.CREATIVE_POWER::get)
        .apply(EIOBlockEntities::legacyPoweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchanterBlockEntity>> ENCHANTER = BLOCK_ENTITY_TYPES.register("enchanter",
        EnchanterBlockEntity::new, MachineBlocks.ENCHANTER::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderfaceBlockEntity>> ENDERFACE = BLOCK_ENTITY_TYPES.register("enderface",
        EnderfaceBlockEntity::new, MachineBlocks.ENDERFACE::get);

    // region Machines

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlloySmelterBlockEntity>> ALLOY_SMELTER = BLOCK_ENTITY_TYPES
        .builder("alloy_smelter", AlloySmelterBlockEntity::new, MachineBlocks.ALLOY_SMELTER::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StirlingGeneratorBlockEntity>> STIRLING_GENERATOR = BLOCK_ENTITY_TYPES
        .builder("stirling_generator", StirlingGeneratorBlockEntity::new, MachineBlocks.STIRLING_GENERATOR::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SagMillBlockEntity>> SAG_MILL = BLOCK_ENTITY_TYPES
        .builder("sag_mill", SagMillBlockEntity::new, MachineBlocks.SAG_MILL::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SlicerBlockEntity>> SLICE_AND_SPLICE = BLOCK_ENTITY_TYPES
        .builder("slice_and_splice", SlicerBlockEntity::new, MachineBlocks.SLICE_AND_SPLICE::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ImpulseHopperBlockEntity>> IMPULSE_HOPPER = BLOCK_ENTITY_TYPES
        .builder("impulse_hopper", ImpulseHopperBlockEntity::new, MachineBlocks.IMPULSE_HOPPER::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VacuumChestBlockEntity>> VACUUM_CHEST = BLOCK_ENTITY_TYPES
        .builder("vacuum_chest", VacuumChestBlockEntity::new, MachineBlocks.VACUUM_CHEST::get)
        .apply(EIOBlockEntities::machineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<XPVacuumBlockEntity>> XP_VACUUM = BLOCK_ENTITY_TYPES
        .builder("xp_vacuum", XPVacuumBlockEntity::new, MachineBlocks.XP_VACUUM::get)
        .apply(EIOBlockEntities::machineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrafterBlockEntity>> CRAFTER = BLOCK_ENTITY_TYPES
        .builder("crafter", CrafterBlockEntity::new, MachineBlocks.CRAFTER::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DrainBlockEntity>> DRAIN = BLOCK_ENTITY_TYPES
        .builder("drain", DrainBlockEntity::new, MachineBlocks.DRAIN::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NiardBlockEntity>> NIARD = BLOCK_ENTITY_TYPES
        .builder("niard", NiardBlockEntity::new, MachineBlocks.NIARD::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SoulBinderBlockEntity>> SOUL_BINDER = BLOCK_ENTITY_TYPES
        .builder("soul_binder", SoulBinderBlockEntity::new, MachineBlocks.SOUL_BINDER::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WiredChargerBlockEntity>> WIRED_CHARGER = BLOCK_ENTITY_TYPES
        .builder("wired_charger", WiredChargerBlockEntity::new, MachineBlocks.WIRED_CHARGER::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WirelessChargerBlockEntity>> WIRELESS_CHARGER = BLOCK_ENTITY_TYPES
        .builder("wireless_charger", WirelessChargerBlockEntity::new, MachineBlocks.WIRELESS_CHARGER::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PaintingMachineBlockEntity>> PAINTING_MACHINE = BLOCK_ENTITY_TYPES
        .builder("painting_machine", PaintingMachineBlockEntity::new, MachineBlocks.PAINTING_MACHINE::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PoweredSpawnerBlockEntity>> POWERED_SPAWNER = BLOCK_ENTITY_TYPES
        .builder("powered_spawner", PoweredSpawnerBlockEntity::new, MachineBlocks.POWERED_SPAWNER::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .apply(EIOBlockEntities::soulBoundCapability)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SoulEngineBlockEntity>> SOUL_ENGINE = BLOCK_ENTITY_TYPES
        .builder("soul_engine", SoulEngineBlockEntity::new, MachineBlocks.SOUL_ENGINE::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .apply(EIOBlockEntities::soulBoundCapability)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<XPObeliskBlockEntity>> XP_OBELISK = BLOCK_ENTITY_TYPES
        .builder("xp_obelisk", XPObeliskBlockEntity::new, MachineBlocks.XP_OBELISK::get)
        .apply(EIOBlockEntities::machineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VatBlockEntity>> VAT = BLOCK_ENTITY_TYPES
        .builder("vat", VatBlockEntity::new, MachineBlocks.VAT::get)
        .apply(EIOBlockEntities::machineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InhibitorObeliskBlockEntity>> INHIBITOR_OBELISK = BLOCK_ENTITY_TYPES
        .builder("inhibitor_obelisk", InhibitorObeliskBlockEntity::new, MachineBlocks.INHIBITOR_OBELISK::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AversionObeliskBlockEntity>> AVERSION_OBELISK = BLOCK_ENTITY_TYPES
        .builder("aversion_obelisk", AversionObeliskBlockEntity::new, MachineBlocks.AVERSION_OBELISK::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RelocatorObeliskBlockEntity>> RELOCATOR_OBELISK = BLOCK_ENTITY_TYPES
        .builder("relocator_obelisk", RelocatorObeliskBlockEntity::new, MachineBlocks.RELOCATOR_OBELISK::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AttractorObeliskBlockEntity>> ATTRACTOR_OBELISK = BLOCK_ENTITY_TYPES
        .builder("attractor_obelisk", AttractorObeliskBlockEntity::new, MachineBlocks.ATTRACTOR_OBELISK::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WeatherObeliskBlockEntity>> WEATHER_OBELISK = BLOCK_ENTITY_TYPES
        .builder("weather_obelisk", WeatherObeliskBlockEntity::new, MachineBlocks.WEATHER_OBELISK::get)
        .apply(EIOBlockEntities::machineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FarmingStationBlockEntity>> FARMING_STATION = BLOCK_ENTITY_TYPES
        .builder("farming_station", FarmingStationBlockEntity::new, MachineBlocks.FARMING_STATION::get)
        .apply(EIOBlockEntities::poweredMachineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .apply(EIOBlockEntities::soulBoundCapability)
        .build();

    public static final Map<SolarPanelTier, DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>>> SOLAR_PANELS = Util.make(() -> {
        Map<SolarPanelTier, DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>>> map = new HashMap<>();
        for (SolarPanelTier tier : SolarPanelTier.values()) {
            map.put(tier, BLOCK_ENTITY_TYPES
                .builder(tier.name().toLowerCase(Locale.ROOT) + "_photovoltaic_cell",
                    (worldPosition, blockState) -> new SolarPanelBlockEntity(worldPosition, blockState, tier),
                    () -> MachineBlocks.SOLAR_PANELS.get(tier).get())
                .apply(EIOBlockEntities::legacyPoweredMachineBlockEntityCapabilities)
                .build())
            ;
        }
        return ImmutableMap.copyOf(map);
    });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MindKillerBlockEntity>> MIND_KILLER = BLOCK_ENTITY_TYPES
        .register("mind_killer", MindKillerBlockEntity::new, MachineBlocks.MIND_KILLER::get);

    private static void machineBlockEntityCapabilities(BlockEntityTypeDeferredRegister.Builder<? extends MachineBlockEntity> builder) {
        builder.capability(EnderIOCapabilities.SIDE_CONFIG, MachineBlockEntity.SIDE_CONFIG_PROVIDER);
        builder.capability(Capabilities.ItemHandler.BLOCK, MachineBlockEntity.ITEM_HANDLER_PROVIDER);
    }

    private static void poweredMachineBlockEntityCapabilities(
        BlockEntityTypeDeferredRegister.Builder<? extends PoweredMachineBlockEntity> builder) {
        machineBlockEntityCapabilities(builder);
        builder.capability(Capabilities.EnergyStorage.BLOCK, PoweredMachineBlockEntity.ENERGY_STORAGE_PROVIDER);
    }

    private static void legacyPoweredMachineBlockEntityCapabilities(BlockEntityTypeDeferredRegister.Builder<? extends LegacyPoweredMachineBlockEntity> builder) {
        legacyMachineBlockEntityCapabilities(builder);
        builder.capability(Capabilities.EnergyStorage.BLOCK,
            LegacyPoweredMachineBlockEntity.ENERGY_STORAGE_PROVIDER);
    }

    private static void legacyMachineBlockEntityCapabilities(BlockEntityTypeDeferredRegister.Builder<? extends LegacyMachineBlockEntity> builder) {
        builder.capability(EnderIOCapabilities.SIDE_CONFIG, LegacyMachineBlockEntity.SIDE_CONFIG_PROVIDER);
        builder.capability(Capabilities.ItemHandler.BLOCK, LegacyMachineBlockEntity.ITEM_HANDLER_PROVIDER);
    }

    private static void soulBoundCapability(BlockEntityTypeDeferredRegister.Builder<? extends MachineBlockEntity> blockEntity) {
        blockEntity.capability(EnderIOCapabilities.SOUL_BINDABLE_BLOCK, MachineBlockEntity.SOUL_BINDABLE);
    }

    // endregion

    // region Painting

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SinglePaintedBlockEntity>> SINGLE_PAINTED = BLOCK_ENTITY_TYPES
        .builder("single_painted", SinglePaintedBlockEntity::new)
        .validBlock(EIOBlocks.PAINTED_FENCE::get)
        .validBlock(EIOBlocks.PAINTED_FENCE_GATE::get)
        .validBlock(EIOBlocks.PAINTED_SAND::get)
        .validBlock(EIOBlocks.PAINTED_STAIRS::get)
        .validBlock(EIOBlocks.PAINTED_CRAFTING_TABLE::get)
        .validBlock(EIOBlocks.PAINTED_REDSTONE_BLOCK::get)
        .validBlock(EIOBlocks.PAINTED_TRAPDOOR::get)
        .validBlock(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE::get)
        .validBlock(EIOBlocks.PAINTED_GLOWSTONE::get)
        .validBlock(EIOBlocks.PAINTED_WALL::get)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoublePaintedBlockEntity>> DOUBLE_PAINTED = BLOCK_ENTITY_TYPES.register("double_painted",
        DoublePaintedBlockEntity::new, EIOBlocks.PAINTED_SLAB::get);

    // endregion

    // region Storage

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidTankBlockEntity.Standard>> FLUID_TANK = BLOCK_ENTITY_TYPES
        .builder("fluid_tank", FluidTankBlockEntity.Standard::new, MachineBlocks.FLUID_TANK::get)
        .apply(EIOBlockEntities::machineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidTankBlockEntity.Enhanced>> PRESSURIZED_FLUID_TANK = BLOCK_ENTITY_TYPES
        .builder("pressurized_fluid_tank", FluidTankBlockEntity.Enhanced::new, MachineBlocks.PRESSURIZED_FLUID_TANK::get)
        .apply(EIOBlockEntities::machineBlockEntityCapabilities)
        .apply(EIOBlockEntities::fluidHandlerCapability)
        .build();

    // endregion

    // region Travel

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TravelAnchorBlockEntity>> TRAVEL_ANCHOR = BLOCK_ENTITY_TYPES
        .builder("travel_anchor", TravelAnchorBlockEntity::new, MachineBlocks.TRAVEL_ANCHOR::get)
        .apply(EIOBlockEntities::machineBlockEntityCapabilities)
        .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PaintedTravelAnchorBlockEntity>> PAINTED_TRAVEL_ANCHOR = BLOCK_ENTITY_TYPES
        .builder("painted_travel_anchor", PaintedTravelAnchorBlockEntity::new, MachineBlocks.PAINTED_TRAVEL_ANCHOR::get)
        .apply(EIOBlockEntities::machineBlockEntityCapabilities)
        .build();

    // endregion

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderSkullBlockEntity>> ENDER_SKULL = BLOCK_ENTITY_TYPES.register("ender_skull",
        EnderSkullBlockEntity::new, EIOBlocks.WALL_ENDERMAN_HEAD::get, EIOBlocks.ENDERMAN_HEAD::get);

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    private static void fluidHandlerCapability(BlockEntityTypeDeferredRegister.Builder<? extends MachineBlockEntity> blockEntity) {
        blockEntity.capability(Capabilities.FluidHandler.BLOCK, FluidTankUser.FLUID_HANDLER_PROVIDER);
    }
}
