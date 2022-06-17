package com.enderio.decoration.datagen.model.block;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorBlockState {
    /**
     * {@see ModelProvider.MODEL}
     */
    public static void paintedBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, Block toCopy) {
        Block paintedBlock = ctx.get();
        ResourceLocation paintedBlockId = ForgeRegistries.BLOCKS.getKey(paintedBlock);
        PaintedModelBuilder paintedModel = new PaintedModelBuilder(
            new ResourceLocation(paintedBlockId.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + paintedBlockId.getPath()),
            prov.models().existingFileHelper, toCopy);
        prov.models().getBuilder(paintedBlockId.getPath());
        prov.models().generatedModels.put(paintedModel.getLocation(), paintedModel);
        prov.simpleBlock(paintedBlock, paintedModel);
    }
}
