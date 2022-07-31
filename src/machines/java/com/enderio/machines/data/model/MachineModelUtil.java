package com.enderio.machines.data.model;

import com.enderio.EnderIO;
import com.enderio.core.data.model.EIOModel;
import com.enderio.machines.common.block.EnhancedMachineBlock;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.mojang.math.Vector3f;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder;

public class MachineModelUtil {

    // region Block states

    public static void machineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        machineBlock(ctx, prov, MachineModelUtil::machineBody);
    }

    public static void soulMachineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        machineBlock(ctx, prov, MachineModelUtil::soulMachineBody);
    }

    private static void machineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, MachineBodyBuilder bodyBuilder) {
        // Create unpowered and powered bodies.
        String ns = ctx.getId().getNamespace();
        String path = ctx.getId().getPath();
        ModelFile unpowered = bodyBuilder.build(prov, ctx.getName(), EIOModel.getExistingParent(prov.models(), new ResourceLocation(ns, "block/" + path + "_front")));
        ModelFile powered = bodyBuilder.build(prov, ctx.getName() + "_on", prov
            .models()
            .withExistingParent(ctx.getName() + "_front_on", new ResourceLocation(ns, "block/" + path + "_front"))
            .texture("front", EnderIO.loc("block/" + ctx.getName() + "_front_on")));
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

    // endregion

    // region Models

    @FunctionalInterface
    private interface MachineBodyBuilder {
        ModelFile build(RegistrateBlockstateProvider prov, String name, BlockModelBuilder frontModel);
    }

    private static ModelFile machineBody(RegistrateBlockstateProvider prov, String name, BlockModelBuilder frontModel) {
        return machineBodyModel(prov, name, frontModel, EnderIO.loc("block/machine_frame"));
    }

    private static ModelFile soulMachineBody(RegistrateBlockstateProvider prov, String name, BlockModelBuilder frontModel) {
        return machineBodyModel(prov, name, frontModel, EnderIO.loc("block/soul_machine_frame"));
    }

    private static ModelFile machineBodyModel(RegistrateBlockstateProvider prov, String name, BlockModelBuilder frontModel, ResourceLocation frameModel) {
        return prov.models().withExistingParent(name, prov.mcLoc("block/block"))
            .customLoader(CompositeModelBuilder::begin)
                .child("frame", EIOModel.getExistingParent(prov.models(), frameModel))
                .child("overlay", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/io_overlay")))
                .child("front", frontModel)
            .end();
    }

    // endregion
}
