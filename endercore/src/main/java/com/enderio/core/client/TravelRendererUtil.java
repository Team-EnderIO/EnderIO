package com.enderio.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

// TODO: Why is this in EnderCore...
public class TravelRendererUtil {

    // Layers of a single travel target, drawn in this order.
    public static final int ORDER_BACKDROP = 0;
    public static final int ORDER_MODEL = 1;
    public static final int ORDER_TEXT = 2;
    public static final int ORDERS_PER_TARGET = 3;

    private TravelRendererUtil() {

    }

    public static void renderBackdrop(PoseStack poseStack, AlwaysOnTopSubmitNodeCollector collector, int packedLight, int color, Lazy<BlockModel> model) {
        BlockModelRenderState renderState = new BlockModelRenderState();
        model.get().update(renderState, Blocks.AIR.defaultBlockState(), BlockDisplayContext.create(), 42);

        renderState.submit(poseStack, collector.withBackdropTint(color), packedLight, OverlayTexture.NO_OVERLAY, 0);
    }

    public static void renderBlockModel(PoseStack poseStack, AlwaysOnTopSubmitNodeCollector collector, BlockState blockState, int packedLight) {
        BlockModelRenderState modelRenderState = new BlockModelRenderState();
        Minecraft.getInstance().getBlockModelResolver().update(modelRenderState, blockState, BlockDisplayContext.create());

        modelRenderState.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, 0);
    }

}
