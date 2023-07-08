package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.core.data.model.EIOModel;
import com.enderio.machines.common.block.CapacitorBankBlock;
import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.block.SolarPanelBlock;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorTier;
import com.enderio.machines.common.blockentity.solar.SolarPanelBlockEntity;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.machines.common.item.CapacitorBankItem;
import com.enderio.machines.common.item.FluidTankItem;
import com.enderio.machines.common.item.PoweredSpawnerItem;
import com.enderio.machines.data.loot.MachinesLootTable;
import com.enderio.machines.data.model.MachineModelUtil;
import com.google.common.collect.ImmutableMap;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.Util;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder;
import net.minecraftforge.common.util.TransformationHelper;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class MachineBlocks {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntry<MachineBlock> FLUID_TANK = REGISTRATE
        .block("fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.FLUID_TANK))
        .properties(props -> props.strength(2.5f, 8).isViewBlocking((pState, pLevel, pPos) -> false).noOcclusion())
        .loot(MachinesLootTable::copyNBT)
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), prov.models()
            .getBuilder(ctx.getName())
            .customLoader(CompositeModelBuilder::begin)
                .child("tank", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/fluid_tank_body")))
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
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), prov.models()
                .withExistingParent(ctx.getName(), prov.mcLoc("block/block"))
                .customLoader(CompositeModelBuilder::begin)
                    .child("tank", EIOModel
                        .getExistingParent(prov.models(), EnderIO.loc("block/fluid_tank_body"))
                            .texture("tank", EnderIO.loc("block/pressurized_fluid_tank"))
                            .texture("bottom", EnderIO.loc("block/enhanced_machine_bottom"))
                            .texture("top", EnderIO.loc("block/enhanced_machine_top")))
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
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), prov.models()
            .withExistingParent(ctx.getName(), prov.mcLoc("block/block"))
            .texture("particle", EnderIO.loc("block/dark_steel_pressure_plate"))
            .customLoader(CompositeModelBuilder::begin)
            .child("plinth", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/dialing_device"))
                .texture("button", EnderIO.loc("block/dark_steel_pressure_plate")))
            .child("book", (BlockModelBuilder) EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/enchanter_book"))
                .rootTransforms()
                    .translation(new Vector3f(0, 11.25f / 16.0f, -3.5f / 16.0f))
                    .rotation(-22.5f, 0, 0, true)
                    .origin(TransformationHelper.TransformOrigin.CENTER)
                .end())
            .end()
        ))
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> PRIMITIVE_ALLOY_SMELTER = standardMachine("primitive_alloy_smelter", () -> MachineBlockEntities.PRIMITIVE_ALLOY_SMELTER)
        .blockstate((ctx, prov) -> {
            ModelFile model = prov.models().withExistingParent(ctx.getName(), prov.mcLoc("furnace")).texture("front", EnderIO.loc("block/primitive_alloy_smelter_front"));
            prov
                .getVariantBuilder(ctx.get())
                .forAllStates(state -> ConfiguredModel
                    .builder()
                    .modelFile(model)
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build());
        })
        .register();

    public static final BlockEntry<ProgressMachineBlock> ALLOY_SMELTER = standardMachine("alloy_smelter", () -> MachineBlockEntities.ALLOY_SMELTER)
        .register();

    public static final BlockEntry<ProgressMachineBlock> PAINTING_MACHINE = standardMachine("painting_machine", () -> MachineBlockEntities.PAINTING_MACHINE)
        .register();

    public static final BlockEntry<MachineBlock> CREATIVE_POWER = REGISTRATE
        .block("creative_power", props -> new MachineBlock(props, MachineBlockEntities.CREATIVE_POWER))
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> WIRED_CHARGER = REGISTRATE
        .block("wired_charger", props -> new MachineBlock(props, MachineBlockEntities.WIRED_CHARGER))
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> STIRLING_GENERATOR = standardMachine("stirling_generator", () -> MachineBlockEntities.STIRLING_GENERATOR)
        .register();

    public static final BlockEntry<ProgressMachineBlock> SAG_MILL = standardMachine("sag_mill", () -> MachineBlockEntities.SAG_MILL)
        .lang("SAG Mill")
        .register();

    public static final BlockEntry<ProgressMachineBlock> SLICE_AND_SPLICE = soulMachine("slice_and_splice", () -> MachineBlockEntities.SLICE_AND_SPLICE)
        .lang("Slice'N'Splice")
        .register();

    public static final BlockEntry<ProgressMachineBlock> IMPULSE_HOPPER = standardMachine("impulse_hopper", () -> MachineBlockEntities.IMPULSE_HOPPER)
        .lang("Impulse Hopper")
        .register();

    public static final BlockEntry<ProgressMachineBlock> SOUL_BINDER = soulMachine("soul_binder", () -> MachineBlockEntities.SOUL_BINDER)
        .lang("Soul Binder")
        .register();

    public static final BlockEntry<ProgressMachineBlock> POWERED_SPAWNER = REGISTRATE
        .block("powered_spawner", props -> new ProgressMachineBlock(props, MachineBlockEntities.POWERED_SPAWNER))
        .loot((l,t) -> MachinesLootTable.copyNBTSingleCap(l, t, "EntityStorage"))
        .blockstate(MachineModelUtil::soulMachineBlock)
        .item(PoweredSpawnerItem::new)
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> VACUUM_CHEST = REGISTRATE
        .block("vacuum_chest", p -> new MachineBlock(p, MachineBlockEntities.VACUUM_CHEST))
        .properties(props -> props.strength(2.5f, 8).noOcclusion())
        .loot(MachinesLootTable::copyNBT)
        .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/vacuum_chest"))))
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> XP_VACUUM = REGISTRATE
        .block("xp_vacuum", p -> new MachineBlock(p, MachineBlockEntities.XP_VACUUM))
        .properties(props -> props.strength(2.5f, 8).noOcclusion())
        .loot(MachinesLootTable::copyNBT)
        .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(EnderIO.loc("block/xp_vacuum"))))
        .item()
        .tab(EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final Map<SolarPanelTier, BlockEntry<SolarPanelBlock>> SOLAR_PANELS = Util.make(() -> {
        Map<SolarPanelTier, BlockEntry<SolarPanelBlock>> panels = new HashMap<>();
        for (SolarPanelTier tier: SolarPanelTier.values()) {
            panels.put(tier, solarPanel(tier.name().toLowerCase(Locale.ROOT) + "_photovoltaic_cell", () -> MachineBlockEntities.SOLAR_PANELS.get(tier), tier).register());
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
    public static final BlockEntry<ProgressMachineBlock> CRAFTER = standardMachine("crafter", () -> MachineBlockEntities.CRAFTER)
        .lang("Crafter")
        .blockstate((ctx, prov) -> MachineModelUtil.customMachineBlock(ctx, prov, "crafter"))
        .register();

    //used when single methods needs to be overridden in the block class
    private static BlockBuilder<ProgressMachineBlock, Registrate> standardMachine(BlockBuilder<ProgressMachineBlock, Registrate> machineBlock) {
        return machineBlock
            .properties(props -> props.strength(2.5f, 8))
            .loot(MachinesLootTable::copyNBT)
            .tag(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(MachineModelUtil::machineBlock)
            .item()
            .tab(EIOCreativeTabs.MACHINES)
            .build();
    }

    private static BlockBuilder<ProgressMachineBlock, Registrate> standardMachine(String name,
        Supplier<BlockEntityEntry<? extends MachineBlockEntity>> blockEntityEntry) {
        return standardMachine(REGISTRATE.block(name, props -> new ProgressMachineBlock(props, blockEntityEntry.get())));
    }


    private static BlockBuilder<ProgressMachineBlock, Registrate> soulMachine(String name, Supplier<BlockEntityEntry<? extends MachineBlockEntity>> blockEntityEntry) {
        return REGISTRATE
            .block(name, props -> new ProgressMachineBlock(props, blockEntityEntry.get()))
            .properties(props -> props.strength(2.5f, 8))
            .loot(MachinesLootTable::copyNBT)
            .blockstate(MachineModelUtil::soulMachineBlock)
            .item()
            .tab(EIOCreativeTabs.MACHINES)
            .build();
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
