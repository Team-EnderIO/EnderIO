package com.enderio.core.client;

// 26.2-port: MultiBufferSource was removed in 26.2. TravelRendererUtil is stubbed until the
// 26.2 submit-node-based render pipeline is reimplemented.

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

public class TravelRendererUtil {

    private TravelRendererUtil() {
    }

    // 26.2-port: stubs — full implementation requires reworking on the 26.2 submit-node API
    public static Object FEATURE_BUFFER = new Object();
    public static Object FEATURE_OUTLINE_BUFFER = new Object();
    public static Object TEXT_BUFFER = new Object();
    public static SubmitNodeStorage NODE = new SubmitNodeStorage();

    public static void renderFeatures() {
        // no-op
    }

    public static void renderBackdrop(PoseStack poseStack, int packedLight, int color, Lazy<BlockModel> model) {
        // no-op
    }

    public static void renderBlockModel(PoseStack poseStack, BlockState blockState, int packedLight) {
        // no-op
    }

    // 26.2-port: BackdropTintingBufferSource stub — full implementation requires the new buffer API
    public static class BackdropTintingBufferSource {
        public void setTintColor(int tintColor) {}
        public void clearTintColor() {}
    }
}
