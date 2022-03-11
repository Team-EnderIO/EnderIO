package com.enderio.machines.common.init;

import com.enderio.base.EnderIO;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.base.datagen.model.EIOModel;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.block.EnhancedMachineBlock;
import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.block.SimpleMachineBlock;
import com.enderio.machines.common.item.FluidTankItem;
import com.enderio.machines.datagen.loot.MachinesLootTable;
import com.enderio.machines.datagen.model.block.MachinesBlockState;
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
        .blockstate((ctx, prov) -> {
            MachinesBlockState.machineBlock(ctx, prov,
                EIOModel.compositeModel(prov.models(), ctx.getName(), builder -> builder
                    .component(EIOMachines.loc("block/simple_machine_frame"))
                    .component(EIOMachines.loc("block/io_overlay"))
                    .component(EIOMachines.loc("block/simple_powered_furnace_front"))),
                EIOModel.compositeModel(prov.models(), ctx.getName() + "_on", builder -> builder
                    .component(EIOMachines.loc("block/simple_machine_frame"))
                    .component(EIOMachines.loc("block/io_overlay"))
                    .component(prov
                        .models()
                        .withExistingParent("simple_powered_furnace_front_on", EIOMachines.loc("block/simple_powered_furnace_front"))
                        .texture("front", EIOMachines.loc("block/simple_powered_furnace_front")))));
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> SIMPLE_ALLOY_SMELTER = REGISTRATE
        .block("simple_alloy_smelter", props -> new ProgressMachineBlock(props, MachineBlockEntities.SIMPLE_ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate((ctx, prov) -> {
            MachinesBlockState.machineBlock(ctx, prov,
                EIOModel.compositeModel(prov.models(), ctx.getName(), builder -> builder
                    .component(EIOMachines.loc("block/simple_machine_frame"), true)
                    .component(EIOMachines.loc("block/simple_alloy_smelter_front"))
                    .component(EIOMachines.loc("block/io_overlay"))),
                EIOModel.compositeModel(prov.models(), ctx.getName() + "_on", builder -> builder
                    .component(EIOMachines.loc("block/simple_machine_frame"), true)
                    .component(EIOMachines.loc("block/io_overlay"))
                    .component(prov
                        .models()
                        .withExistingParent("simple_alloy_smelter_front_on", EIOMachines.loc("block/simple_alloy_smelter_front"))
                        .texture("front", EIOMachines.loc("block/simple_alloy_smelter_front_on")))));
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> ALLOY_SMELTER = REGISTRATE
        .block("alloy_smelter", props -> new ProgressMachineBlock(props, MachineBlockEntities.ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate((ctx, prov) -> {
            MachinesBlockState.machineBlock(ctx, prov,
                EIOModel.compositeModel(prov.models(), ctx.getName(), builder -> builder
                    .component(EIOMachines.loc("block/machine_frame"), true)
                    .component(EIOMachines.loc("block/io_overlay"))
                    .component(EIOMachines.loc("block/alloy_smelter_front"))),
                EIOModel.compositeModel(prov.models(), ctx.getName() + "_on", builder -> builder
                    .component(EIOMachines.loc("block/machine_frame"), true)
                    .component(EIOMachines.loc("block/io_overlay"))
                    .component(prov
                        .models()
                        .withExistingParent("alloy_smelter_front_on", EIOMachines.loc("block/alloy_smelter_front"))
                        .texture("front", EIOMachines.loc("block/alloy_smelter_front_on")))));
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<EnhancedMachineBlock> ENHANCED_ALLOY_SMELTER = REGISTRATE
        .block("enhanced_alloy_smelter", props -> new EnhancedMachineBlock(props, MachineBlockEntities.ENHANCED_ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::tallCopyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate((ctx, prov) -> {
            MachinesBlockState.enhancedMachineBlock(ctx, prov,
                // bottom unpowered
                EIOModel.compositeModel(prov.models(), ctx.getName(), builder -> builder
                    .component(EIOMachines.loc("block/enhanced_machine_frame"), true)
                    .component(EIOMachines.loc("block/enhanced_alloy_smelter_front"))
                    .component(EIOMachines.loc("block/io_overlay"))),
                // bottom powered
                EIOModel.compositeModel(prov.models(), ctx.getName() + "_on", builder -> builder
                    .component(EIOMachines.loc("block/enhanced_machine_frame"), true)
                    .component(prov
                        .models()
                        .withExistingParent("enhanced_alloy_smelter_front_on", EIOMachines.loc("block/enhanced_alloy_smelter_front"))
                        .texture("front", EIOMachines.loc("block/enhanced_alloy_smelter_front_on")))
                    .component(EIOMachines.loc("block/io_overlay"))),
                // top unpowered
                prov.models().getExistingFile(EIOMachines.loc("block/enhanced_alloy_smelter_top")),
                // top powered
                prov
                    .models()
                    .withExistingParent("enhanced_alloy_smelter_top_on", EIOMachines.loc("block/enhanced_alloy_smelter_top"))
                    .texture("front", EIOMachines.loc("block/enhanced_alloy_smelter_front_on")));
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .model((ctx, prov) -> EIOModel.compositeModel(prov, ctx.getName(), builder -> builder
            .component(EIOMachines.loc("block/enhanced_machine_frame"))
            .component(EIOMachines.loc("block/enhanced_alloy_smelter_front"))
            .component(EIOMachines.loc("block/enhanced_alloy_smelter_top"), new Vector3f(0.0f, 1.0f, 0.0f))))
        .build()
        .register();
    
    
    public static final BlockEntry<MachineBlock> VACUUM_CHEST = REGISTRATE
            .block("vacuum_chest", p -> new MachineBlock(p, MachineBlockEntities.VACUUM_CHEST))
            .properties(props -> props.strength(2.5f, 8))
            .loot(MachinesLootTable::copyNBT)
            .item()
            .tab(() -> EIOCreativeTabs.MACHINES)
            .build()
            .register();

    public static void classload() {}
}
