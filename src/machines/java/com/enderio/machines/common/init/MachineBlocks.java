package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.core.data.model.EIOModel;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.block.CapacitorBankBlock;
import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.block.SolarPanelBlock;
import com.enderio.machines.common.block.TravelAnchorBlock;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorTier;
import com.enderio.machines.common.blockentity.solar.SolarPanelBlockEntity;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.machines.common.item.BoundSoulBlockItem;
import com.enderio.machines.common.item.CapacitorBankItem;
import com.enderio.machines.common.item.FluidTankItem;
import com.enderio.machines.data.loot.MachinesLootTable;
import com.enderio.machines.data.model.MachineModelUtil;
import com.enderio.regilite.data.DataGenContext;
import com.enderio.regilite.data.RegiliteBlockStateProvider;
import com.enderio.regilite.holder.RegiliteBlock;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.enderio.regilite.registry.BlockRegistry;
import com.enderio.regilite.registry.ItemRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MachineBlocks {
    private static final BlockRegistry BLOCK_REGISTRY = BlockRegistry.createRegistry(EnderIO.MODID);
    private static final ItemRegistry ITEM_REGISTRY = ItemRegistry.createRegistry(EnderIO.MODID);

    public static final RegiliteBlock<MachineBlock> FLUID_TANK = BLOCK_REGISTRY
        .registerBlock("fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.FLUID_TANK),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .setLootTable(MachinesLootTable::copyNBT)
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setBlockStateProvider((prov, ctx) -> prov.horizontalBlock(ctx.get(), prov.models()
            .getBuilder(ctx.getName())
            .customLoader(CompositeModelBuilder::begin)
                .child("tank", EIOModel.getExistingParent(prov.models(), EnderIO.loc(String.format("block/%s_body", ctx.getName()))))
                .child("overlay", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/io_overlay")))
            .end()
            .texture("particle", EnderIO.loc("block/machine_side"))
        ))
        .createBlockItem(ITEM_REGISTRY, (block) -> new FluidTankItem(block, new Item.Properties(), 16000),
            item -> item
                .setModelProvider((prov, ctx) -> {})
                .setTab(EIOCreativeTabs.MACHINES)
        );

    public static final RegiliteBlock<MachineBlock> PRESSURIZED_FLUID_TANK = BLOCK_REGISTRY
        .registerBlock("pressurized_fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.PRESSURIZED_FLUID_TANK),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .setLootTable(MachinesLootTable::copyNBT)
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setBlockStateProvider((prov, ctx) -> prov.horizontalBlock(ctx.get(), prov.models()
                .withExistingParent(ctx.getName(), prov.mcLoc("block/block"))
                .customLoader(CompositeModelBuilder::begin)
            .child("tank", EIOModel.getExistingParent(prov.models(), EnderIO.loc(String.format("block/%s_body", ctx.getName()))))
                    .child("overlay", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/io_overlay")))
                .end()
                .texture("particle", EnderIO.loc("block/machine_side"))
        ))
        .createBlockItem(ITEM_REGISTRY, (block) -> new FluidTankItem(block, new Item.Properties(), 32000),
            item -> item
                .setModelProvider((prov, ctx) -> {})
                .setTab(EIOCreativeTabs.MACHINES)
        );

    public static final RegiliteBlock<MachineBlock> ENCHANTER = BLOCK_REGISTRY
        .registerBlock("enchanter", props -> new MachineBlock(props, MachineBlockEntities.ENCHANTER),
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion().isViewBlocking((pState, pLevel, pPos) -> false))
        .setLootTable(MachinesLootTable::copyNBT)
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setBlockStateProvider(MachineModelUtil::machineBlock)
        .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<ProgressMachineBlock> PRIMITIVE_ALLOY_SMELTER =
        progressMachine("primitive_alloy_smelter", () -> MachineBlockEntities.PRIMITIVE_ALLOY_SMELTER);

    public static final RegiliteBlock<ProgressMachineBlock> ALLOY_SMELTER =
        progressMachine("alloy_smelter", () -> MachineBlockEntities.ALLOY_SMELTER);

    public static final RegiliteBlock<ProgressMachineBlock> PAINTING_MACHINE =
        progressMachine("painting_machine", () -> MachineBlockEntities.PAINTING_MACHINE);

    public static final RegiliteBlock<MachineBlock> WIRED_CHARGER =
        machine("wired_charger", () -> MachineBlockEntities.WIRED_CHARGER);

    public static final RegiliteBlock<MachineBlock> CREATIVE_POWER = BLOCK_REGISTRY
        .registerBlock("creative_power", props -> new MachineBlock(props, MachineBlockEntities.CREATIVE_POWER), BlockBehaviour.Properties.of())
        .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<ProgressMachineBlock> STIRLING_GENERATOR =
        progressMachine("stirling_generator", () -> MachineBlockEntities.STIRLING_GENERATOR);

    public static final RegiliteBlock<ProgressMachineBlock> SAG_MILL =
        progressMachine("sag_mill", () -> MachineBlockEntities.SAG_MILL)
            .setTranslation("SAG Mill");

    public static final RegiliteBlock<ProgressMachineBlock> SLICE_AND_SPLICE =
        progressMachine("slice_and_splice", () -> MachineBlockEntities.SLICE_AND_SPLICE)
            .setTranslation("Slice'N'Splice");

    public static final RegiliteBlock<ProgressMachineBlock> IMPULSE_HOPPER =
        progressMachine("impulse_hopper", () -> MachineBlockEntities.IMPULSE_HOPPER)
            .setTranslation("Impulse Hopper");

    public static final RegiliteBlock<ProgressMachineBlock> SOUL_BINDER =
        progressMachine("soul_binder", () -> MachineBlockEntities.SOUL_BINDER)
            .setTranslation("Soul Binder");

    public static final RegiliteBlock<ProgressMachineBlock> POWERED_SPAWNER = BLOCK_REGISTRY
        .registerBlock("powered_spawner", properties -> new ProgressMachineBlock(properties, MachineBlockEntities.POWERED_SPAWNER),
            BlockBehaviour.Properties.of().strength(2.5f, 8))
        .setLootTable((l,t) -> MachinesLootTable.copyNBTSingleCap(l, t, MachineNBTKeys.ENTITY_STORAGE))
        .setBlockStateProvider(MachineModelUtil::progressMachineBlock)
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .createBlockItem(ITEM_REGISTRY,
            block -> new BoundSoulBlockItem(block, new Item.Properties()),
            item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<MachineBlock> VACUUM_CHEST = BLOCK_REGISTRY
        .registerBlock("vacuum_chest", p -> new MachineBlock(p, MachineBlockEntities.VACUUM_CHEST),
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setLootTable(MachinesLootTable::copyNBT)
        .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/" + ctx.getName()))))
        .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<MachineBlock> XP_VACUUM = BLOCK_REGISTRY
        .registerBlock("xp_vacuum", p -> new MachineBlock(p, MachineBlockEntities.XP_VACUUM),
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setLootTable(MachinesLootTable::copyNBT)
        .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/" + ctx.getName()))))
        .setTranslation("XP Vacuum")
        .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<TravelAnchorBlock> TRAVEL_ANCHOR = BLOCK_REGISTRY
        .registerBlock("travel_anchor", TravelAnchorBlock::new, BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setLootTable(MachinesLootTable::copyNBT)
        .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/" + ctx.getName()))))
        .createBlockItem(ITEM_REGISTRY, item -> item.setTab(EIOCreativeTabs.MACHINES));

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
        .registerBlock("soul_engine", p -> new ProgressMachineBlock(p, MachineBlockEntities.SOUL_ENGINE),
            BlockBehaviour.Properties.of().strength(2.5f, 8).noOcclusion())
        .setLootTable(MachinesLootTable::copyNBT)
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setBlockStateProvider(MachineModelUtil::progressMachineBlock)
        .createBlockItem(ITEM_REGISTRY,
            b -> new BoundSoulBlockItem(b, new Item.Properties()),
            item -> item.setTab(EIOCreativeTabs.MACHINES));

    public static final RegiliteBlock<ProgressMachineBlock> DRAIN =
        progressMachine("drain", () -> MachineBlockEntities.DRAIN);

    public static final RegiliteBlock<MachineBlock> XP_OBELISK = BLOCK_REGISTRY
        .registerBlock("xp_obelisk", props -> new MachineBlock(props, MachineBlockEntities.XP_OBELISK),
            BlockBehaviour.Properties.of().strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .setLootTable(MachinesLootTable::copyNBT)
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/" + ctx.getName()))))
        .setTranslation("XP Obelisk")
        .createBlockItem(ITEM_REGISTRY, item -> item.setTab((EIOCreativeTabs.MACHINES)));

    //used when single methods needs to be overridden in the block class
    private static <T extends MachineBlock> RegiliteBlock<T> baseMachine(RegiliteBlock<T> machineBlock,
        BiConsumer<BlockStateProvider, DataGenContext<Block, T>> blockStateProvider) {
        return machineBlock
            .setLootTable(MachinesLootTable::copyNBT)
            .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setBlockStateProvider(blockStateProvider)
            .createBlockItem(ITEM_REGISTRY, item -> item
                .setTab(EIOCreativeTabs.MACHINES)
            );
    }

    private static RegiliteBlock<MachineBlock> machine(String name,
        Supplier<RegiliteBlockEntity<? extends MachineBlockEntity>> RegiliteBlockEntity) {
        return baseMachine(
            BLOCK_REGISTRY.registerBlock(name, props -> new MachineBlock(props, RegiliteBlockEntity.get()), BlockBehaviour.Properties.of().strength(2.5f, 8)),
            MachineModelUtil::machineBlock);
    }

    private static RegiliteBlock<ProgressMachineBlock> progressMachine(String name,
        Supplier<RegiliteBlockEntity<? extends MachineBlockEntity>> RegiliteBlockEntity) {
        return baseMachine(
            BLOCK_REGISTRY.registerBlock(name, props -> new ProgressMachineBlock(props, RegiliteBlockEntity.get()), BlockBehaviour.Properties.of().strength(2.5f, 8)),
            MachineModelUtil::progressMachineBlock);
    }

    private static RegiliteBlock<SolarPanelBlock> solarPanel(String name, Supplier<RegiliteBlockEntity<? extends SolarPanelBlockEntity>> RegiliteBlockEntity, SolarPanelTier tier) {
        return BLOCK_REGISTRY
            .registerBlock(name, props -> new SolarPanelBlock(props, RegiliteBlockEntity.get(), tier),
                BlockBehaviour.Properties.of().strength(2.5f, 8))
            .setBlockStateProvider((prov, ctx) -> MachineModelUtil.solarPanel(prov, ctx, tier))
            .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .createBlockItem(ITEM_REGISTRY, item -> item
                .setModelProvider((prov, ctx) -> MachineModelUtil.solarPanel(prov, ctx, tier))
                .setTab(EIOCreativeTabs.MACHINES)
            );
    }

    private static RegiliteBlock<CapacitorBankBlock> capacitorBank(String name, Supplier<RegiliteBlockEntity<? extends CapacitorBankBlockEntity>> RegiliteBlockEntity, CapacitorTier tier) {
        return BLOCK_REGISTRY
            .registerBlock(name, props -> new CapacitorBankBlock(props, RegiliteBlockEntity.get(), tier),
                BlockBehaviour.Properties.of().strength(2.5f, 8))
            .setBlockStateProvider((prov, ctx) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc(ctx.getName()))))
            .setLootTable(MachinesLootTable::copyNBT)
            .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE)
            .createBlockItem(ITEM_REGISTRY,
                block -> new CapacitorBankItem(block, new Item.Properties()),
                item -> item
                    .setModelProvider((prov, ctx) -> {})
                    .setTab(EIOCreativeTabs.MACHINES)
            );
    }

    public static void register() {}
}
