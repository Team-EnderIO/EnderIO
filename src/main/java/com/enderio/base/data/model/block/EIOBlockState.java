package com.enderio.base.data.model.block;

import com.enderio.EnderIO;
import com.enderio.base.common.block.light.PoweredLight;
import com.enderio.regilite.data.DataGenContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

public class EIOBlockState {

    public static void paneBlock(BlockStateProvider prov, DataGenContext<Block, ? extends IronBarsBlock> ctx) {
        prov.paneBlock(ctx.get(),
            prov.models()
                .panePost(ctx.getName().concat("_post"), prov.blockTexture(ctx.get()), prov.blockTexture(ctx.get()))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneSide(ctx.getName().concat("_side"), prov.blockTexture(ctx.get()), prov.blockTexture(ctx.get()))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneSideAlt(ctx.getName().concat("_side_alt"), prov.blockTexture(ctx.get()), prov.blockTexture(ctx.get()))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneNoSide(ctx.getName().concat("_no_side"), prov.blockTexture(ctx.get()))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneNoSideAlt(ctx.getName().concat("_no_side_alt"), prov.blockTexture(ctx.get()))
                .renderType(prov.mcLoc("cutout_mipped")));
    }

    public static <T extends Block> void paintedBlock(String name, BlockStateProvider prov, T ctx, Block toCopy, @Nullable Direction itemTextureRotation) {
        prov.simpleBlock(ctx, prov.models().getBuilder(name)
            .customLoader(PaintedBlockModelBuilder::begin)
            .reference(toCopy)
            .itemTextureRotation(itemTextureRotation)
            .end()
        );
    }

    public static <T extends Block> void lightBlock(BlockStateProvider prov, DataGenContext<Block, T> ctx) {
        prov.getVariantBuilder(ctx.get()).forAllStates(state -> {
            Direction facing = state.getValue(PoweredLight.FACING);
            AttachFace face = state.getValue(PoweredLight.FACE);
            boolean powered = state.getValue(PoweredLight.ENABLED);

            ModelFile light = prov.models().withExistingParent(ctx.getName(), EnderIO.loc("block/lightblock"));
            ModelFile light_powered = prov.models().withExistingParent(ctx.getName() + "_powered", EnderIO.loc("block/lightblock"));
            return ConfiguredModel.builder()
                .modelFile(powered ? light : light_powered)
                .rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
                .rotationY((int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
                .uvLock(face == AttachFace.WALL)
                .build();
        });
    }
}
