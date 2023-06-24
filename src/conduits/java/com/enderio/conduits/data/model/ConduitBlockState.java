package com.enderio.conduits.data.model;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class ConduitBlockState {
    public static void conduit(
        DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov) {
        prov.simpleBlock(ctx.get(), prov.models().getBuilder(ctx.getName())
            .customLoader(ConduitModelBuilder::begin).end()
        );
    }
}
