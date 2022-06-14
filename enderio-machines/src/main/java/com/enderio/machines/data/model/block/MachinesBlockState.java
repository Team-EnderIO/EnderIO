package com.enderio.machines.data.model.block;

import com.enderio.base.data.model.EIOModel;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.block.EnhancedMachineBlock;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

public class MachinesBlockState {

    public static ModelFile machineBody(RegistrateBlockstateProvider prov, String name, ModelFile frontModel) {
        return machineBody(prov, name, frontModel.getUncheckedLocation());
    }

    public static ModelFile machineBody(RegistrateBlockstateProvider prov, String name, ResourceLocation frontModel) {
        return EIOModel.compositeModel(prov.models(), name, builder -> builder
            .component(EIOMachines.loc("block/machine_frame"), true)
            .component(EIOMachines.loc("block/io_overlay"))
            .component(frontModel));
    }

    public static ModelFile simpleMachineBody(RegistrateBlockstateProvider prov, String name, ModelFile frontModel) {
        return simpleMachineBody(prov, name, frontModel.getUncheckedLocation());
    }

    public static ModelFile simpleMachineBody(RegistrateBlockstateProvider prov, String name, ResourceLocation frontModel) {
        return EIOModel.compositeModel(prov.models(), name, builder -> builder
            .component(EIOMachines.loc("block/simple_machine_frame"), true)
            .component(EIOMachines.loc("block/io_overlay"))
            .component(frontModel));
    }

    public static void machineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        String ns = ctx.getId().getNamespace();
        String path = ctx.getId().getPath();
        ModelFile unpowered = machineBody(prov, ctx.getName(), new ResourceLocation(ns, "block/" + path + "_front"));
        ModelFile powered = machineBody(prov, ctx.getName() + "_on", prov
            .models()
            .withExistingParent(ctx.getName() + "_front_on", new ResourceLocation(ns, "block/" + path + "_front"))
            .texture("front", EIOMachines.loc("block/" + ctx.getName() + "_front_on")));

        prov
            .getVariantBuilder(ctx.get())
            .forAllStates(state -> ConfiguredModel
                .builder()
                .modelFile(state.getValue(ProgressMachineBlock.POWERED) ? powered : unpowered)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());
    }

    public static void simpleMachineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        String ns = ctx.getId().getNamespace();
        String path = ctx.getId().getPath();
        ModelFile unpowered = simpleMachineBody(prov, ctx.getName(), new ResourceLocation(ns, "block/" + path + "_front"));
        ModelFile powered = simpleMachineBody(prov, ctx.getName() + "_on", prov
            .models()
            .withExistingParent(ctx.getName() + "_front_on", new ResourceLocation(ns, "block/" + path + "_front"))
            .texture("front", EIOMachines.loc("block/" + ctx.getName() + "_front_on")));

        prov
            .getVariantBuilder(ctx.get())
            .forAllStates(state -> ConfiguredModel
                .builder()
                .modelFile(state.getValue(ProgressMachineBlock.POWERED) ? powered : unpowered)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());
    }

    public static void machineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, ModelFile unpowered, ModelFile powered) {
        prov
            .getVariantBuilder(ctx.get())
            .forAllStates(state -> ConfiguredModel
                .builder()
                .modelFile(state.getValue(ProgressMachineBlock.POWERED) ? powered : unpowered)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());
    }

    public static void enhancedMachineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, ModelFile bottomUnpowered, ModelFile bottomPowered, ModelFile topUnpowered, ModelFile topPowered) {
        prov
            .getVariantBuilder(ctx.get())
            .forAllStates(state -> {
                boolean on = state.getValue(ProgressMachineBlock.POWERED);
                boolean bottom = state.getValue(EnhancedMachineBlock.HALF) == DoubleBlockHalf.LOWER;
                return ConfiguredModel
                    .builder()
                    .modelFile(bottom ? (on ? bottomPowered : bottomUnpowered) : (on ? topPowered : topUnpowered))
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build();
            });
    }
}
