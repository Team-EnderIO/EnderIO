package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.base.client.renderer.PaintedBlockColor;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.base.data.loot.DecorLootTable;
import com.enderio.base.data.model.block.EIOBlockState;
import com.enderio.core.data.model.EIOModel;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.block.CapacitorBankBlock;
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
import com.enderio.machines.common.item.BoundSoulBlockItem;
import com.enderio.machines.common.item.CapacitorBankItem;
import com.enderio.machines.common.item.FluidTankItem;
import com.enderio.machines.data.loot.MachinesLootTable;
import com.enderio.machines.data.model.MachineModelUtil;
import com.google.common.collect.ImmutableMap;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.Util;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class MachineBlocks {
    private static final Registrate REGISTRATE = EnderIO.registrate();
    private static final VoxelShape OBELISK_SHAPE = Shapes.box(0.32, 0.0, 0.32, 0.68, 0.375, 0.68); // Obelisk's Custom Voxel Shape

    public static final BlockEntry<MachineBlock> FLUID_TANK = REGISTRATE
        .block("fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.FLUID_TANK))
        .properties(props -> props.strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .loot(MachinesLootTable::copyNBT)
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), prov.models()
            .getBuilder(ctx.getName())
            .customLoader(CompositeModelBuilder::begin)
                .child("tank", EIOModel.getExistingParent(prov.models(), EnderIO.loc(String.format("block/%s_body", ctx.getName()))))
                .child("overlay", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/io_overlay")))
            .end()
            .texture("particle", EnderIO.loc("block/machine_side"))
        ))
        .item((MachineBlock block, Item.Properties props) -> new FluidTankItem(block, props, 16000))
        .model((ctx, prov) -> {})
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> PRESSURIZED_FLUID_TANK = REGISTRATE
        .block("pressurized_fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.PRESSURIZED_FLUID_TANK))
        .properties(props -> props.strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .loot(MachinesLootTable::copyNBT)
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), prov.models()
                .withExistingParent(ctx.getName(), prov.mcLoc("block/block"))
                .customLoader(CompositeModelBuilder::begin)
            .child("tank", EIOModel.getExistingParent(prov.models(), EnderIO.loc(String.format("block/%s_body", ctx.getName()))))
                    .child("overlay", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/io_overlay")))
                .end()
                .texture("particle", EnderIO.loc("block/machine_side"))
        ))
        .item((MachineBlock block, Item.Properties props) -> new FluidTankItem(block, props, 32000))
        .model((ctx, prov) -> {})
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> ENCHANTER = REGISTRATE
        .block("enchanter", props -> new MachineBlock(props, MachineBlockEntities.ENCHANTER))
        .properties(props -> props.strength(2.5f, 8).noOcclusion().isViewBlocking((pState, pLevel, pPos) -> false))
        .loot(MachinesLootTable::copyNBT)
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockstate(MachineModelUtil::machineBlock)
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> PRIMITIVE_ALLOY_SMELTER = progressMachine("primitive_alloy_smelter", () -> MachineBlockEntities.PRIMITIVE_ALLOY_SMELTER)
        .register();

    public static final BlockEntry<ProgressMachineBlock> ALLOY_SMELTER = progressMachine("alloy_smelter", () -> MachineBlockEntities.ALLOY_SMELTER)
        .register();

    public static final BlockEntry<ProgressMachineBlock> PAINTING_MACHINE = progressMachine("painting_machine", () -> MachineBlockEntities.PAINTING_MACHINE)
        .register();

    public static final BlockEntry<MachineBlock> WIRED_CHARGER = machine("wired_charger", () -> MachineBlockEntities.WIRED_CHARGER)
        .register();

    public static final BlockEntry<MachineBlock> CREATIVE_POWER = REGISTRATE
        .block("creative_power", props -> new MachineBlock(props, MachineBlockEntities.CREATIVE_POWER))
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> STIRLING_GENERATOR = progressMachine("stirling_generator", () -> MachineBlockEntities.STIRLING_GENERATOR)
        .register();

    public static final BlockEntry<ProgressMachineBlock> SAG_MILL = progressMachine("sag_mill", () -> MachineBlockEntities.SAG_MILL)
        .lang("SAG Mill")
        .register();

    public static final BlockEntry<ProgressMachineBlock> SLICE_AND_SPLICE = progressMachine("slice_and_splice", () -> MachineBlockEntities.SLICE_AND_SPLICE)
        .lang("Slice'N'Splice")
        .register();

    public static final BlockEntry<ProgressMachineBlock> IMPULSE_HOPPER = progressMachine("impulse_hopper", () -> MachineBlockEntities.IMPULSE_HOPPER)
        .lang("Impulse Hopper")
        .register();

    public static final BlockEntry<ProgressMachineBlock> SOUL_BINDER = progressMachine("soul_binder", () -> MachineBlockEntities.SOUL_BINDER)
        .lang("Soul Binder")
        .register();

    public static final BlockEntry<ProgressMachineBlock> POWERED_SPAWNER = REGISTRATE
        .block("powered_spawner", properties -> new ProgressMachineBlock(properties, MachineBlockEntities.POWERED_SPAWNER))
        .loot((l,t) -> MachinesLootTable.copyNBTSingleCap(l, t, MachineNBTKeys.ENTITY_STORAGE))
        .properties(props -> props.strength(2.5f, 8))
        .blockstate(MachineModelUtil::progressMachineBlock)
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .item(BoundSoulBlockItem::new)
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> VACUUM_CHEST = REGISTRATE
        .block("vacuum_chest", p -> new MachineBlock(p, MachineBlockEntities.VACUUM_CHEST))
        .properties(props -> props.strength(2.5f, 8).noOcclusion())
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .loot(MachinesLootTable::copyNBT)
        .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/" + ctx.getName()))))
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> XP_VACUUM = REGISTRATE
        .block("xp_vacuum", p -> new MachineBlock(p, MachineBlockEntities.XP_VACUUM))
        .properties(props -> props.strength(2.5f, 8).noOcclusion())
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .loot(MachinesLootTable::copyNBT)
        .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/" + ctx.getName()))))
        .lang("XP Vacuum")
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<TravelAnchorBlock> TRAVEL_ANCHOR = REGISTRATE
        .block("travel_anchor", TravelAnchorBlock::new)
        .properties(props -> props.strength(2.5f, 8).noOcclusion())
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .loot(MachinesLootTable::copyNBT)
        .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/" + ctx.getName()))))
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<PaintedTravelAnchorBlock> PAINTED_TRAVEL_ANCHOR = REGISTRATE
        .block("painted_travel_anchor", PaintedTravelAnchorBlock::new)
        .properties(props -> props.strength(2.5f, 8).noOcclusion())
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .color(() -> PaintedBlockColor::new)
        .loot(DecorLootTable::withPaint)
        .blockstate((ctx, prov) -> EIOBlockState.paintedBlock(ctx, prov, Blocks.DIRT, null)) //Any cube will do
        .item()
        .color(() -> PaintedBlockColor::new)
        .build()
        .register();

    public static final Map<SolarPanelTier, BlockEntry<SolarPanelBlock>> SOLAR_PANELS = Util.make(() -> {
        Map<SolarPanelTier, BlockEntry<SolarPanelBlock>> panels = new HashMap<>();
        for (SolarPanelTier tier: SolarPanelTier.values()) {
            panels.put(tier, solarPanel(tier.name().toLowerCase(Locale.ROOT) + "_photovoltaic_module", () -> MachineBlockEntities.SOLAR_PANELS.get(tier), tier).register());
        }
        return ImmutableMap.copyOf(panels);
    });

    public static final Map<CapacitorTier, BlockEntry<CapacitorBankBlock>> CAPACITOR_BANKS = Util.make(() -> {
        Map<CapacitorTier, BlockEntry<CapacitorBankBlock>> banks = new HashMap<>();
        for (CapacitorTier tier: CapacitorTier.values()) {
            banks.put(tier, capacitorBank(tier.name().toLowerCase(Locale.ROOT) + "_capacitor_bank", () -> MachineBlockEntities.CAPACITOR_BANKS.get(tier), tier).register());
        }
        return ImmutableMap.copyOf(banks);
    });

    public static final BlockEntry<ProgressMachineBlock> CRAFTER = progressMachine("crafter", () -> MachineBlockEntities.CRAFTER)
        .register();

    public static final BlockEntry<ProgressMachineBlock> SOUL_ENGINE = REGISTRATE
        .block("soul_engine", p -> new ProgressMachineBlock(p, MachineBlockEntities.SOUL_ENGINE))
        .properties(props -> props.strength(2.5f, 8).noOcclusion())
        .loot(MachinesLootTable::copyNBT)
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockstate(MachineModelUtil::progressMachineBlock)
        .item(BoundSoulBlockItem::new)
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();
    public static final BlockEntry<ProgressMachineBlock> DRAIN = progressMachine("drain", () -> MachineBlockEntities.DRAIN)
        .register();

    public static final BlockEntry<MachineBlock> XP_OBELISK = REGISTRATE
        .block("xp_obelisk", props -> new MachineBlock(props, MachineBlockEntities.XP_OBELISK, OBELISK_SHAPE))
        .properties(props -> props.strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .loot(MachinesLootTable::copyNBT)
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/" + ctx.getName()))))
        .lang("XP Obelisk")
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> AVERSION_OBELISK = REGISTRATE
        .block("aversion_obelisk", props -> new MachineBlock(props, MachineBlockEntities.AVERSION_OBELISK, OBELISK_SHAPE))
        .properties(props -> props.strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .loot(MachinesLootTable::copyNBT)
        .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/" + ctx.getName()))))
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    //used when single methods needs to be overridden in the block class
    private static <T extends MachineBlock> BlockBuilder<T, Registrate> baseMachine(BlockBuilder<T, Registrate> machineBlock,
        NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> blockStateProvider) {
        return machineBlock
            .properties(props -> props.strength(2.5f, 8))
            .loot(MachinesLootTable::copyNBT)
            .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(blockStateProvider)
            .item()
            .tab(EIOCreativeTabs.MACHINES)
            .build();
    }

    private static BlockBuilder<MachineBlock, Registrate> machine(String name,
        Supplier<BlockEntityEntry<? extends MachineBlockEntity>> blockEntityEntry) {
        return baseMachine(REGISTRATE.block(name, props -> new MachineBlock(props, blockEntityEntry.get())), MachineModelUtil::machineBlock);
    }

    private static BlockBuilder<ProgressMachineBlock, Registrate> progressMachine(String name,
        Supplier<BlockEntityEntry<? extends MachineBlockEntity>> blockEntityEntry) {
        return baseMachine(REGISTRATE.block(name, props -> new ProgressMachineBlock(props, blockEntityEntry.get())), MachineModelUtil::progressMachineBlock);
    }

    private static BlockBuilder<SolarPanelBlock, Registrate> solarPanel(String name, Supplier<BlockEntityEntry<? extends SolarPanelBlockEntity>> blockEntityEntry, SolarPanelTier tier) {
        return REGISTRATE
            .block(name, props -> new SolarPanelBlock(props, blockEntityEntry.get(), tier))
            .properties(props -> props.strength(2.5f, 8))
            .blockstate((ctx, prov) -> MachineModelUtil.solarPanel(ctx, prov, tier))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .model((ctx, prov) -> MachineModelUtil.solarPanel(ctx, prov, tier))
            .tab(EIOCreativeTabs.MACHINES)
            .build();
    }

    private static BlockBuilder<CapacitorBankBlock, Registrate> capacitorBank(String name, Supplier<BlockEntityEntry<? extends CapacitorBankBlockEntity>> blockEntityEntry, CapacitorTier tier) {
        return REGISTRATE
            .block(name, props -> new CapacitorBankBlock(props, blockEntityEntry.get(), tier))
            .properties(props -> props.strength(2.5f, 8))
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().getExistingFile(EnderIO.loc(ctx.getName()))))
            .loot(MachinesLootTable::copyNBT)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item(CapacitorBankItem::new)
            .model((ctx, cons) -> {})
            .tab(EIOCreativeTabs.MACHINES)
            .build();
    }

    public static void register() {}
}
