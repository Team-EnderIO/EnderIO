package com.enderio.enderio.datagen.client.models;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.data.model.block.ConduitModelBuilder;
import com.enderio.enderio.init.ConduitBlocks;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EIOBlockStateProvider extends BlockStateProvider {
    public EIOBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, EnderIO.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(EIOBlocks.COPPER_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.DARK_STEEL_BLOCK.get());
        simpleBlock(EIOBlocks.SOULARIUM_BLOCK.get());
        simpleBlock(EIOBlocks.END_STEEL_BLOCK.get());

        // Conduit
        simpleBlock(ConduitBlocks.CONDUIT_BUNDLE.get(), models().getBuilder(ConduitBlocks.CONDUIT_BUNDLE.getId().toString()).customLoader(ConduitModelBuilder::begin).end());
    }
}
