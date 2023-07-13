package com.enderio.conduits.data.model;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;

public class ConduitBlockState {
    public static void conduit(
        DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        prov.simpleBlock(ctx.get(), prov.models().getBuilder(ctx.getName())
            .customLoader(ConduitModelBuilder::begin).end()
        );
    }
}
