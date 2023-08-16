package com.enderio.machines.data.model;

import com.enderio.EnderIO;
import com.enderio.core.data.model.EIOModel;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.block.SolarPanelBlock;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder;

import java.util.Locale;

public class MachineModelUtil {

    // region Block states

    public static void machineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        // Create unpowered and powered bodies.
        String ns = ctx.getId().getNamespace();
        String path = ctx.getId().getPath();
        machineBlock(ctx, prov, wrapMachineModel(ctx, prov, new ResourceLocation(ns, "block/" + path)));
    }

    public static void progressMachineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        // Create unpowered and powered bodies.
        String ns = ctx.getId().getNamespace();
        String path = ctx.getId().getPath();
        var unpowered = new ResourceLocation(ns, "block/" + path);
        var powered = new ResourceLocation(ns, "block/" + path + "_active");

        var unpoweredModel = wrapMachineModel(ctx, prov, unpowered);
        var poweredModel = wrapMachineModel(ctx, prov, powered);
        progressMachineBlock(ctx, prov, unpoweredModel, poweredModel);
    }

    public static void solarPanel(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, SolarPanelTier tier) {
        String tierName = tier.name().toLowerCase(Locale.ROOT);
        var baseModel = prov.models().withExistingParent(ctx.getName() + "_base", EnderIO.loc("block/photovoltaic_cell_base")).texture("panel", "block/" + tierName + "_top").texture("side", "block/" + tierName + "_side");
        var sideModel = prov.models().withExistingParent(ctx.getName() + "_side", EnderIO.loc("block/photovoltaic_cell_side")).texture("side", "block/" + tierName + "_side");
        var cornerModel = prov.models().withExistingParent(ctx.getName() + "_corner", EnderIO.loc("block/photovoltaic_cell_corner")).texture("side", "block/" + tierName + "_side");
        var builder = prov.getMultipartBuilder(ctx.get());
        builder.part().modelFile(baseModel).addModel();
        builder.part().modelFile(sideModel).addModel().condition(SolarPanelBlock.NORTH, true);
        builder.part().modelFile(sideModel).rotationY(90).addModel().condition(SolarPanelBlock.EAST, true);
        builder.part().modelFile(sideModel).rotationY(180).addModel().condition(SolarPanelBlock.SOUTH, true);
        builder.part().modelFile(sideModel).rotationY(270).addModel().condition(SolarPanelBlock.WEST, true);
        builder.part().modelFile(cornerModel).addModel().condition(SolarPanelBlock.NORTH_EAST, true);
        builder.part().modelFile(cornerModel).rotationY(90).addModel().condition(SolarPanelBlock.SOUTH_EAST, true);
        builder.part().modelFile(cornerModel).rotationY(180).addModel().condition(SolarPanelBlock.SOUTH_WEST, true);
        builder.part().modelFile(cornerModel).rotationY(270).addModel().condition(SolarPanelBlock.NORTH_WEST, true);
    }

    public static void solarPanel(DataGenContext<Item, ? extends Item> ctx, RegistrateItemModelProvider prov, SolarPanelTier tier) {
        String tierName = tier.name().toLowerCase(Locale.ROOT);
        prov.withExistingParent(ctx.getName(), EnderIO.loc("item/photovoltaic_cell")).texture("side", "block/" + tierName + "_side").texture("panel", "block/" + tierName + "_top");
    }

    private static void machineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, ModelFile model) {
        prov
            .getVariantBuilder(ctx.get())
            .forAllStates(state -> ConfiguredModel
                .builder()
                .modelFile(model)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());
    }

    private static void progressMachineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, ModelFile unpowered,
        ModelFile powered) {
        prov
            .getVariantBuilder(ctx.get())
            .forAllStates(state -> ConfiguredModel
                .builder()
                .modelFile(state.getValue(ProgressMachineBlock.POWERED) ? powered : unpowered)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());
    }

    private static ModelFile wrapMachineModel(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, ResourceLocation model) {
        return prov.models().withExistingParent(model.getPath() + "_combined", prov.mcLoc("block/block"))
            .texture("particle", new ResourceLocation(model.getNamespace(),"block/" + ctx.getName() + "_front"))
            .customLoader(CompositeModelBuilder::begin)
            .child("machine", EIOModel.getExistingParent(prov.models(), model))
            .child("overlay", EIOModel.getExistingParent(prov.models(), EnderIO.loc("block/io_overlay")))
            .end();
    }

    // endregion
}
