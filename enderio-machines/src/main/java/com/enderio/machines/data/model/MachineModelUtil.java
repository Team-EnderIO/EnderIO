package com.enderio.machines.data.model;

import com.enderio.EnderIOBase;
import com.enderio.core.data.model.ModelHelper;
import com.enderio.machines.common.block.LegacyProgressMachineBlock;
import com.enderio.machines.common.block.SolarPanelBlock;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import com.enderio.regilite.data.DataGenContext;
import com.enderio.regilite.data.RegiliteItemModelProvider;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;

public class MachineModelUtil {

    // region Block states

    public static void machineBlock(BlockStateProvider prov, DataGenContext<Block, ? extends Block> ctx) {
        // Create unpowered and powered bodies.
        String ns = ctx.getId().getNamespace();
        String path = ctx.getId().getPath();
        machineBlock(prov, ctx,
                wrapMachineModel(prov, ctx, ResourceLocation.fromNamespaceAndPath(ns, "block/" + path)));
    }

    public static void progressMachineBlock(BlockStateProvider prov, DataGenContext<Block, ? extends Block> ctx) {
        // Create unpowered and powered bodies.
        String ns = ctx.getId().getNamespace();
        String path = ctx.getId().getPath();
        var unpowered = ResourceLocation.fromNamespaceAndPath(ns, "block/" + path);
        var powered = ResourceLocation.fromNamespaceAndPath(ns, "block/" + path + "_active");

        var unpoweredModel = wrapMachineModel(prov, ctx, unpowered);
        var poweredModel = wrapMachineModel(prov, ctx, powered);
        progressMachineBlock(prov, ctx, unpoweredModel, poweredModel);
    }

    public static void solarPanel(BlockStateProvider prov, DataGenContext<Block, ? extends Block> ctx,
            SolarPanelTier tier) {
        String tierName = tier.name().toLowerCase(Locale.ROOT);
        var baseModel = prov.models()
                .withExistingParent(ctx.getName() + "_base", EnderIOBase.loc("block/photovoltaic_cell_base"))
                .texture("panel", "block/" + tierName + "_top")
                .texture("side", "block/" + tierName + "_side");
        var sideModel = prov.models()
                .withExistingParent(ctx.getName() + "_side", EnderIOBase.loc("block/photovoltaic_cell_side"))
                .texture("side", "block/" + tierName + "_side");
        var cornerModel = prov.models()
                .withExistingParent(ctx.getName() + "_corner", EnderIOBase.loc("block/photovoltaic_cell_corner"))
                .texture("side", "block/" + tierName + "_side");
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

    public static void solarPanel(RegiliteItemModelProvider prov, DataGenContext<Item, ? extends Item> ctx,
            SolarPanelTier tier) {
        String tierName = tier.name().toLowerCase(Locale.ROOT);
        prov.withExistingParent(ctx.getName(), EnderIOBase.loc("item/photovoltaic_cell"))
                .texture("side", "block/" + tierName + "_side")
                .texture("panel", "block/" + tierName + "_top");
    }

    private static void machineBlock(BlockStateProvider prov, DataGenContext<Block, ? extends Block> ctx,
            ModelFile model) {
        prov.getVariantBuilder(ctx.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(model)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                        .build());
    }

    private static void progressMachineBlock(BlockStateProvider prov, DataGenContext<Block, ? extends Block> ctx,
            ModelFile unpowered, ModelFile powered) {
        prov.getVariantBuilder(ctx.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(LegacyProgressMachineBlock.POWERED) ? powered : unpowered)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                        .build());
    }

    private static ModelFile wrapMachineModel(BlockStateProvider prov, DataGenContext<Block, ? extends Block> ctx,
            ResourceLocation model) {
        return prov.models()
                .withExistingParent(model.getPath() + "_combined", prov.mcLoc("block/block"))
                .texture("particle",
                        ctx.getName().equals("enchanter") ? EnderIOBase.loc("block/dark_steel_pressure_plate")
                                : ResourceLocation.fromNamespaceAndPath(model.getNamespace(),
                                        "block/" + ctx.getName() + "_front"))
                .customLoader(CompositeModelBuilder::begin)
                .child("machine", ModelHelper.getExistingAsBuilder(prov.models(), model))
                .child("overlay", ModelHelper.getExistingAsBuilder(prov.models(), EnderIOBase.loc("block/io_overlay")))
                .end();
    }

    // endregion
}
