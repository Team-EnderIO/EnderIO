package com.enderio.enderio.client.foundation.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jspecify.annotations.NonNull;

public class OutlineBuffer implements MultiBufferSource {

    public static final OutlineBuffer INSTANCE = new OutlineBuffer();

    private OutlineBuffer() {}

    @NonNull
    @Override
    public VertexConsumer getBuffer(@NonNull RenderType type) {
        return Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(OutlineRenderType.CUTOUT_NO_DEPTH);
    }
}

