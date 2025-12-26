package com.enderio.enderio.client.foundation.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.jetbrains.annotations.Nullable;

public record ColorQuadTransformer(@Nullable DyeColor insert, @Nullable DyeColor extract) implements IQuadTransformer {
    @Override
    public void processInPlace(BakedQuad quad) {
        if (quad.isTinted()) {
            if (quad.tintIndex() == 0 && extract != null) {
                //quad.tintIndex = extract.ordinal();
                quad = new BakedQuad(quad.vertices(), extract.ordinal(), quad.direction(), quad.sprite(), quad.shade(), quad.lightEmission(), quad.hasAmbientOcclusion());
            } else if (quad.tintIndex() == 1 && insert != null) {
                //quad.tintIndex = insert.ordinal();
                quad = new BakedQuad(quad.vertices(), insert.ordinal(), quad.direction(), quad.sprite(), quad.shade(), quad.lightEmission(), quad.hasAmbientOcclusion());
            }
        }
    }
}
