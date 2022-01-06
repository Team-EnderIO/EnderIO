package com.enderio.machines.common.block;

import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.base.data.model.EIOModel;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.blockentity.MachineBlockEntities;
import com.enderio.machines.common.data.LootTableUtils;
import com.enderio.base.data.model.CompositeModelBuilder;
import com.mojang.math.Vector3f;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Function;

public class MachineBlocks {
    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final BlockEntry<MachineBlock> FLUID_TANK = REGISTRATE
        .block("fluid_tank", props -> new MachineBlock(props, MachineBlockEntities.FLUID_TANK))
        .properties(props -> props.strength(2.5f, 8))
        .loot(LootTableUtils::copyNBT)
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), EIOModel.compositeModel(prov.models(), ctx.getName(),
            builder -> builder.component(EIOMachines.loc("block/machine_frame"), true).component(EIOMachines.loc("block/placeholder_machine_front")))))
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<MachineBlock> ENCHANTER = REGISTRATE
        .block("enchanter", props -> new MachineBlock(props, MachineBlockEntities.ENCHANTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(LootTableUtils::copyNBT)
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> SIMPLE_POWERED_FURNACE = REGISTRATE
        .block("simple_powered_furnace", props -> new ProgressMachineBlock(props, MachineBlockEntities.SIMPLE_POWERED_FURNACE))
        .properties(props -> props.strength(2.5f, 8))
        .loot(LootTableUtils::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate((ctx, prov) -> {
            // Get the model.
            Function<Boolean, ModelFile> getModel = on -> EIOModel.compositeModel(prov.models(), ctx.getName() + (on ? "_on" : ""), builder -> builder
                .component(EIOMachines.loc("block/simple_machine_frame"), true)
                .component(EIOMachines.loc("block/simple_powered_furnace_front" + (on ? "_on" : "")))
                .component(EIOMachines.loc("block/io_overlay")));

            // Generate the "on" front
            prov
                .models()
                .withExistingParent("simple_powered_furnace_front_on", EIOMachines.loc("block/simple_powered_furnace_front"))
                .texture("front", EIOMachines.loc("block/simple_powered_furnace_front_on"));

            prov
                .getVariantBuilder(ctx.get())
                .forAllStates(state -> ConfiguredModel
                    .builder()
                    .modelFile(getModel.apply(state.getValue(ProgressMachineBlock.POWERED)))
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build());
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> SIMPLE_ALLOY_SMELTER = REGISTRATE
        .block("simple_alloy_smelter", props -> new ProgressMachineBlock(props, MachineBlockEntities.SIMPLE_ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(LootTableUtils::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate((ctx, prov) -> {
            // Get the model.
            Function<Boolean, ModelFile> getModel = on -> EIOModel.compositeModel(prov.models(), ctx.getName() + (on ? "_on" : ""), builder -> builder
                .component(EIOMachines.loc("block/simple_machine_frame"), true)
                .component(EIOMachines.loc("block/simple_alloy_smelter_front" + (on ? "_on" : "")))
                .component(EIOMachines.loc("block/io_overlay")));

            // Generate the "on" front
            prov
                .models()
                .withExistingParent("simple_alloy_smelter_front_on", EIOMachines.loc("block/simple_alloy_smelter_front"))
                .texture("front", EIOMachines.loc("block/simple_alloy_smelter_front_on"));

            prov
                .getVariantBuilder(ctx.get())
                .forAllStates(state -> ConfiguredModel
                    .builder()
                    .modelFile(getModel.apply(state.getValue(ProgressMachineBlock.POWERED)))
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build());
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<ProgressMachineBlock> ALLOY_SMELTER = REGISTRATE
        .block("alloy_smelter", props -> new ProgressMachineBlock(props, MachineBlockEntities.ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(LootTableUtils::copyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate((ctx, prov) -> {
            // Get the model.
            Function<Boolean, ModelFile> getModel = on -> EIOModel.compositeModel(prov.models(), ctx.getName() + (on ? "_on" : ""), builder -> builder
                .component(EIOMachines.loc("block/machine_frame"), true)
                .component(EIOMachines.loc("block/alloy_smelter_front" + (on ? "_on" : "")))
                .component(EIOMachines.loc("block/io_overlay")));

            // Generate the "on" front
            prov
                .models()
                .withExistingParent("alloy_smelter_front_on", EIOMachines.loc("block/alloy_smelter_front"))
                .texture("front", EIOMachines.loc("block/alloy_smelter_front_on"));

            prov
                .getVariantBuilder(ctx.get())
                .forAllStates(state -> ConfiguredModel
                    .builder()
                    .modelFile(getModel.apply(state.getValue(ProgressMachineBlock.POWERED)))
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build());
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .build()
        .register();

    public static final BlockEntry<EnhancedMachineBlock> ENHANCED_ALLOY_SMELTER = REGISTRATE
        .block("enhanced_alloy_smelter", props -> new EnhancedMachineBlock(props, MachineBlockEntities.ENHANCED_ALLOY_SMELTER))
        .properties(props -> props.strength(2.5f, 8))
        .loot(LootTableUtils::tallCopyNBT)
        .addLayer(() -> RenderType::cutout)
        .blockstate((ctx, prov) -> {
            // TODO: Common model utils class
            // Get bottom model.
            Function<Boolean, ModelFile> getBottomModel = on -> EIOModel.compositeModel(prov.models(), ctx.getName() + (on ? "_on" : ""), builder -> builder
                .component(EIOMachines.loc("block/enhanced_machine_frame"), true)
                .component(EIOMachines.loc("block/enhanced_alloy_smelter_front" + (on ? "_on" : "")))
                .component(EIOMachines.loc("block/io_overlay")));

            // Get top model.
            Function<Boolean, ModelFile> getTopModel = on -> {
                if (on)
                    return prov
                        .models()
                        .withExistingParent("enhanced_alloy_smelter_top_on", EIOMachines.loc("block/enhanced_alloy_smelter_top"))
                        .texture("front", EIOMachines.loc("block/enhanced_alloy_smelter_front_on"));
                return prov.models().getExistingFile(EIOMachines.loc("block/enhanced_alloy_smelter_top"));
            };

            // Generate the "on" variants
            prov
                .models()
                .withExistingParent("enhanced_alloy_smelter_front_on", EIOMachines.loc("block/enhanced_alloy_smelter_front"))
                .texture("front", EIOMachines.loc("block/enhanced_alloy_smelter_front_on"));

            prov.getVariantBuilder(ctx.get()).forAllStates(state -> {
                boolean on = state.getValue(ProgressMachineBlock.POWERED);
                DoubleBlockHalf half = state.getValue(EnhancedMachineBlock.HALF);
                return ConfiguredModel
                    .builder()
                    .modelFile(half == DoubleBlockHalf.LOWER ? getBottomModel.apply(on) : getTopModel.apply(on))
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build();
            });
        })
        .item()
        .tab(() -> EIOCreativeTabs.MACHINES)
        .model((ctx, prov) -> EIOModel.compositeModel(prov, ctx.getName(), builder -> builder
            .component(EIOMachines.loc("block/enhanced_machine_frame"))
            .component(EIOMachines.loc("block/enhanced_alloy_smelter_front"))
            .component(EIOMachines.loc("block/enhanced_alloy_smelter_top"), new Vector3f(0.0f, 1.0f, 0.0f))))
        .build()
        .register();

    public static void register() {}
}
