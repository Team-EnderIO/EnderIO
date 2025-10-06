package com.enderio.enderio.init;

import com.enderio.core.data.model.ModelHelper;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.client.content.paint.PaintedBlockColor;
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
import com.enderio.enderio.data.loot.DecorLootTable;
import com.enderio.enderio.data.loot.MachinesLootTable;
import com.enderio.enderio.data.model.MachineModelUtil;
import com.enderio.enderio.data.model.block.EIOBlockState;
import com.enderio.enderio.foundation.block.MachineBlock;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.block.legacy.LegacyMachineBlock;
import com.enderio.enderio.foundation.soul.SoulCapabilityProviders;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.regilite.data.DataGenContext;
import com.enderio.regilite.holder.RegiliteBlock;
import com.enderio.regilite.registry.BlockRegistry;
import com.enderio.regilite.registry.ItemRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MachineBlocks {
    private static final BlockRegistry BLOCK_REGISTRY = EnderIO.REGILITE.blockRegistry();
    private static final ItemRegistry ITEM_REGISTRY = EnderIO.REGILITE.itemRegistry();

    public static final RegiliteBlock<FluidTankBlock> FLUID_TANK = BLOCK_REGISTRY
            .registerBlock("fluid_tank", props -> new FluidTankBlock(EIOBlockEntities.FLUID_TANK::get, props),
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 8)
                            .isViewBlocking((pState, pLevel, pPos) -> false)
                            .noOcclusion())
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider((prov, ctx) -> prov.horizontalBlock(ctx.get(), prov.models()
                    .getBuilder(ctx.getName())
                    .customLoader(CompositeModelBuilder::begin)
                    .child("tank",
                            ModelHelper.getExistingAsBuilder(prov.models(),
                                    EnderIO.rl(String.format("block/%s_body", ctx.getName()))))
                    .child("overlay", ModelHelper.getExistingAsBuilder(prov.models(), EnderIO.rl("block/io_overlay")))
                    .end()
                    .texture("particle", EnderIO.rl("block/machine_side"))))
            .createBlockItem(ITEM_REGISTRY, block -> new FluidTankBlockItem(block, new Item.Properties(), 16000),
                    item -> item.setModelProvider((prov, ctx) -> {
                    })
                            .setTab(EIOCreativeTabs.MACHINES)
                            .addCapability(Capabilities.FluidHandler.ITEM, FluidTankBlockItem.FLUID_HANDLER_PROVIDER));

    public static final RegiliteBlock<FluidTankBlock> PRESSURIZED_FLUID_TANK = BLOCK_REGISTRY
            .registerBlock("pressurized_fluid_tank",
                    props -> new FluidTankBlock(EIOBlockEntities.PRESSURIZED_FLUID_TANK::get, props),
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 8)
                            .isViewBlocking((pState, pLevel, pPos) -> false)
                            .noOcclusion())
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider((prov, ctx) -> prov.horizontalBlock(ctx.get(), prov.models()
                    .withExistingParent(ctx.getName(), prov.mcLoc("block/block"))
                    .customLoader(CompositeModelBuilder::begin)
                    .child("tank",
                            ModelHelper.getExistingAsBuilder(prov.models(),
                                    EnderIO.rl(String.format("block/%s_body", ctx.getName()))))
                    .child("overlay", ModelHelper.getExistingAsBuilder(prov.models(), EnderIO.rl("block/io_overlay")))
                    .end()
                    .texture("particle", EnderIO.rl("block/machine_side"))))
            .createBlockItem(ITEM_REGISTRY, (block) -> new FluidTankBlockItem(block, new Item.Properties(), 32000),
                    item -> item.setModelProvider((prov, ctx) -> {
                    })
                            .setTab(EIOCreativeTabs.MACHINES)
                            .addCapability(Capabilities.FluidHandler.ITEM, FluidTankBlockItem.FLUID_HANDLER_PROVIDER));

    public static final RegiliteBlock<EnchanterBlock> ENCHANTER = BLOCK_REGISTRY
            .registerBlock("enchanter", EnchanterBlock::new,
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 8)
                            .noOcclusion()
                            .isViewBlocking((pState, pLevel, pPos) -> false))
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider(MachineModelUtil::machineBlock)
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<EnderfaceBlock> ENDERFACE = BLOCK_REGISTRY
            .registerBlock("enderface", EnderfaceBlock::new,
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 8)
                            .noOcclusion()
                            .isViewBlocking((pState, pLevel, pPos) -> false)
                            .requiredFeatures(EIOFeatureFlags.ENDERFACE))
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setTranslation("Ender IO")
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().cubeAll("enderface", EnderIO.rl("block/enderface")).renderType("translucent")))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<ProgressMachineBlock<?>> ALLOY_SMELTER = progressMachine("alloy_smelter",
            () -> EIOBlockEntities.ALLOY_SMELTER::get);

    public static final RegiliteBlock<ProgressMachineBlock<?>> PAINTING_MACHINE = progressMachine("painting_machine",
            () -> EIOBlockEntities.PAINTING_MACHINE::get);

    public static final RegiliteBlock<MachineBlock<?>> WIRED_CHARGER = machine("wired_charger",
            () -> EIOBlockEntities.WIRED_CHARGER::get);

    public static final RegiliteBlock<ProgressMachineBlock<?>> WIRELESS_CHARGER = progressMachine("wireless_charger",
            () -> EIOBlockEntities.WIRELESS_CHARGER::get);

    public static final RegiliteBlock<WirelessAntennaBlock> WIRELESS_CHARGER_ANTENNA = wirelessAntenna(
            "wireless_charger_antenna", "Pulsating Wireless Antenna");

    public static final RegiliteBlock<WirelessAntennaBlock> WIRELESS_CHARGER_ANTENNA_ADVANCED = wirelessAntenna(
            "wireless_charger_antenna_advanced", "Vibrant Wireless Antenna");

    public static final RegiliteBlock<LegacyMachineBlock> CREATIVE_POWER = BLOCK_REGISTRY
            .registerBlock("creative_power",
                    props -> new LegacyMachineBlock(EIOBlockEntities.CREATIVE_POWER::get, props),
                    BlockBehaviour.Properties.of())
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<ProgressMachineBlock<?>> STIRLING_GENERATOR = progressMachine(
            "stirling_generator", () -> EIOBlockEntities.STIRLING_GENERATOR::get);

    public static final RegiliteBlock<ProgressMachineBlock<?>> SAG_MILL = progressMachine("sag_mill",
            () -> EIOBlockEntities.SAG_MILL::get).setTranslation("SAG Mill");

    public static final RegiliteBlock<ProgressMachineBlock<?>> SLICE_AND_SPLICE = progressMachine("slice_and_splice",
            () -> EIOBlockEntities.SLICE_AND_SPLICE::get).setTranslation("Slice'N'Splice");

    public static final RegiliteBlock<ProgressMachineBlock<?>> IMPULSE_HOPPER = progressMachine("impulse_hopper",
            () -> EIOBlockEntities.IMPULSE_HOPPER::get).setTranslation("Impulse Hopper");

    public static final RegiliteBlock<ProgressMachineBlock<?>> SOUL_BINDER = progressMachine("soul_binder",
            () -> EIOBlockEntities.SOUL_BINDER::get).setTranslation("Soul Binder");

    public static final RegiliteBlock<ProgressMachineBlock<PoweredSpawnerBlockEntity>> POWERED_SPAWNER = BLOCK_REGISTRY
            .registerBlock("powered_spawner",
                    properties -> new ProgressMachineBlock<>(EIOBlockEntities.POWERED_SPAWNER::get, properties),
                    BlockBehaviour.Properties.of().strength(2.5f, 8))
            .setLootTable(
                    (l, t) -> MachinesLootTable.copyStandardComponentsWith(l, t, EIODataComponents.SOUL.get()))
            .setBlockStateProvider(MachineModelUtil::progressMachineBlock)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .createBlockItem(ITEM_REGISTRY,
                    b -> new BlockItem(b,
                            new Item.Properties().component(EIODataComponents.SOUL, Soul.EMPTY)),
                    item -> item.setTab(EIOCreativeTabs.MACHINES)
                            .addCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM, SoulCapabilityProviders.COMPONENT_SOUL_BINDABLE_PROVIDER));

    public static final RegiliteBlock<MindKillerBlock> MIND_KILLER =  BLOCK_REGISTRY
            .registerBlock("mind_killer", MindKillerBlock::new,
                BlockBehaviour.Properties.of()
                    .strength(2.5f, 8)
                    .isViewBlocking((pState, pLevel, pPos) -> false)
                    .noOcclusion())
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE, EIOTags.Blocks.MIND_KILLER)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<MachineBlock<VacuumChestBlockEntity>> VACUUM_CHEST = BLOCK_REGISTRY
            .registerBlock("vacuum_chest", p -> new MachineBlock<>(EIOBlockEntities.VACUUM_CHEST::get, p),
                    BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setLootTable(MachinesLootTable::copyComponents)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<MachineBlock<XPVacuumBlockEntity>> XP_VACUUM = BLOCK_REGISTRY
            .registerBlock("xp_vacuum", p -> new MachineBlock<>(EIOBlockEntities.XP_VACUUM::get, p),
                    BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setLootTable(MachinesLootTable::copyComponents)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .setTranslation("XP Vacuum")
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<TravelAnchorBlock<TravelAnchorBlockEntity>> TRAVEL_ANCHOR = BLOCK_REGISTRY
            .registerBlock("travel_anchor",
                    props -> new TravelAnchorBlock<>(EIOBlockEntities.TRAVEL_ANCHOR::get, props),
                    BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setLootTable(MachinesLootTable::copyComponents)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<PaintedTravelAnchorBlock> PAINTED_TRAVEL_ANCHOR = BLOCK_REGISTRY
            .registerBlock("painted_travel_anchor", PaintedTravelAnchorBlock::new,
                    BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setColorSupplier(() -> PaintedBlockColor::new)
            .setLootTable(DecorLootTable::withPaint)
            .setBlockStateProvider((prov, ctx) -> EIOBlockState.paintedBlock("painted_travel_anchor", prov, ctx.get(),
                    Blocks.DIRT, null)) // Any cube will do
            .createBlockItem(ITEM_REGISTRY, b -> new PaintedBlockItem(b, new Item.Properties()),
                    item -> item.setColorSupplier(() -> PaintedBlockColor::new));

    public static final Map<SolarPanelTier, RegiliteBlock<SolarPanelBlock>> SOLAR_PANELS = Util.make(() -> {
        Map<SolarPanelTier, RegiliteBlock<SolarPanelBlock>> panels = new HashMap<>();
        for (SolarPanelTier tier : SolarPanelTier.values()) {
            panels.put(tier, solarPanel(tier.name().toLowerCase(Locale.ROOT) + "_photovoltaic_module",
                    () -> EIOBlockEntities.SOLAR_PANELS.get(tier)::get, tier));
        }
        return ImmutableMap.copyOf(panels);
    });

    public static final Map<CapacitorTier, RegiliteBlock<CapacitorBankBlock>> CAPACITOR_BANKS = Util.make(() -> {
        Map<CapacitorTier, RegiliteBlock<CapacitorBankBlock>> banks = new HashMap<>();
        for (CapacitorTier tier : CapacitorTier.values()) {
            banks.put(tier, capacitorBank(tier.name().toLowerCase(Locale.ROOT) + "_capacitor_bank",
                    () -> EIOBlockEntities.CAPACITOR_BANKS.get(tier)::get, tier));
        }
        return ImmutableMap.copyOf(banks);
    });

    public static final RegiliteBlock<ProgressMachineBlock<?>> CRAFTER = progressMachine("crafter",
            () -> EIOBlockEntities.CRAFTER::get);

    public static final RegiliteBlock<ProgressMachineBlock<SoulEngineBlockEntity>> SOUL_ENGINE = BLOCK_REGISTRY
            .registerBlock("soul_engine", p -> new ProgressMachineBlock<>(EIOBlockEntities.SOUL_ENGINE::get, p),
                    BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider(MachineModelUtil::progressMachineBlock)
            .createBlockItem(ITEM_REGISTRY,
                    b -> new BlockItem(b,
                            new Item.Properties().component(EIODataComponents.SOUL, Soul.EMPTY)),
                    item -> item.setTab(EIOCreativeTabs.MACHINES)
                            .addCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM, SoulCapabilityProviders.COMPONENT_SOUL_BINDABLE_PROVIDER));

    public static final RegiliteBlock<ProgressMachineBlock<?>> DRAIN = progressMachine("drain",
            () -> EIOBlockEntities.DRAIN::get);

    public static final RegiliteBlock<NiardBlock> NIARD = baseMachine(BLOCK_REGISTRY.registerBlock("niard", NiardBlock::new,
            BlockBehaviour.Properties.of()
                .strength(2.5f, 8)
                .isViewBlocking((pState, pLevel, pPos) -> false)
                .noOcclusion()
                .requiredFeatures(EIOFeatureFlags.NIARD)
            )
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setLootTable(MachinesLootTable::copyComponents)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName())))),
        MachineModelUtil::machineBlock).setTranslation("Niard");

    public static final RegiliteBlock<VatBlock> VAT = baseMachine(BLOCK_REGISTRY.registerBlock("vat",
            VatBlock::new, BlockBehaviour.Properties.of().strength(2.5f, 8)),
            MachineModelUtil::machineBlock).setTranslation("VAT");

    public static final RegiliteBlock<BlockDetectorBlock> BLOCK_DETECTOR = BLOCK_REGISTRY
            .registerBlock("block_detector", BlockDetectorBlock::new,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OBSERVER))
            .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider((prov, ctx) -> prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName())))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<MachineBlock<XPObeliskBlockEntity>> XP_OBELISK = BLOCK_REGISTRY
            .registerBlock("xp_obelisk", props -> new MachineBlock<>(EIOBlockEntities.XP_OBELISK::get, props),
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 8)
                            .isViewBlocking((pState, pLevel, pPos) -> false)
                            .noOcclusion())
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .setTranslation("XP Obelisk")
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<ProgressMachineBlock> FARMING_STATION = BLOCK_REGISTRY.registerBlock(
            "farming_station", properties -> new ProgressMachineBlock(EIOBlockEntities.FARMING_STATION, properties),
            BlockBehaviour.Properties.of().strength(2.5f, 8).requiredFeatures(EIOFeatureFlags.FARMING_STATION))
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<MachineBlock<InhibitorObeliskBlockEntity>> INHIBITOR_OBELISK = BLOCK_REGISTRY
            .registerBlock("inhibitor_obelisk",
                    props -> new MachineBlock<>(EIOBlockEntities.INHIBITOR_OBELISK::get, props),
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 8)
                            .isViewBlocking((pState, pLevel, pPos) -> false)
                            .noOcclusion())
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<MachineBlock<AversionObeliskBlockEntity>> AVERSION_OBELISK = BLOCK_REGISTRY
            .registerBlock("aversion_obelisk",
                    props -> new MachineBlock<>(EIOBlockEntities.AVERSION_OBELISK::get, props),
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 8)
                            .isViewBlocking((pState, pLevel, pPos) -> false)
                            .noOcclusion())
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<MachineBlock<RelocatorObeliskBlockEntity>> RELOCATOR_OBELISK = BLOCK_REGISTRY
            .registerBlock("relocator_obelisk",
                    props -> new MachineBlock<>(EIOBlockEntities.RELOCATOR_OBELISK::get, props),
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 8)
                            .isViewBlocking((pState, pLevel, pPos) -> false)
                            .noOcclusion())
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<MachineBlock<AttractorObeliskBlockEntity>> ATTRACTOR_OBELISK = BLOCK_REGISTRY
            .registerBlock("attractor_obelisk",
                    props -> new MachineBlock<>(EIOBlockEntities.ATTRACTOR_OBELISK::get, props),
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 8)
                            .isViewBlocking((pState, pLevel, pPos) -> false)
                            .noOcclusion())
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<MachineBlock<WeatherObeliskBlockEntity>> WEATHER_OBELISK = BLOCK_REGISTRY
            .registerBlock("weather_obelisk",
                    props -> new MachineBlock<>(EIOBlockEntities.WEATHER_OBELISK::get, props),
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 8)
                            .isViewBlocking((pState, pLevel, pPos) -> false)
                            .noOcclusion())
            .setLootTable(MachinesLootTable::copyComponents)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                    prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
            .createBlockItem(ITEM_REGISTRY, item -> item.setTab((EIOCreativeTabs.MACHINES)));

    // used when single methods needs to be overridden in the block class
    private static <T extends MachineBlock<?>> RegiliteBlock<T> baseMachine(RegiliteBlock<T> machineBlock,
            BiConsumer<BlockStateProvider, DataGenContext<Block, T>> blockStateProvider) {
        return machineBlock.setLootTable(MachinesLootTable::copyComponents)
                .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
                .setBlockStateProvider(blockStateProvider)
                .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));
    }

    private static RegiliteBlock<MachineBlock<?>> machine(String name,
            Supplier<Supplier<BlockEntityType<? extends MachineBlockEntity>>> regiliteBlockEntity) {
        return baseMachine(
                BLOCK_REGISTRY.registerBlock(name, props -> new MachineBlock<>(regiliteBlockEntity.get(), props),
                        BlockBehaviour.Properties.of().strength(2.5f, 8)),
                MachineModelUtil::machineBlock);
    }

    private static RegiliteBlock<ProgressMachineBlock<?>> progressMachine(String name,
            Supplier<Supplier<BlockEntityType<? extends MachineBlockEntity>>> regiliteBlockEntity) {
        return baseMachine(BLOCK_REGISTRY.registerBlock(name,
                props -> new ProgressMachineBlock<>(regiliteBlockEntity.get(), props),
                BlockBehaviour.Properties.of().strength(2.5f, 8)), MachineModelUtil::progressMachineBlock);
    }

    private static RegiliteBlock<SolarPanelBlock> solarPanel(String name,
            Supplier<Supplier<BlockEntityType<? extends SolarPanelBlockEntity>>> regiliteBlockEntity, SolarPanelTier tier) {
        return BLOCK_REGISTRY
                .registerBlock(name, props -> new SolarPanelBlock(regiliteBlockEntity.get()::get, props, tier),
                        BlockBehaviour.Properties.of().strength(2.5f, 8))
                .setBlockStateProvider((prov, ctx) -> MachineModelUtil.solarPanel(prov, ctx, tier))
                .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE)
                .setLootTable(MachinesLootTable::copyComponents)
                .createBlockItem(ITEM_REGISTRY,
                        item -> item.setModelProvider((prov, ctx) -> MachineModelUtil.solarPanel(prov, ctx, tier))
                                .setTab(EIOCreativeTabs.MACHINES)
                                .addCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM, SoulCapabilityProviders.COMPONENT_SOUL_BINDABLE_PROVIDER));
    }

    private static RegiliteBlock<CapacitorBankBlock> capacitorBank(String name,
            Supplier<Supplier<BlockEntityType<? extends CapacitorBankBlockEntity>>> regiliteBlockEntity, CapacitorTier tier) {
        return BLOCK_REGISTRY
                .registerBlock(name, props -> new CapacitorBankBlock(props, regiliteBlockEntity.get()::get, tier),
                        BlockBehaviour.Properties.of().strength(2.5f, 8))
                .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                        prov.models().getExistingFile(EnderIO.rl(ctx.getName()))))
                .setLootTable(MachinesLootTable::copyComponents)
                .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE)
                .createBlockItem(ITEM_REGISTRY, block -> new CapacitorBankItem(block, new Item.Properties()),
                        item -> item.setModelProvider((prov, ctx) -> {
                        })
                                .setTab(EIOCreativeTabs.MACHINES)
                                .addCapability(Capabilities.EnergyStorage.ITEM,
                                        CapacitorBankItem.ENERGY_STORAGE_PROVIDER));
    }

    private static RegiliteBlock<WirelessAntennaBlock> wirelessAntenna(String name, String translation) {
        return BLOCK_REGISTRY
                .registerBlock(name, WirelessAntennaBlock::new,
                        BlockBehaviour.Properties.of()
                                .strength(2.5f, 8)
                                .isViewBlocking((pState, pLevel, pPos) -> false)
                                .noOcclusion())
                .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE,
                    EIOTags.Blocks.RANGE_EXTENDER)
                .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(),
                        prov.models().getExistingFile(EnderIO.rl("block/" + ctx.getName()))))
                .setTranslation(translation)
                .createBlockItem(ITEM_REGISTRY, item -> item.setTab((EIOCreativeTabs.MACHINES)));
    }

    public static void register(IEventBus bus) {
        BLOCK_REGISTRY.register(bus);
        ITEM_REGISTRY.register(bus);
    }
}
