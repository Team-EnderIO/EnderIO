package com.enderio.enderio.datagen.client.models.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import org.jetbrains.annotations.Nullable;

public class EIOBlockState {

    public static <T extends Block> void paintedBlock(String name, BlockStateProvider prov, T ctx, Block toCopy, @Nullable Direction itemTextureRotation) {
        prov.simpleBlock(ctx, prov.models().getBuilder(name)
            .customLoader(PaintedBlockModelBuilder::begin)
            .reference(toCopy)
            .itemTextureRotation(itemTextureRotation)
            .end()
        );
    }
}
