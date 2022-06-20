package com.enderio.machines.common.init;

import com.enderio.base.EnderIO;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.base.data.model.EIOModel;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.block.EnhancedMachineBlock;
import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.block.SimpleMachineBlock;
import com.enderio.machines.common.item.FluidTankItem;
import com.enderio.machines.data.loot.MachinesLootTable;
import com.enderio.machines.data.model.block.MachineModelUtil;
import com.mojang.math.Vector3f;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;

public class MachineBlocks {
    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final BlockEntry<MachineBlock> FLUID_TANK = REGISTRATE
        .block("fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.FLUID_TANK))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), EIOModel.compositeModel(prov.models(), ctx.getName(),
            builder -> builder
                .component(EIOMachines.loc("block/fluid_tank_body"), true)
                .component(EIOMachines.loc("block/io_overlay")))))
        .item(FluidTankItem::new)
        .model((ctx, prov) -> EIOModel.dummyModel(prov, ctx.getName()))
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> PRESSURIZED_FLUID_TANK = REGISTRATE
        .block("pressurized_fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.PRESSURIZED_FLUID_TANK))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), EIOModel.compositeModel(prov.models(), ctx.getName(),
            builder -> builder
                .component(prov.models()
                    .withExistingParent(ctx.getName() + "_body", EIOMachines.loc("block/fluid_tank_body"))
                    .texture("tank", EIOMachines.loc("block/pressurized_fluid_tank"))
                    .texture("bottom", EIOMachines.loc("block/enhanced_machine_bottom"))
                    .texture("top", EIOMachines.loc("block/enhanced_machine_top")), true)
                .component(EIOMachines.loc("block/io_overlay")))))
        .item(FluidTankItem::new)
        .model((ctx, prov) -> EIOModel.dummyModel(prov, ctx.getName()))
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> ENCHANTER = REGISTRATE
        .block("enchanter", props -> new MachineBlock(props, MachineBlockEntities.ENCHANTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::copyNBT)
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), EIOModel.compositeModel(prov.models(), ctx.getName(), builder -> builder
            .component(prov.models()
                .withExistingParent(ctx.getName() + "_plinth", EIOMachines.loc("block/dialing_device"))
                .texture("button", EnderIO.loc("block/dark_steel_pressure_plate")), true)
                .component(EIOMachines.loc("block/enchanter_book"), new Vector3f(0, 11.25f / 16.0f, -3.5f / 16.0f), new Vector3f(-22.5f * 0.01745f, 0, 0)))))
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<SimpleMachineBlock> SIMPLE_POWERED_FURNACE = REGISTRATE
        .block("simple_powered_furnace", props -> new SimpleMachineBlock(props, MachineBlockEntities.SIMPLE_POWERED_FURNACE))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate(MachineModelUtil::simpleMachineBlock)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> SIMPLE_ALLOY_SMELTER = REGISTRATE
        .block("simple_alloy_smelter", props -> new ProgressMachineBlock(props, MachineBlockEntities.SIMPLE_ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate(MachineModelUtil::simpleMachineBlock)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> ALLOY_SMELTER = REGISTRATE
        .block("alloy_smelter", props -> new ProgressMachineBlock(props, MachineBlockEntities.ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate(MachineModelUtil::machineBlock)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<EnhancedMachineBlock> ENHANCED_ALLOY_SMELTER = REGISTRATE
        .block("enhanced_alloy_smelter", props -> new EnhancedMachineBlock(props, MachineBlockEntities.ENHANCED_ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::tallCopyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate(MachineModelUtil::enhancedMachineBlock)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .model(MachineModelUtil::enhancedMachineBlockItem)
        .build()
        .register();

    public static BlockEntry<MachineBlock> CREATIVE_POWER = REGISTRATE
        .block("creative_power", props -> new MachineBlock(props, MachineBlockEntities.CREATIVE_POWER))
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static BlockEntry<SimpleMachineBlock> SIMPLE_STIRLING_GENERATOR = REGISTRATE
        .block("simple_stirling_generator", props -> new SimpleMachineBlock(props, MachineBlockEntities.SIMPLE_STIRLING_GENERATOR))
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate(MachineModelUtil::simpleMachineBlock)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static BlockEntry<ProgressMachineBlock> STIRLING_GENERATOR = REGISTRATE
        .block("stirling_generator", props -> new ProgressMachineBlock(props, MachineBlockEntities.STIRLING_GENERATOR))
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate(MachineModelUtil::machineBlock)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static BlockEntry<SimpleMachineBlock> SIMPLE_SAG_MILL = REGISTRATE
        .block("simple_sag_mill", props -> new SimpleMachineBlock(props, MachineBlockEntities.SIMPLE_SAG_MILL))
        .lang("Simple SAG Mill")
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate(MachineModelUtil::simpleMachineBlock)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static BlockEntry<ProgressMachineBlock> SAG_MILL = REGISTRATE
        .block("sag_mill", props -> new ProgressMachineBlock(props, MachineBlockEntities.SAG_MILL))
        .lang("SAG Mill")
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate(MachineModelUtil::machineBlock)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static BlockEntry<EnhancedMachineBlock> ENHANCED_SAG_MILL = REGISTRATE
        .block("enhanced_sag_mill", props -> new EnhancedMachineBlock(props, MachineBlockEntities.ENHANCED_SAG_MILL))
        .lang("Enhanced SAG Mill")
        .loot(MachinesLootTable::tallCopyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate(MachineModelUtil::enhancedMachineBlock)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .model(MachineModelUtil::enhancedMachineBlockItem)
        .build()
        .register();

    public static BlockEntry<ProgressMachineBlock> SLICE_AND_SPLICE = REGISTRATE
        .block("slice_and_splice", props -> new ProgressMachineBlock(props, MachineBlockEntities.SLICE_AND_SPLICE))
        .lang("Slice'N'Splice")
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate(MachineModelUtil::soulMachineBlock)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static void classload() {}
}
