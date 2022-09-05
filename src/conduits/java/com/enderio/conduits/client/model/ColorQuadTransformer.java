package com.enderio.conduits.client.model;

import com.enderio.api.misc.ColorControl;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.IQuadTransformer;
import org.jetbrains.annotations.Nullable;

public record ColorQuadTransformer(@Nullable ColorControl insert, @Nullable ColorControl extract) implements IQuadTransformer {
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
