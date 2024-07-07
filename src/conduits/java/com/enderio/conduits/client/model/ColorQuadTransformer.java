package com.enderio.conduits.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.jetbrains.annotations.Nullable;

public record ColorQuadTransformer(@Nullable DyeColor insert, @Nullable DyeColor extract) implements IQuadTransformer {
    @Override
    public void processInPlace(BakedQuad quad) {
        if (quad.isTinted()) {
            if (quad.getTintIndex() == 0 && extract != null) {
                quad.tintIndex = extract.ordinal();
            } else if (quad.getTintIndex() == 1 && insert != null) {
                quad.tintIndex = insert.ordinal();
            }
        }
    }
}
