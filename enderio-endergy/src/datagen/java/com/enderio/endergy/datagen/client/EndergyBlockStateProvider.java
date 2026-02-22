package com.enderio.endergy.datagen.client;

import com.enderio.endergy.common.EnderIOEndergy;
import com.enderio.endergy.common.init.EndergyBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EndergyBlockStateProvider extends BlockStateProvider {
    public EndergyBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, EnderIOEndergy.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Alloys
        simpleBlock(EndergyBlocks.CRUDE_STEEL_BLOCK.get());
        simpleBlock(EndergyBlocks.CRYSTALLINE_ALLOY_BLOCK.get());
        simpleBlock(EndergyBlocks.MELODIC_ALLOY_BLOCK.get());
        simpleBlock(EndergyBlocks.STELLAR_ALLOY_BLOCK.get());
        simpleBlock(EndergyBlocks.VIVID_ALLOY_BLOCK.get());
    }

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return this.key(block).getPath();
    }
}
