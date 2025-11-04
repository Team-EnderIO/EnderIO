package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.enchanter.EnchanterBlock;
import com.enderio.enderio.content.enderface.EnderfaceBlock;
import com.enderio.enderio.content.machines.block_detector.BlockDetectorBlock;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankBlock;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankBlockEntity;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankItem;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorTier;
import com.enderio.enderio.content.machines.niard.NiardBlock;
import com.enderio.enderio.content.machines.obelisks.attractor.AttractorObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.aversion.AversionObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.inhibitor.InhibitorObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.relocator.RelocatorObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.weather.WeatherObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.xp.XPObeliskBlockEntity;
import com.enderio.enderio.content.machines.powered_spawner.MindKillerBlock;
import com.enderio.enderio.content.machines.powered_spawner.PoweredSpawnerBlockEntity;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelBlock;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelBlockEntity;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelTier;
import com.enderio.enderio.content.machines.soul_engine.SoulEngineBlockEntity;
import com.enderio.enderio.content.machines.vacuum.chest.VacuumChestBlockEntity;
import com.enderio.enderio.content.machines.vacuum.xp.XPVacuumBlockEntity;
import com.enderio.enderio.content.machines.vat.VatBlock;
import com.enderio.enderio.content.machines.wireless_charger.WirelessAntennaBlock;
import com.enderio.enderio.content.paint.item.PaintedBlockItem;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlock;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockItem;
import com.enderio.enderio.content.travel.travel_anchor.PaintedTravelAnchorBlock;
import com.enderio.enderio.content.travel.travel_anchor.TravelAnchorBlock;
import com.enderio.enderio.content.travel.travel_anchor.TravelAnchorBlockEntity;
import com.enderio.enderio.foundation.block.MachineBlock;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.block.legacy.LegacyMachineBlock;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class MachineBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EnderIO.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnderIO.MOD_ID);

    // Fluid Tanks
    public static final DeferredBlock<FluidTankBlock> FLUID_TANK = BLOCKS.registerBlock("fluid_tank",
        props -> new FluidTankBlock(EIOBlockEntities.FLUID_TANK::get, props),
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    public static final DeferredBlock<FluidTankBlock> PRESSURIZED_FLUID_TANK = BLOCKS.registerBlock("pressurized_fluid_tank",
        props -> new FluidTankBlock(EIOBlockEntities.PRESSURIZED_FLUID_TANK::get, props),
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    // Enchanter
    public static final DeferredBlock<EnchanterBlock> ENCHANTER = BLOCKS.registerBlock("enchanter", EnchanterBlock::new,
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .noOcclusion()
            .isViewBlocking((pState, pLevel, pPos) -> false));

    // Enderface
    public static final DeferredBlock<EnderfaceBlock> ENDERFACE = BLOCKS.registerBlock("enderface", EnderfaceBlock::new,
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .noOcclusion()
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .requiredFeatures(EIOFeatureFlags.ENDERFACE));

    // Progress Machines
    public static final DeferredBlock<ProgressMachineBlock<?>> ALLOY_SMELTER = progressMachine("alloy_smelter",
        () -> EIOBlockEntities.ALLOY_SMELTER::get);

    public static final DeferredBlock<ProgressMachineBlock<?>> PAINTING_MACHINE = progressMachine("painting_machine",
        () -> EIOBlockEntities.PAINTING_MACHINE::get);

    public static final DeferredBlock<ProgressMachineBlock<?>> WIRELESS_CHARGER = progressMachine("wireless_charger",
        () -> EIOBlockEntities.WIRELESS_CHARGER::get);

    public static final DeferredBlock<ProgressMachineBlock<?>> STIRLING_GENERATOR = progressMachine("stirling_generator",
        () -> EIOBlockEntities.STIRLING_GENERATOR::get);

    public static final DeferredBlock<ProgressMachineBlock<?>> SAG_MILL = progressMachine("sag_mill",
        () -> EIOBlockEntities.SAG_MILL::get);

    public static final DeferredBlock<ProgressMachineBlock<?>> SLICE_AND_SPLICE = progressMachine("slice_and_splice",
        () -> EIOBlockEntities.SLICE_AND_SPLICE::get);

    public static final DeferredBlock<ProgressMachineBlock<?>> IMPULSE_HOPPER = progressMachine("impulse_hopper",
        () -> EIOBlockEntities.IMPULSE_HOPPER::get);

    public static final DeferredBlock<ProgressMachineBlock<?>> SOUL_BINDER = progressMachine("soul_binder",
        () -> EIOBlockEntities.SOUL_BINDER::get);

    public static final DeferredBlock<ProgressMachineBlock<?>> CRAFTER = progressMachine("crafter",
        () -> EIOBlockEntities.CRAFTER::get);

    public static final DeferredBlock<ProgressMachineBlock<?>> DRAIN = progressMachine("drain",
        () -> EIOBlockEntities.DRAIN::get);

    // Machines
    public static final DeferredBlock<MachineBlock<?>> WIRED_CHARGER = machine("wired_charger",
        () -> EIOBlockEntities.WIRED_CHARGER::get);

    // Wireless Antennas
    public static final DeferredBlock<WirelessAntennaBlock> WIRELESS_CHARGER_ANTENNA = BLOCKS.registerBlock("wireless_charger_antenna",
        WirelessAntennaBlock::new,
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    public static final DeferredBlock<WirelessAntennaBlock> WIRELESS_CHARGER_ANTENNA_ADVANCED = BLOCKS.registerBlock("wireless_charger_antenna_advanced",
        WirelessAntennaBlock::new,
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    // Creative Power
    public static final DeferredBlock<LegacyMachineBlock> CREATIVE_POWER = BLOCKS.registerBlock("creative_power",
        props -> new LegacyMachineBlock(EIOBlockEntities.CREATIVE_POWER::get, props),
        BlockBehaviour.Properties.of());

    // Powered Spawner
    public static final DeferredBlock<ProgressMachineBlock<PoweredSpawnerBlockEntity>> POWERED_SPAWNER = BLOCKS.registerBlock("powered_spawner",
        properties -> new ProgressMachineBlock<>(EIOBlockEntities.POWERED_SPAWNER::get, properties),
        BlockBehaviour.Properties.of().strength(2.5f, 8));

    // Mind Killer
    public static final DeferredBlock<MindKillerBlock> MIND_KILLER = BLOCKS.registerBlock("mind_killer", MindKillerBlock::new,
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    // Vacuum Machines
    public static final DeferredBlock<MachineBlock<VacuumChestBlockEntity>> VACUUM_CHEST = BLOCKS.registerBlock("vacuum_chest",
        p -> new MachineBlock<>(EIOBlockEntities.VACUUM_CHEST::get, p),
        BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion());

    public static final DeferredBlock<MachineBlock<XPVacuumBlockEntity>> XP_VACUUM = BLOCKS.registerBlock("xp_vacuum",
        p -> new MachineBlock<>(EIOBlockEntities.XP_VACUUM::get, p),
        BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion());

    // Travel Anchors
    public static final DeferredBlock<TravelAnchorBlock<TravelAnchorBlockEntity>> TRAVEL_ANCHOR = BLOCKS.registerBlock("travel_anchor",
        props -> new TravelAnchorBlock<>(EIOBlockEntities.TRAVEL_ANCHOR::get, props),
        BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion());

    public static final DeferredBlock<PaintedTravelAnchorBlock> PAINTED_TRAVEL_ANCHOR = BLOCKS.registerBlock("painted_travel_anchor",
        PaintedTravelAnchorBlock::new,
        BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion());

    // Solar Panels
    public static final Map<SolarPanelTier, DeferredBlock<SolarPanelBlock>> SOLAR_PANELS = Util.make(() -> {
        Map<SolarPanelTier, DeferredBlock<SolarPanelBlock>> panels = new HashMap<>();
        for (SolarPanelTier tier : SolarPanelTier.values()) {
            panels.put(tier, solarPanel(tier.name().toLowerCase(Locale.ROOT) + "_photovoltaic_module",
                () -> EIOBlockEntities.SOLAR_PANELS.get(tier)::get, tier));
        }
        return ImmutableMap.copyOf(panels);
    });

    // Capacitor Banks
    public static final Map<CapacitorTier, DeferredBlock<CapacitorBankBlock>> CAPACITOR_BANKS = Util.make(() -> {
        Map<CapacitorTier, DeferredBlock<CapacitorBankBlock>> banks = new HashMap<>();
        for (CapacitorTier tier : CapacitorTier.values()) {
            banks.put(tier, capacitorBank(tier.name().toLowerCase(Locale.ROOT) + "_capacitor_bank",
                () -> EIOBlockEntities.CAPACITOR_BANKS.get(tier)::get, tier));
        }
        return ImmutableMap.copyOf(banks);
    });

    // Soul Engine
    public static final DeferredBlock<ProgressMachineBlock<SoulEngineBlockEntity>> SOUL_ENGINE = BLOCKS.registerBlock("soul_engine",
        p -> new ProgressMachineBlock<>(EIOBlockEntities.SOUL_ENGINE::get, p),
        BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion());

    // Niard
    public static final DeferredBlock<NiardBlock> NIARD = BLOCKS.registerBlock("niard", NiardBlock::new,
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion()
            .requiredFeatures(EIOFeatureFlags.NIARD));

    // VAT
    public static final DeferredBlock<VatBlock> VAT = BLOCKS.registerBlock("vat",
        VatBlock::new,
        BlockBehaviour.Properties.of().strength(2.5f, 8));

    // Block Detector
    public static final DeferredBlock<BlockDetectorBlock> BLOCK_DETECTOR = BLOCKS.registerBlock("block_detector",
        BlockDetectorBlock::new,
        BlockBehaviour.Properties.ofFullCopy(Blocks.OBSERVER));

    // Obelisks
    public static final DeferredBlock<MachineBlock<XPObeliskBlockEntity>> XP_OBELISK = BLOCKS.registerBlock("xp_obelisk",
        props -> new MachineBlock<>(EIOBlockEntities.XP_OBELISK::get, props),
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    public static final DeferredBlock<ProgressMachineBlock> FARMING_STATION = BLOCKS.registerBlock("farming_station",
        properties -> new ProgressMachineBlock(EIOBlockEntities.FARMING_STATION, properties),
        BlockBehaviour.Properties.of().strength(2.5f, 8).requiredFeatures(EIOFeatureFlags.FARMING_STATION));

    public static final DeferredBlock<MachineBlock<InhibitorObeliskBlockEntity>> INHIBITOR_OBELISK = BLOCKS.registerBlock("inhibitor_obelisk",
        props -> new MachineBlock<>(EIOBlockEntities.INHIBITOR_OBELISK::get, props),
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    public static final DeferredBlock<MachineBlock<AversionObeliskBlockEntity>> AVERSION_OBELISK = BLOCKS.registerBlock("aversion_obelisk",
        props -> new MachineBlock<>(EIOBlockEntities.AVERSION_OBELISK::get, props),
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    public static final DeferredBlock<MachineBlock<RelocatorObeliskBlockEntity>> RELOCATOR_OBELISK = BLOCKS.registerBlock("relocator_obelisk",
        props -> new MachineBlock<>(EIOBlockEntities.RELOCATOR_OBELISK::get, props),
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    public static final DeferredBlock<MachineBlock<AttractorObeliskBlockEntity>> ATTRACTOR_OBELISK = BLOCKS.registerBlock("attractor_obelisk",
        props -> new MachineBlock<>(EIOBlockEntities.ATTRACTOR_OBELISK::get, props),
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    public static final DeferredBlock<MachineBlock<WeatherObeliskBlockEntity>> WEATHER_OBELISK = BLOCKS.registerBlock("weather_obelisk",
        props -> new MachineBlock<>(EIOBlockEntities.WEATHER_OBELISK::get, props),
        BlockBehaviour.Properties.of()
            .strength(2.5f, 8)
            .isViewBlocking((pState, pLevel, pPos) -> false)
            .noOcclusion());

    // Helper methods
    private static DeferredBlock<MachineBlock<?>> machine(String name,
        Supplier<Supplier<BlockEntityType<? extends MachineBlockEntity>>> regiliteBlockEntity) {
        return BLOCKS.registerBlock(name,
            props -> new MachineBlock<>(regiliteBlockEntity.get(), props),
            BlockBehaviour.Properties.of().strength(2.5f, 8));
    }

    private static DeferredBlock<ProgressMachineBlock<?>> progressMachine(String name,
        Supplier<Supplier<BlockEntityType<? extends MachineBlockEntity>>> regiliteBlockEntity) {
        return BLOCKS.registerBlock(name,
            props -> new ProgressMachineBlock<>(regiliteBlockEntity.get(), props),
            BlockBehaviour.Properties.of().strength(2.5f, 8));
    }

    private static DeferredBlock<SolarPanelBlock> solarPanel(String name,
        Supplier<Supplier<BlockEntityType<? extends SolarPanelBlockEntity>>> regiliteBlockEntity, SolarPanelTier tier) {
        return BLOCKS.registerBlock(name,
            props -> new SolarPanelBlock(regiliteBlockEntity.get()::get, props, tier),
            BlockBehaviour.Properties.of().strength(2.5f, 8));
    }

    private static DeferredBlock<CapacitorBankBlock> capacitorBank(String name,
        Supplier<Supplier<BlockEntityType<? extends CapacitorBankBlockEntity>>> regiliteBlockEntity, CapacitorTier tier) {
        return BLOCKS.registerBlock(name,
            props -> new CapacitorBankBlock(props, regiliteBlockEntity.get()::get, tier),
            BlockBehaviour.Properties.of().strength(2.5f, 8));
    }

    // Items that need capabilities (exposed as DeferredItems)
    public static final DeferredItem<FluidTankBlockItem> FLUID_TANK_ITEM = ITEMS.register("fluid_tank",
        () -> new FluidTankBlockItem(FLUID_TANK.get(), new Item.Properties(), 16000));
    public static final DeferredItem<FluidTankBlockItem> PRESSURIZED_FLUID_TANK_ITEM = ITEMS.register("pressurized_fluid_tank",
        () -> new FluidTankBlockItem(PRESSURIZED_FLUID_TANK.get(), new Item.Properties(), 32000));

    public static final DeferredItem<BlockItem> POWERED_SPAWNER_ITEM = ITEMS.register("powered_spawner",
        () -> new BlockItem(POWERED_SPAWNER.get(), new Item.Properties().component(EIODataComponents.SOUL, Soul.EMPTY)));

    public static final DeferredItem<BlockItem> SOUL_ENGINE_ITEM = ITEMS.register("soul_engine",
        () -> new BlockItem(SOUL_ENGINE.get(), new Item.Properties().component(EIODataComponents.SOUL, Soul.EMPTY)));

    public static final Map<SolarPanelTier, DeferredItem<BlockItem>> SOLAR_PANEL_ITEMS = Util.make(() -> {
        Map<SolarPanelTier, DeferredItem<BlockItem>> items = new HashMap<>();
        for (var entry : SOLAR_PANELS.entrySet()) {
            items.put(entry.getKey(), ITEMS.register(entry.getValue().getId().getPath(),
                () -> new BlockItem(entry.getValue().get(), new Item.Properties().component(EIODataComponents.SOUL, Soul.EMPTY))));
        }
        return ImmutableMap.copyOf(items);
    });

    public static final Map<CapacitorTier, DeferredItem<CapacitorBankItem>> CAPACITOR_BANK_ITEMS = Util.make(() -> {
        Map<CapacitorTier, DeferredItem<CapacitorBankItem>> items = new HashMap<>();
        for (var entry : CAPACITOR_BANKS.entrySet()) {
            items.put(entry.getKey(), ITEMS.register(entry.getValue().getId().getPath(),
                () -> new CapacitorBankItem(entry.getValue().get(), new Item.Properties())));
        }
        return ImmutableMap.copyOf(items);
    });

    // Item registration methods
    public static void registerItems() {
        // Fluid Tanks - already registered above

        // Enchanter
        ITEMS.registerSimpleBlockItem(ENCHANTER);

        // Enderface
        ITEMS.registerSimpleBlockItem(ENDERFACE);

        // Progress Machines
        ITEMS.registerSimpleBlockItem(ALLOY_SMELTER);
        ITEMS.registerSimpleBlockItem(PAINTING_MACHINE);
        ITEMS.registerSimpleBlockItem(WIRELESS_CHARGER);
        ITEMS.registerSimpleBlockItem(STIRLING_GENERATOR);
        ITEMS.registerSimpleBlockItem(SAG_MILL);
        ITEMS.registerSimpleBlockItem(SLICE_AND_SPLICE);
        ITEMS.registerSimpleBlockItem(IMPULSE_HOPPER);
        ITEMS.registerSimpleBlockItem(SOUL_BINDER);
        ITEMS.registerSimpleBlockItem(CRAFTER);
        ITEMS.registerSimpleBlockItem(DRAIN);

        // Machines
        ITEMS.registerSimpleBlockItem(WIRED_CHARGER);

        // Wireless Antennas
        ITEMS.registerSimpleBlockItem(WIRELESS_CHARGER_ANTENNA);
        ITEMS.registerSimpleBlockItem(WIRELESS_CHARGER_ANTENNA_ADVANCED);

        // Creative Power
        ITEMS.registerSimpleBlockItem(CREATIVE_POWER);

        // Powered Spawner - already registered above
        // Soul Engine - already registered above
        // Solar Panels - already registered above
        // Capacitor Banks - already registered above

        // Mind Killer
        ITEMS.registerSimpleBlockItem(MIND_KILLER);

        // Vacuum Machines
        ITEMS.registerSimpleBlockItem(VACUUM_CHEST);
        ITEMS.registerSimpleBlockItem(XP_VACUUM);

        // Travel Anchors
        ITEMS.registerSimpleBlockItem(TRAVEL_ANCHOR);
        ITEMS.register("painted_travel_anchor", () -> new PaintedBlockItem(PAINTED_TRAVEL_ANCHOR.get(), new Item.Properties()));

        // Niard
        ITEMS.registerSimpleBlockItem(NIARD);

        // VAT
        ITEMS.registerSimpleBlockItem(VAT);

        // Block Detector
        ITEMS.registerSimpleBlockItem(BLOCK_DETECTOR);

        // Obelisks
        ITEMS.registerSimpleBlockItem(XP_OBELISK);
        ITEMS.registerSimpleBlockItem(FARMING_STATION);
        ITEMS.registerSimpleBlockItem(INHIBITOR_OBELISK);
        ITEMS.registerSimpleBlockItem(AVERSION_OBELISK);
        ITEMS.registerSimpleBlockItem(RELOCATOR_OBELISK);
        ITEMS.registerSimpleBlockItem(ATTRACTOR_OBELISK);
        ITEMS.registerSimpleBlockItem(WEATHER_OBELISK);
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        registerItems();
    }
}
