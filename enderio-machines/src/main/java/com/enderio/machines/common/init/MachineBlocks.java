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
import com.enderio.regilite.blockentities.DeferredBlockEntityType;
import com.enderio.regilite.blocks.BlockBuilder;
import com.enderio.regilite.blocks.RegiliteBlocks;
import com.enderio.regilite.data.DataGenContext;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MachineBlocks {
    private static final RegiliteBlocks BLOCK_REGISTRY = EnderIOMachines.REGILITE.blocks();

    public static final DeferredBlock<MachineBlock> FLUID_TANK = BLOCK_REGISTRY
        .create("fluid_tank", props -> new MachineBlock(MachineBlockEntities.FLUID_TANK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .lootTable(MachinesLootTable::copyComponents)
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockState((prov, ctx) -> prov.horizontalBlock(ctx.get(), prov.models()
            .getBuilder(ctx.getName())
            .customLoader(CompositeModelBuilder::begin)
                .child("tank", ModelHelper.getExistingAsBuilder(prov.models(), EnderIOBase.loc(String.format("block/%s_body", ctx.getName()))))
                .child("overlay", ModelHelper.getExistingAsBuilder(prov.models(), EnderIOBase.loc("block/io_overlay")))
            .end()
            .texture("particle", EnderIOBase.loc("block/machine_side"))
        ))
        .createBlockItem(
            block -> new FluidTankItem(block, new Item.Properties(), 16000),
            item -> item
                .model((prov, ctx) -> {})
                .tab(EIOCreativeTabs.MACHINES)
                .capability(Capabilities.FluidHandler.ITEM, FluidTankItem.FLUID_HANDLER_PROVIDER)
        )
        .finish();

    public static final DeferredBlock<MachineBlock> PRESSURIZED_FLUID_TANK = BLOCK_REGISTRY
        .create("pressurized_fluid_tank", props -> new MachineBlock(MachineBlockEntities.PRESSURIZED_FLUID_TANK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .lootTable(MachinesLootTable::copyComponents)
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockState((prov, ctx) -> prov.horizontalBlock(ctx.get(), prov.models()
                .withExistingParent(ctx.getName(), prov.mcLoc("block/block"))
                .customLoader(CompositeModelBuilder::begin)
            .child("tank", ModelHelper.getExistingAsBuilder(prov.models(), EnderIOBase.loc(String.format("block/%s_body", ctx.getName()))))
                    .child("overlay", ModelHelper.getExistingAsBuilder(prov.models(), EnderIOBase.loc("block/io_overlay")))
                .end()
                .texture("particle", EnderIOBase.loc("block/machine_side"))
        ))
        .createBlockItem((block) -> new FluidTankItem(block, new Item.Properties(), 32000),
            item -> item
                .model((prov, ctx) -> {})
                .tab(EIOCreativeTabs.MACHINES)
                .capability(Capabilities.FluidHandler.ITEM, FluidTankItem.FLUID_HANDLER_PROVIDER)
        )
        .finish();

    public static final DeferredBlock<EnchanterBlock> ENCHANTER = BLOCK_REGISTRY
        .create("enchanter", EnchanterBlock::new,
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion().isViewBlocking((pState, pLevel, pPos) -> false))
        .lootTable(MachinesLootTable::copyComponents)
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockState(MachineModelUtil::machineBlock)
        .createSimpleBlockItem(item -> item.tab(EIOCreativeTabs.MACHINES))
        .finish();

    public static final DeferredBlock<ProgressMachineBlock> PRIMITIVE_ALLOY_SMELTER =
        progressMachine("primitive_alloy_smelter", () -> MachineBlockEntities.PRIMITIVE_ALLOY_SMELTER)
            .finish();

    public static final DeferredBlock<ProgressMachineBlock> ALLOY_SMELTER =
        progressMachine("alloy_smelter", () -> MachineBlockEntities.ALLOY_SMELTER)
            .finish();

    public static final DeferredBlock<ProgressMachineBlock> PAINTING_MACHINE =
        progressMachine("painting_machine", () -> MachineBlockEntities.PAINTING_MACHINE)
            .finish();

    public static final DeferredBlock<MachineBlock> WIRED_CHARGER =
        machine("wired_charger", () -> MachineBlockEntities.WIRED_CHARGER)
            .finish();

    public static final DeferredBlock<MachineBlock> CREATIVE_POWER = BLOCK_REGISTRY
        .create("creative_power", props -> new MachineBlock(MachineBlockEntities.CREATIVE_POWER, props), BlockBehaviour.Properties.of())
        .createSimpleBlockItem(item -> item.tab(EIOCreativeTabs.MACHINES))
        .finish();

    public static final DeferredBlock<ProgressMachineBlock> STIRLING_GENERATOR =
        progressMachine("stirling_generator", () -> MachineBlockEntities.STIRLING_GENERATOR)
            .finish();

    public static final DeferredBlock<ProgressMachineBlock> SAG_MILL =
        progressMachine("sag_mill", () -> MachineBlockEntities.SAG_MILL)
            .translation("SAG Mill")
            .finish();

    public static final DeferredBlock<ProgressMachineBlock> SLICE_AND_SPLICE =
        progressMachine("slice_and_splice", () -> MachineBlockEntities.SLICE_AND_SPLICE)
            .translation("Slice'N'Splice")
            .finish();

    public static final DeferredBlock<ProgressMachineBlock> IMPULSE_HOPPER =
        progressMachine("impulse_hopper", () -> MachineBlockEntities.IMPULSE_HOPPER)
            .translation("Impulse Hopper")
            .finish();

    public static final DeferredBlock<ProgressMachineBlock> SOUL_BINDER =
        progressMachine("soul_binder", () -> MachineBlockEntities.SOUL_BINDER)
            .translation("Soul Binder")
            .finish();

    public static final DeferredBlock<ProgressMachineBlock> POWERED_SPAWNER = BLOCK_REGISTRY
        .create("powered_spawner", properties -> new ProgressMachineBlock(MachineBlockEntities.POWERED_SPAWNER, properties),
            BlockBehaviour.Properties.of().strength(2.5f, 8))
        .lootTable((l,t) -> MachinesLootTable.copyStandardComponentsWith(l, t, EIODataComponents.STORED_ENTITY.get()))
        .blockState(MachineModelUtil::progressMachineBlock)
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .createBlockItem(b -> new BlockItem(b, new Item.Properties().component(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY)),
            item -> item
                .tab(EIOCreativeTabs.MACHINES)
                .tags(EIOTags.Items.ENTITY_STORAGE)
        )
        .finish();

    public static final DeferredBlock<MachineBlock> VACUUM_CHEST = BLOCK_REGISTRY
        .create("vacuum_chest", p -> new MachineBlock(MachineBlockEntities.VACUUM_CHEST, p),
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .lootTable(MachinesLootTable::copyComponents)
        .blockState((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .createSimpleBlockItem(item -> item.tab(EIOCreativeTabs.MACHINES))
        .finish();

    public static final DeferredBlock<MachineBlock> XP_VACUUM = BLOCK_REGISTRY
        .create("xp_vacuum", p -> new MachineBlock(MachineBlockEntities.XP_VACUUM, p),
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .lootTable(MachinesLootTable::copyComponents)
        .blockState((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .translation("XP Vacuum")
        .createSimpleBlockItem(item -> item.tab(EIOCreativeTabs.MACHINES))
        .finish();

    public static final DeferredBlock<TravelAnchorBlock> TRAVEL_ANCHOR = BLOCK_REGISTRY
        .create("travel_anchor", TravelAnchorBlock::new, BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .lootTable(MachinesLootTable::copyComponents)
        .blockState((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .createSimpleBlockItem(item -> item.tab(EIOCreativeTabs.MACHINES))
        .finish();

    public static final DeferredBlock<PaintedTravelAnchorBlock> PAINTED_TRAVEL_ANCHOR = BLOCK_REGISTRY
        .create("painted_travel_anchor", PaintedTravelAnchorBlock::new, BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockColor(() -> PaintedBlockColor::new)
        .lootTable(DecorLootTable::withPaint)
        .blockState((prov, ctx) -> EIOBlockState.paintedBlock("painted_travel_anchor", prov, ctx.get(), Blocks.DIRT, null)) //Any cube will do
        .createBlockItem(
            b -> new PaintedBlockItem(b, new Item.Properties()),
            item -> item.itemColor(() -> PaintedBlockColor::new))
        .finish();

    public static final Map<SolarPanelTier, DeferredBlock<SolarPanelBlock>> SOLAR_PANELS = Util.make(() -> {
        Map<SolarPanelTier, DeferredBlock<SolarPanelBlock>> panels = new HashMap<>();
        for (SolarPanelTier tier: SolarPanelTier.values()) {
            panels.put(tier, solarPanel(tier.name().toLowerCase(Locale.ROOT) + "_photovoltaic_module", () -> MachineBlockEntities.SOLAR_PANELS.get(tier), tier));
        }
        return ImmutableMap.copyOf(panels);
    });

    public static final Map<CapacitorTier, DeferredBlock<CapacitorBankBlock>> CAPACITOR_BANKS = Util.make(() -> {
        Map<CapacitorTier, DeferredBlock<CapacitorBankBlock>> banks = new HashMap<>();
        for (CapacitorTier tier: CapacitorTier.values()) {
            banks.put(tier, capacitorBank(tier.name().toLowerCase(Locale.ROOT) + "_capacitor_bank", () -> MachineBlockEntities.CAPACITOR_BANKS.get(tier), tier));
        }
        return ImmutableMap.copyOf(banks);
    });

    public static final DeferredBlock<ProgressMachineBlock> CRAFTER =
        progressMachine("crafter", () -> MachineBlockEntities.CRAFTER)
            .finish();

    public static final DeferredBlock<ProgressMachineBlock> SOUL_ENGINE = BLOCK_REGISTRY
        .create("soul_engine", p -> new ProgressMachineBlock(MachineBlockEntities.SOUL_ENGINE, p),
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .lootTable(MachinesLootTable::copyComponents)
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockState(MachineModelUtil::progressMachineBlock)
        .createBlockItem(b -> new BlockItem(b, new Item.Properties().component(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY)),
            item -> item
                .tab(EIOCreativeTabs.MACHINES)
                .tags(EIOTags.Items.ENTITY_STORAGE)
        )
        .finish();

    public static final DeferredBlock<ProgressMachineBlock> DRAIN = 
        progressMachine("drain", () -> MachineBlockEntities.DRAIN)
            .finish();

    public static final DeferredBlock<MachineBlock> VAT = machine("vat", () -> MachineBlockEntities.VAT).translation("VAT").finish();

    public static final DeferredBlock<BlockDetectorBlock> BLOCK_DETECTOR = BLOCK_REGISTRY
        .create("block_detector", BlockDetectorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OBSERVER))
        .tags(BlockTags.MINEABLE_WITH_PICKAXE)
        .blockState((prov, ctx) -> prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName())))
        .createSimpleBlockItem(item -> item.tab((EIOCreativeTabs.MACHINES)))
        .finish();

    public static final DeferredBlock<MachineBlock> XP_OBELISK = BLOCK_REGISTRY
        .create("xp_obelisk", props -> new MachineBlock(MachineBlockEntities.XP_OBELISK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .lootTable(MachinesLootTable::copyComponents)
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockState((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .translation("XP Obelisk")
        .createSimpleBlockItem(item -> item.tab((EIOCreativeTabs.MACHINES)))
        .finish();

    public static final DeferredBlock<MachineBlock> INHIBITOR_OBELISK = BLOCK_REGISTRY
        .create("inhibitor_obelisk", props -> new MachineBlock(MachineBlockEntities.INHIBITOR_OBELISK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .lootTable(MachinesLootTable::copyComponents)
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockState((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .createSimpleBlockItem(item -> item.tab((EIOCreativeTabs.MACHINES)))
        .finish();

    public static final DeferredBlock<MachineBlock> AVERSION_OBELISK = BLOCK_REGISTRY
        .create("aversion_obelisk", props -> new MachineBlock(MachineBlockEntities.AVERSION_OBELISK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .lootTable(MachinesLootTable::copyComponents)
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockState((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .createSimpleBlockItem(item -> item.tab((EIOCreativeTabs.MACHINES)))
        .finish();

    public static final DeferredBlock<MachineBlock> RELOCATOR_OBELISK = BLOCK_REGISTRY
        .create("relocator_obelisk", props -> new MachineBlock(MachineBlockEntities.RELOCATOR_OBELISK, props),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .lootTable(MachinesLootTable::copyComponents)
        .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockState((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc("block/" + ctx.getName()))))
        .createSimpleBlockItem(item -> item.tab((EIOCreativeTabs.MACHINES)))
        .finish();

    //used when single methods needs to be overridden in the block class
    private static <T extends MachineBlock> BlockBuilder<T> baseMachine(BlockBuilder<T> machineBlock,
        BiConsumer<BlockStateProvider, DataGenContext<Block, T>> blockStateProvider) {
        return machineBlock
            .lootTable(MachinesLootTable::copyComponents)
            .tags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .blockState(blockStateProvider)
            .createSimpleBlockItem(item -> item
                .tab(EIOCreativeTabs.MACHINES)
            );
    }

    private static BlockBuilder<MachineBlock> machine(String name,
        Supplier<DeferredBlockEntityType<? extends MachineBlockEntity>> RegiliteBlockEntity) {
        return baseMachine(
            BLOCK_REGISTRY.create(name, props -> new MachineBlock(RegiliteBlockEntity.get(), props), BlockBehaviour.Properties.of().strength(2.5f, 8)),
            MachineModelUtil::machineBlock);
    }

    private static BlockBuilder<ProgressMachineBlock> progressMachine(String name,
        Supplier<DeferredBlockEntityType<? extends MachineBlockEntity>> RegiliteBlockEntity) {
        return baseMachine(
            BLOCK_REGISTRY.create(name, props -> new ProgressMachineBlock(RegiliteBlockEntity.get(), props),
                BlockBehaviour.Properties.of().strength(2.5f, 8)),
            MachineModelUtil::progressMachineBlock);
    }

    private static DeferredBlock<SolarPanelBlock> solarPanel(String name, Supplier<DeferredBlockEntityType<? extends SolarPanelBlockEntity>> RegiliteBlockEntity, SolarPanelTier tier) {
        return BLOCK_REGISTRY
            .create(name, props -> new SolarPanelBlock(RegiliteBlockEntity.get(), props, tier),
                BlockBehaviour.Properties.of().strength(2.5f, 8))
            .blockState((prov, ctx) -> MachineModelUtil.solarPanel(prov, ctx, tier))
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .lootTable(MachinesLootTable::copyComponents)
            .createSimpleBlockItem(item -> item
                .model((prov, ctx) -> MachineModelUtil.solarPanel(prov, ctx, tier))
                .tab(EIOCreativeTabs.MACHINES)
                .tags(EIOTags.Items.ENTITY_STORAGE)
            )
            .finish();
    }

    private static DeferredBlock<CapacitorBankBlock> capacitorBank(String name, Supplier<DeferredBlockEntityType<? extends CapacitorBankBlockEntity>> RegiliteBlockEntity, CapacitorTier tier) {
        return BLOCK_REGISTRY
            .create(name, props -> new CapacitorBankBlock(props, RegiliteBlockEntity.get(), tier),
                BlockBehaviour.Properties.of().strength(2.5f, 8))
            .blockState((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIOBase.loc(ctx.getName()))))
            .lootTable(MachinesLootTable::copyComponents)
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .createBlockItem(
                block -> new CapacitorBankItem(block, new Item.Properties()),
                item -> item
                    .model((prov, ctx) -> {})
                    .tab(EIOCreativeTabs.MACHINES)
                    .capability(Capabilities.EnergyStorage.ITEM, CapacitorBankItem.ENERGY_STORAGE_PROVIDER)
            )
            .finish();
    }

    public static void register() {
    }
}
