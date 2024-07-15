package com.enderio.machines.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.client.paint.PaintedBlockColor;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.paint.item.PaintedBlockItem;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.loot.DecorLootTable;
import com.enderio.base.data.model.block.EIOBlockState;
import com.enderio.core.data.model.ModelHelper;
import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.block.BlockDetectorBlock;
import com.enderio.machines.common.block.CapacitorBankBlock;
import com.enderio.machines.common.block.EnchanterBlock;
import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.block.PaintedTravelAnchorBlock;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.block.SolarPanelBlock;
import com.enderio.machines.common.block.TravelAnchorBlock;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorTier;
import com.enderio.machines.common.blockentity.solar.SolarPanelBlockEntity;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.machines.common.item.CapacitorBankItem;
import com.enderio.machines.common.item.FluidTankItem;
import com.enderio.machines.data.loot.MachinesLootTable;
import com.enderio.machines.data.model.MachineModelUtil;
import com.enderio.regilite.data.DataGenContext;
import com.enderio.regilite.holder.RegiliteBlock;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.enderio.regilite.registry.BlockRegistry;
import com.enderio.regilite.registry.ItemRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
    private static final BlockRegistry BLOCK_REGISTRY = EnderIOMachines.REGILITE.blockRegistry();
    private static final ItemRegistry ITEM_REGISTRY = EnderIOMachines.REGILITE.itemRegistry();

    public static final RegiliteBlock<MachineBlock> FLUID_TANK = BLOCK_REGISTRY
        .registerBlock("fluid_tank", props -> new MachineBlock(MachineBlockEntities.FLUID_TANK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .withLootTable(MachinesLootTable::copyComponents)
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockStateProvider((prov, ctx) -> prov.horizontalBlock(ctx.get(), prov.models()
            .getBuilder(ctx.getName())
            .customLoader(CompositeModelBuilder::begin)
                .child("tank", ModelHelper.getExistingAsBuilder(prov.models(), EnderIOBase.loc(String.format("block/%s_body", ctx.getName()))))
                .child("overlay", ModelHelper.getExistingAsBuilder(prov.models(), EnderIOBase.loc("block/io_overlay")))
            .end()
            .texture("particle", EnderIOBase.loc("block/machine_side"))
        ))
        .withBlockItem(ITEM_REGISTRY,
            block -> new FluidTankItem(block, new Item.Properties(), 16000),
            item -> item
                .withModelProvider((prov, ctx) -> {})
                .withTab(EIOCreativeTabs.MACHINES)
                .withCapability(Capabilities.FluidHandler.ITEM, FluidTankItem.FLUID_HANDLER_PROVIDER)
        );

    public static final RegiliteBlock<MachineBlock> PRESSURIZED_FLUID_TANK = BLOCK_REGISTRY
        .registerBlock("pressurized_fluid_tank", props -> new MachineBlock(MachineBlockEntities.PRESSURIZED_FLUID_TANK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .withLootTable(MachinesLootTable::copyComponents)
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockStateProvider((prov, ctx) -> prov.horizontalBlock(ctx.get(), prov.models()
                .withExistingParent(ctx.getName(), prov.mcLoc("block/block"))
                .customLoader(CompositeModelBuilder::begin)
            .child("tank", ModelHelper.getExistingAsBuilder(prov.models(), EnderIOBase.loc(String.format("block/%s_body", ctx.getName()))))
                    .child("overlay", ModelHelper.getExistingAsBuilder(prov.models(), EnderIOBase.loc("block/io_overlay")))
                .end()
                .texture("particle", EnderIOBase.loc("block/machine_side"))
        ))
        .withBlockItem(ITEM_REGISTRY, (block) -> new FluidTankItem(block, new Item.Properties(), 32000),
            item -> item
                .withModelProvider((prov, ctx) -> {})
                .withTab(EIOCreativeTabs.MACHINES)
                .withCapability(Capabilities.FluidHandler.ITEM, FluidTankItem.FLUID_HANDLER_PROVIDER)
        );

    public static final RegiliteBlock<EnchanterBlock> ENCHANTER = BLOCK_REGISTRY
        .registerBlock("enchanter", EnchanterBlock::new,
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion().isViewBlocking((pState, pLevel, pPos) -> false))
        .withLootTable(MachinesLootTable::copyComponents)
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockStateProvider(MachineModelUtil::machineBlock)
        .withBlockItem(ITEM_REGISTRY, item -> item.withTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<ProgressMachineBlock> PRIMITIVE_ALLOY_SMELTER =
        progressMachine("primitive_alloy_smelter", () -> MachineBlockEntities.PRIMITIVE_ALLOY_SMELTER);

    public static final RegiliteBlock<ProgressMachineBlock> ALLOY_SMELTER =
        progressMachine("alloy_smelter", () -> MachineBlockEntities.ALLOY_SMELTER);

    public static final RegiliteBlock<ProgressMachineBlock> PAINTING_MACHINE =
        progressMachine("painting_machine", () -> MachineBlockEntities.PAINTING_MACHINE);

    public static final RegiliteBlock<MachineBlock> WIRED_CHARGER =
        machine("wired_charger", () -> MachineBlockEntities.WIRED_CHARGER);

    public static final RegiliteBlock<MachineBlock> CREATIVE_POWER = BLOCK_REGISTRY
        .registerBlock("creative_power", props -> new MachineBlock(MachineBlockEntities.CREATIVE_POWER, props), BlockBehaviour.Properties.of())
        .withBlockItem(ITEM_REGISTRY, item -> item.withTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<ProgressMachineBlock> STIRLING_GENERATOR =
        progressMachine("stirling_generator", () -> MachineBlockEntities.STIRLING_GENERATOR);

    public static final RegiliteBlock<ProgressMachineBlock> SAG_MILL =
        progressMachine("sag_mill", () -> MachineBlockEntities.SAG_MILL)
            .withTranslation("SAG Mill");

    public static final RegiliteBlock<ProgressMachineBlock> SLICE_AND_SPLICE =
        progressMachine("slice_and_splice", () -> MachineBlockEntities.SLICE_AND_SPLICE)
            .withTranslation("Slice'N'Splice");

    public static final RegiliteBlock<ProgressMachineBlock> IMPULSE_HOPPER =
        progressMachine("impulse_hopper", () -> MachineBlockEntities.IMPULSE_HOPPER)
            .withTranslation("Impulse Hopper");

    public static final RegiliteBlock<ProgressMachineBlock> SOUL_BINDER =
        progressMachine("soul_binder", () -> MachineBlockEntities.SOUL_BINDER)
            .withTranslation("Soul Binder");

    public static final RegiliteBlock<ProgressMachineBlock> POWERED_SPAWNER = BLOCK_REGISTRY
        .registerBlock("powered_spawner", properties -> new ProgressMachineBlock(MachineBlockEntities.POWERED_SPAWNER, properties),
            BlockBehaviour.Properties.of().strength(2.5f, 8))
        .withLootTable((l,t) -> MachinesLootTable.copyStandardComponentsWith(l, t, EIODataComponents.STORED_ENTITY.get()))
        .withBlockStateProvider(MachineModelUtil::progressMachineBlock)
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockItem(ITEM_REGISTRY, b -> new BlockItem(b, new Item.Properties().component(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY)),
            item -> item
                .withTab(EIOCreativeTabs.MACHINES)
                .withTags(EIOTags.Items.ENTITY_STORAGE)
        );

    public static final RegiliteBlock<MachineBlock> VACUUM_CHEST = BLOCK_REGISTRY
        .registerBlock("vacuum_chest", p -> new MachineBlock(MachineBlockEntities.VACUUM_CHEST, p),
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withLootTable(MachinesLootTable::copyComponents)
        .withBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .withBlockItem(ITEM_REGISTRY, item -> item.withTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<MachineBlock> XP_VACUUM = BLOCK_REGISTRY
        .registerBlock("xp_vacuum", p -> new MachineBlock(MachineBlockEntities.XP_VACUUM, p),
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withLootTable(MachinesLootTable::copyComponents)
        .withBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .withTranslation("XP Vacuum")
        .withBlockItem(ITEM_REGISTRY, item -> item.withTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<TravelAnchorBlock> TRAVEL_ANCHOR = BLOCK_REGISTRY
        .registerBlock("travel_anchor", TravelAnchorBlock::new, BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withLootTable(MachinesLootTable::copyComponents)
        .withBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .withBlockItem(ITEM_REGISTRY, item -> item.withTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<PaintedTravelAnchorBlock> PAINTED_TRAVEL_ANCHOR = BLOCK_REGISTRY
        .registerBlock("painted_travel_anchor", PaintedTravelAnchorBlock::new, BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockColor(() -> PaintedBlockColor::new)
        .withLootTable(DecorLootTable::withPaint)
        .withBlockStateProvider((prov, ctx) -> EIOBlockState.paintedBlock("painted_travel_anchor", prov, ctx.get(), Blocks.DIRT, null)) //Any cube will do
        .withBlockItem(
            ITEM_REGISTRY,
            b -> new PaintedBlockItem(b, new Item.Properties()),
            item -> item.withItemColor(() -> PaintedBlockColor::new));

    public static final Map<SolarPanelTier, RegiliteBlock<SolarPanelBlock>> SOLAR_PANELS = Util.make(() -> {
        Map<SolarPanelTier, RegiliteBlock<SolarPanelBlock>> panels = new HashMap<>();
        for (SolarPanelTier tier: SolarPanelTier.values()) {
            panels.put(tier, solarPanel(tier.name().toLowerCase(Locale.ROOT) + "_photovoltaic_module", () -> MachineBlockEntities.SOLAR_PANELS.get(tier), tier));
        }
        return ImmutableMap.copyOf(panels);
    });

    public static final Map<CapacitorTier, RegiliteBlock<CapacitorBankBlock>> CAPACITOR_BANKS = Util.make(() -> {
        Map<CapacitorTier, RegiliteBlock<CapacitorBankBlock>> banks = new HashMap<>();
        for (CapacitorTier tier: CapacitorTier.values()) {
            banks.put(tier, capacitorBank(tier.name().toLowerCase(Locale.ROOT) + "_capacitor_bank", () -> MachineBlockEntities.CAPACITOR_BANKS.get(tier), tier));
        }
        return ImmutableMap.copyOf(banks);
    });

    public static final RegiliteBlock<ProgressMachineBlock> CRAFTER =
        progressMachine("crafter", () -> MachineBlockEntities.CRAFTER);

    public static final RegiliteBlock<ProgressMachineBlock> SOUL_ENGINE = BLOCK_REGISTRY
        .registerBlock("soul_engine", p -> new ProgressMachineBlock(MachineBlockEntities.SOUL_ENGINE, p),
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .withLootTable(MachinesLootTable::copyComponents)
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockStateProvider(MachineModelUtil::progressMachineBlock)
        .withBlockItem(ITEM_REGISTRY, b -> new BlockItem(b, new Item.Properties().component(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY)),
            item -> item
                .withTab(EIOCreativeTabs.MACHINES)
                .withTags(EIOTags.Items.ENTITY_STORAGE)
        );

    public static final RegiliteBlock<ProgressMachineBlock> DRAIN =
        progressMachine("drain", () -> MachineBlockEntities.DRAIN);

    public static final RegiliteBlock<MachineBlock> VAT = machine("vat", () -> MachineBlockEntities.VAT).withTranslation("VAT");

    public static final RegiliteBlock<BlockDetectorBlock> BLOCK_DETECTOR = BLOCK_REGISTRY
        .registerBlock("block_detector", BlockDetectorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OBSERVER))
        .withTags(BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockStateProvider((prov, ctx) -> prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName())))
        .withBlockItem(ITEM_REGISTRY, item -> item.withTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<MachineBlock> XP_OBELISK = BLOCK_REGISTRY
        .registerBlock("xp_obelisk", props -> new MachineBlock(MachineBlockEntities.XP_OBELISK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .withLootTable(MachinesLootTable::copyComponents)
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .withTranslation("XP Obelisk")
        .withBlockItem(ITEM_REGISTRY, item -> item.withTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<MachineBlock> INHIBITOR_OBELISK = BLOCK_REGISTRY
        .registerBlock("inhibitor_obelisk", props -> new MachineBlock(MachineBlockEntities.INHIBITOR_OBELISK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .withLootTable(MachinesLootTable::copyComponents)
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .withBlockItem(ITEM_REGISTRY, item -> item.withTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<MachineBlock> AVERSION_OBELISK = BLOCK_REGISTRY
        .registerBlock("aversion_obelisk", props -> new MachineBlock(MachineBlockEntities.AVERSION_OBELISK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .withLootTable(MachinesLootTable::copyComponents)
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .withBlockItem(ITEM_REGISTRY, item -> item.withTab((EIOCreativeTabs.MACHINES)));

    public static final RegiliteBlock<MachineBlock> RELOCATOR_OBELISK = BLOCK_REGISTRY
        .registerBlock("relocator_obelisk", props -> new MachineBlock(MachineBlockEntities.RELOCATOR_OBELISK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .withLootTable(MachinesLootTable::copyComponents)
        .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .withBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .withBlockItem(ITEM_REGISTRY, item -> item.withTab((EIOCreativeTabs.MACHINES)));

    //used when single methods needs to be overridden in the block class
    private static <T extends MachineBlock> RegiliteBlock<T> baseMachine(RegiliteBlock<T> machineBlock,
        BiConsumer<BlockStateProvider, DataGenContext<Block, T>> blockStateProvider) {
        return machineBlock
            .withLootTable(MachinesLootTable::copyComponents)
            .withTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .withBlockStateProvider(blockStateProvider)
            .withBlockItem(ITEM_REGISTRY, item -> item
                .withTab(EIOCreativeTabs.MACHINES)
            );
    }

    private static RegiliteBlock<MachineBlock> machine(String name,
        Supplier<RegiliteBlockEntity<? extends MachineBlockEntity>> RegiliteBlockEntity) {
        return baseMachine(
            BLOCK_REGISTRY.registerBlock(name, props -> new MachineBlock(RegiliteBlockEntity.get(), props), BlockBehaviour.Properties.of().strength(2.5f, 8)),
            MachineModelUtil::machineBlock);
    }

    private static RegiliteBlock<ProgressMachineBlock> progressMachine(String name,
        Supplier<RegiliteBlockEntity<? extends MachineBlockEntity>> RegiliteBlockEntity) {
        return baseMachine(
            BLOCK_REGISTRY.registerBlock(name, props -> new ProgressMachineBlock(RegiliteBlockEntity.get(), props),
                BlockBehaviour.Properties.of().strength(2.5f, 8)),
            MachineModelUtil::progressMachineBlock);
    }

    private static RegiliteBlock<SolarPanelBlock> solarPanel(String name, Supplier<RegiliteBlockEntity<? extends SolarPanelBlockEntity>> RegiliteBlockEntity, SolarPanelTier tier) {
        return BLOCK_REGISTRY
            .registerBlock(name, props -> new SolarPanelBlock(RegiliteBlockEntity.get(), props, tier),
                BlockBehaviour.Properties.of().strength(2.5f, 8))
            .withBlockStateProvider((prov, ctx) -> MachineModelUtil.solarPanel(prov, ctx, tier))
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withLootTable(MachinesLootTable::copyComponents)
            .withBlockItem(ITEM_REGISTRY, item -> item
                .withModelProvider((prov, ctx) -> MachineModelUtil.solarPanel(prov, ctx, tier))
                .withTab(EIOCreativeTabs.MACHINES)
                .withTags(EIOTags.Items.ENTITY_STORAGE)
            );
    }

    private static RegiliteBlock<CapacitorBankBlock> capacitorBank(String name, Supplier<RegiliteBlockEntity<? extends CapacitorBankBlockEntity>> RegiliteBlockEntity, CapacitorTier tier) {
        return BLOCK_REGISTRY
            .registerBlock(name, props -> new CapacitorBankBlock(props, RegiliteBlockEntity.get(), tier),
                BlockBehaviour.Properties.of().strength(2.5f, 8))
            .withBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc(ctx.getName()))))
            .withLootTable(MachinesLootTable::copyComponents)
            .withTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withBlockItem(ITEM_REGISTRY,
                block -> new CapacitorBankItem(block, new Item.Properties()),
                item -> item
                    .withModelProvider((prov, ctx) -> {})
                    .withTab(EIOCreativeTabs.MACHINES)
                    .withCapability(Capabilities.EnergyStorage.ITEM, CapacitorBankItem.ENERGY_STORAGE_PROVIDER)
            );
    }

    public static void register(IEventBus bus) {
        BLOCK_REGISTRY.register(bus);
        ITEM_REGISTRY.register(bus);
    }
}
