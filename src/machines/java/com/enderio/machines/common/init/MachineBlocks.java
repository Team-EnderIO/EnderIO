package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.core.data.model.EIOModel;
import com.enderio.machines.common.block.EnhancedMachineBlock;
import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.block.SimpleMachineBlock;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.item.FluidTankItem;
import com.enderio.machines.data.loot.MachinesLootTable;
import com.enderio.machines.data.model.MachineModelUtil;
import com.mojang.math.Vector3f;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder;

import java.util.function.Supplier;

public class MachineBlocks {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntry<MachineBlock> FLUID_TANK = REGISTRATE
        .block("fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.FLUID_TANK))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::copyNBT)
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), prov.models()
            .getBuilder(ctx.getName())
            .customLoader(CompositeModelBuilder::begin)
                .child("tank", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/fluid_tank_body")))
                .child("overlay", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/io_overlay")))
            .end()
        ))
        .item(FluidTankItem::new)
        .model((ctx, prov) -> {})
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> PRESSURIZED_FLUID_TANK = REGISTRATE
        .block("pressurized_fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.PRESSURIZED_FLUID_TANK))
        .properties(props -> props.strength(2.5f, 8))
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
        ))
        .item(FluidTankItem::new)
        .model((ctx, prov) -> {})
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> ENCHANTER = REGISTRATE
        .block("enchanter", props -> new MachineBlock(props, MachineBlockEntities.ENCHANTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(MachinesLootTable::copyNBT)
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), EIOModel.compositeModel(prov.models(), ctx.getName(), builder -> builder
            .component(prov.models()
                .withExistingParent(ctx.getName() + "_plinth", EnderIO.loc("block/dialing_device"))
                .texture("button", EnderIO.loc("block/dark_steel_pressure_plate")), true)
            .component(EnderIO.loc("block/enchanter_book"), new Vector3f(0, 11.25f / 16.0f, -3.5f / 16.0f), new Vector3f(-22.5f * 0.01745f, 0, 0)))))
        // TODO: 1.19: https://github.com/MinecraftForge/MinecraftForge/pull/8860
//        .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
//            .withExistingParent(ctx.getName(), prov.mcLoc("block/block"))
//            .customLoader(CompositeModelBuilder::begin)
//            .child("plinth", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/dialing_device"))
//                .texture("button", EnderIO.loc("block/dark_steel_pressure_plate")))
//            .child("book", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/enchanter_book"))
//                .rootTransform()
//                    .translation(new Vector3f(0, 11.25f / 16.0f, -3.5f / 16.0f))
//                    .rotation(Quaternion.fromXYZDegrees(new Vector3f(-22.5f, 0, 0)))
//                    .origin("center")
//                .end())
//            .end()
//        ))
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<SimpleMachineBlock> SIMPLE_POWERED_FURNACE = simpleMachine("simple_powered_furnace", () -> MachineBlockEntities.SIMPLE_POWERED_FURNACE)
        .register();

    public static final BlockEntry<SimpleMachineBlock> SIMPLE_ALLOY_SMELTER = simpleMachine("simple_alloy_smelter", () -> MachineBlockEntities.SIMPLE_ALLOY_SMELTER)
        .register();

    public static final BlockEntry<ProgressMachineBlock> ALLOY_SMELTER = standardMachine("alloy_smelter", () -> MachineBlockEntities.ALLOY_SMELTER)
        .register();

    public static final BlockEntry<EnhancedMachineBlock> ENHANCED_ALLOY_SMELTER = enhancedMachine("enhanced_alloy_smelter", () -> MachineBlockEntities.ENHANCED_ALLOY_SMELTER)
        .register();

    public static BlockEntry<MachineBlock> CREATIVE_POWER = REGISTRATE
        .block("creative_power", props -> new MachineBlock(props, MachineBlockEntities.CREATIVE_POWER))
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static BlockEntry<SimpleMachineBlock> SIMPLE_STIRLING_GENERATOR = simpleMachine("simple_stirling_generator", () -> MachineBlockEntities.SIMPLE_STIRLING_GENERATOR)
        .register();

    public static BlockEntry<ProgressMachineBlock> STIRLING_GENERATOR = standardMachine("stirling_generator", () -> MachineBlockEntities.STIRLING_GENERATOR)
        .register();

    public static BlockEntry<SimpleMachineBlock> SIMPLE_SAG_MILL = simpleMachine("simple_sag_mill", () -> MachineBlockEntities.SIMPLE_SAG_MILL)
        .lang("Simple SAG Mill")
        .register();

    public static BlockEntry<ProgressMachineBlock> SAG_MILL = standardMachine("sag_mill", () -> MachineBlockEntities.SAG_MILL)
        .lang("SAG Mill")
        .register();

    public static BlockEntry<EnhancedMachineBlock> ENHANCED_SAG_MILL = enhancedMachine("enhanced_sag_mill", () -> MachineBlockEntities.ENHANCED_SAG_MILL)
        .lang("Enhanced SAG Mill")
        .register();

    public static BlockEntry<ProgressMachineBlock> SLICE_AND_SPLICE = soulMachine("slice_and_splice", () -> MachineBlockEntities.SLICE_AND_SPLICE)
        .lang("Slice'N'Splice")
        .register();

    public static BlockEntry<ProgressMachineBlock> IMPULSE_HOPPER = standardMachine("impulse_hopper", () -> MachineBlockEntities.IMPULSE_HOPPER)
        .lang("Impulse Hopper")
        .register();

    // We use a supplier for the block entity entry so it doesnt init the block entities until its the right time.
    private static BlockBuilder<SimpleMachineBlock, Registrate> simpleMachine(String name, Supplier<BlockEntityEntry<? extends MachineBlockEntity>> blockEntityEntry) {
        return REGISTRATE
            .block(name, props -> new SimpleMachineBlock(props, blockEntityEntry.get()))
            .properties(props -> props.strength(2.5f, 8))
            .loot(MachinesLootTable::copyNBT)
            .blockstate(MachineModelUtil::simpleMachineBlock)
            .item()
            .tab(() -> EIOCreativeTabs.MACHINES)
            .build();
    }

    private static BlockBuilder<ProgressMachineBlock, Registrate> standardMachine(String name, Supplier<BlockEntityEntry<? extends MachineBlockEntity>> blockEntityEntry) {
        return REGISTRATE
            .block(name, props -> new ProgressMachineBlock(props, blockEntityEntry.get()))
            .properties(props -> props.strength(2.5f, 8))
            .loot(MachinesLootTable::copyNBT)
            .blockstate(MachineModelUtil::machineBlock)
            .item()
            .tab(() -> EIOCreativeTabs.MACHINES)
            .build();
    }

    private static BlockBuilder<ProgressMachineBlock, Registrate> soulMachine(String name, Supplier<BlockEntityEntry<? extends MachineBlockEntity>> blockEntityEntry) {
        return REGISTRATE
            .block(name, props -> new ProgressMachineBlock(props, blockEntityEntry.get()))
            .properties(props -> props.strength(2.5f, 8))
            .loot(MachinesLootTable::copyNBT)
            .blockstate(MachineModelUtil::soulMachineBlock)
            .item()
            .tab(() -> EIOCreativeTabs.MACHINES)
            .build();
    }

    private static BlockBuilder<EnhancedMachineBlock, Registrate> enhancedMachine(String name, Supplier<BlockEntityEntry<? extends MachineBlockEntity>> blockEntityEntry) {
        return REGISTRATE
            .block(name, props -> new EnhancedMachineBlock(props, blockEntityEntry.get()))
            .properties(props -> props.strength(2.5f, 8))
            .loot(MachinesLootTable::tallCopyNBT)
            .blockstate(MachineModelUtil::enhancedMachineBlock)
            .item()
            .tab(() -> EIOCreativeTabs.MACHINES)
            .model(MachineModelUtil::enhancedMachineBlockItem)
            .build();
    }

    public static void register() {}
}
