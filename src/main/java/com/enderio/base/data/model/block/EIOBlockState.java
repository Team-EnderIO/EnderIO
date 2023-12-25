package com.enderio.base.data.model.block;

import com.enderio.EnderIO;
import com.enderio.base.common.block.light.PoweredLight;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

public class EIOBlockState {

    public static <T extends IronBarsBlock> void paneBlock(BlockStateProvider prov, T block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        prov.paneBlock(block,
            prov.models()
                .panePost(name.concat("_post"), prov.blockTexture(block), prov.blockTexture(block))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneSide(name.concat("_side"), prov.blockTexture(block), prov.blockTexture(block))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneSideAlt(name.concat("_side_alt"), prov.blockTexture(block), prov.blockTexture(block))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneNoSide(name.concat("_no_side"), prov.blockTexture(block))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneNoSideAlt(name.concat("_no_side_alt"), prov.blockTexture(block))
                .renderType(prov.mcLoc("cutout_mipped")));
    }

    public static void paintedBlock(Block block, BlockStateProvider prov, Block toCopy, @Nullable Direction itemTextureRotation) {
        prov.simpleBlock(block, prov.models().getBuilder(BuiltInRegistries.BLOCK.getKey(block).getPath())
            .customLoader(PaintedBlockModelBuilder::begin)
            .reference(toCopy)
            .itemTextureRotation(itemTextureRotation)
            .end()
        );
    }

    public static void lightBlock(BlockStateProvider prov, Block block) {
        prov.getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(PoweredLight.FACING);
            AttachFace face = state.getValue(PoweredLight.FACE);
            boolean powered = state.getValue(PoweredLight.ENABLED);

            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            ModelFile light = prov.models().withExistingParent(id.getPath(), EnderIO.loc("block/lightblock"));
            ModelFile light_powered = prov.models().withExistingParent(id.getPath() + "_powered", EnderIO.loc("block/lightblock"));
            return ConfiguredModel.builder()
                .modelFile(powered ? light : light_powered)
                .rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
                .rotationY((int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
                .uvLock(face == AttachFace.WALL)
                .build();
        });
    }
}
