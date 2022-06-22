package com.enderio.decoration.datagen.model.block;

import com.enderio.decoration.EIODecor;
import com.enderio.decoration.common.block.light.PoweredLight;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;

public class DecorBlockState {
    /**
     * {@see ModelProvider.MODEL}
     */
    public static void paintedBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, Block toCopy) {
        Block paintedBlock = ctx.get();
        PaintedModelBuilder paintedModel = new PaintedModelBuilder(
            new ResourceLocation(paintedBlock.getRegistryName().getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + paintedBlock.getRegistryName().getPath()),
            prov.models().existingFileHelper, toCopy);
        prov.models().getBuilder(paintedBlock.getRegistryName().getPath());
        prov.models().generatedModels.put(paintedModel.getLocation(), paintedModel);
        prov.simpleBlock(paintedBlock, paintedModel);
    }
    
    public static void lightBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        prov.getVariantBuilder(ctx.get()).forAllStates(state -> {
            Direction facing = state.getValue(PoweredLight.FACING);
            AttachFace face = state.getValue(PoweredLight.FACE);
            boolean powered = state.getValue(PoweredLight.ENABLED);
            
            ModelFile light = prov.models().withExistingParent(ctx.get().getRegistryName().getPath(), new ResourceLocation(EIODecor.MODID, "block/lightblock"));
            ModelFile light_powered = prov.models().withExistingParent(ctx.get().getRegistryName().getPath() + "_powered", new ResourceLocation(EIODecor.MODID, "block/lightblock"));
            return ConfiguredModel.builder()
                    .modelFile(powered ? light : light_powered)
                    .rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
                    .rotationY((int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
                    .uvLock(face == AttachFace.WALL)
                    .build();
        });
    }
}
