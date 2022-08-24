package com.enderio.conduits.client;

import com.enderio.core.common.blockentity.ColorControl;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.IQuadTransformer;
import org.jetbrains.annotations.Nullable;

public record ColorQuadTransformer(@Nullable ColorControl in, @Nullable ColorControl out) implements IQuadTransformer {
    @Override
    public void processInPlace(BakedQuad quad) {
        if (quad.isTinted()) {
            if (quad.getTintIndex() == 0 && out != null) {
                quad.tintIndex = out.ordinal();
            } else if (quad.getTintIndex() == 1 && in != null) {
                quad.tintIndex = in.ordinal();
            }
        }
    }
}
