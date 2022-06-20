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

    // region Block states

    public static void machineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        machineBlock(ctx, prov, MachinesBlockState::machineBody);
    }

    public static void soulMachineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        machineBlock(ctx, prov, MachinesBlockState::soulMachineBody);
    }

    public static void simpleMachineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        machineBlock(ctx, prov, MachinesBlockState::simpleMachineBody);
    }

    private static void machineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, MachineBodyBuilder bodyBuilder) {
        // Create unpowered and powered bodies.
        String ns = ctx.getId().getNamespace();
        String path = ctx.getId().getPath();
        ModelFile unpowered = bodyBuilder.build(prov, ctx.getName(), prov.models().getExistingFile(new ResourceLocation(ns, "block/" + path + "_front")));
        ModelFile powered = bodyBuilder.build(prov, ctx.getName() + "_on", prov
            .models()
            .withExistingParent(ctx.getName() + "_front_on", new ResourceLocation(ns, "block/" + path + "_front"))
            .texture("front", EIOMachines.loc("block/" + ctx.getName() + "_front_on")));
        machineBlock(ctx, prov, unpowered, powered);
    }

    private static void machineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, ModelFile unpowered, ModelFile powered) {
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

    // endregion

    // region Models

    @FunctionalInterface
    private interface MachineBodyBuilder {
        ModelFile build(RegistrateBlockstateProvider prov, String name, ModelFile frontModel);
    }

    private static ModelFile machineBody(RegistrateBlockstateProvider prov, String name, ModelFile frontModel) {
        return machineBodyModel(prov, name, frontModel, EIOMachines.loc("block/machine_frame"));
    }

    private static ModelFile soulMachineBody(RegistrateBlockstateProvider prov, String name, ModelFile frontModel) {
        return machineBodyModel(prov, name, frontModel, EIOMachines.loc("block/soul_machine_frame"));
    }

    private static ModelFile simpleMachineBody(RegistrateBlockstateProvider prov, String name, ModelFile frontModel) {
        return machineBodyModel(prov, name, frontModel, EIOMachines.loc("block/simple_machine_frame"));
    }

    private static ModelFile machineBodyModel(RegistrateBlockstateProvider prov, String name, ModelFile frontModel, ResourceLocation frameModel) {
        return EIOModel.compositeModel(prov.models(), name, builder -> builder
            .component(frameModel, true)
            .component(EIOMachines.loc("block/io_overlay"))
            .component(frontModel));
    }

    // endregion
}
