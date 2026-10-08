package com.enderio.enderio.client.foundation.renderer;

// 26.2-port: MultiBufferSource was removed in 26.2; OutlineBuffer is stubbed
// import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jspecify.annotations.NonNull;

public class OutlineBuffer /* 26.2-port: implements MultiBufferSource */ {

    public static final OutlineBuffer INSTANCE = new OutlineBuffer();

    private OutlineBuffer() {}

    @NonNull
    // 26.2-port: getBuffer method stubbed — MultiBufferSource is removed in 26.2
    public VertexConsumer getBuffer(@NonNull RenderType type) {
        throw new UnsupportedOperationException("OutlineBuffer is disabled in the 26.2 port — see ClientIntegration/IOConfigSceneRenderer");
    }
}
