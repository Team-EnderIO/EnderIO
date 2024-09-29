package com.enderio.conduits.data.model;

import com.enderio.regilite.data.DataGenContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

public class ConduitBlockState {
    public static void conduit(BlockStateProvider prov, DataGenContext<Block, ? extends Block> ctx) {
        prov.simpleBlock(ctx.get(), prov.models().getBuilder(ctx.getName())
            .customLoader(ConduitModelBuilder::begin).end()
        );
    }
}
