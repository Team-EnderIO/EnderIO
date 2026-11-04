package com.enderio.enderio.compat.jei.category;

import com.enderio.enderio.init.EIOFluids;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;
import org.jspecify.annotations.Nullable;

public class FireCraftingRenderer extends PictureInPictureRenderer<FireCraftingRenderer.State> {

    public FireCraftingRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public Class<State> getRenderStateClass() {
        return State.class;
    }

    @Override
    protected void renderToTexture(State state, PoseStack poseStack) {
        var minecraft = Minecraft.getInstance();
        var dispatcher = minecraft.gameRenderer.getFeatureRenderDispatcher();
        var modelResolver = minecraft.getBlockModelResolver();
        poseStack.pushPose();
        minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.LEVEL);

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.translate(-20F, -30F, 0);
        poseStack.scale(20F, 20F, 20F);

        var blockRenderState = new BlockModelRenderState();
        modelResolver.update(blockRenderState, state.blockState, BlockDisplayContext.create());
        blockRenderState.submit(poseStack, dispatcher.getSubmitNodeStorage(), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

        // render the fire/fluid block one block above
        poseStack.translate(0F, 1F, 0);
        if (!state.showAltFire) {
            var fireState = Blocks.FIRE.defaultBlockState();
            modelResolver.update(blockRenderState, fireState, BlockDisplayContext.create());
            blockRenderState.submit(poseStack, dispatcher.getSubmitNodeStorage(), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        } else {
            var fluidState = EIOFluids.FIRE_WATER.source().get().defaultFluidState();
            var fluidRenderer = new FluidRenderer(minecraft.getModelManager().getFluidStateModelSet());
            var renderState = new MovingBlockRenderState();
            renderState.blockState = fluidState.createLegacyBlock();

            fluidRenderer.tesselate(renderState, BlockPos.ZERO, layer -> {
                var buffer = bufferSource.getBuffer(layer.translucent() ? Sheets.translucentBlockSheet() : Sheets.cutoutBlockSheet());
                return new LiquidVertexConsumer(buffer, poseStack.last());
            }, fluidState.createLegacyBlock(), fluidState);
        }

        dispatcher.renderAllFeatures();
        poseStack.popPose();
    }

    @Override
    protected String getTextureLabel() {
        return "EnderIO JEI Fire Crafting";
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2.0F;
    }

    public record State(int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds, BlockState blockState,
                        boolean showAltFire) implements PictureInPictureRenderState {

        public static PictureInPictureRenderState create(ScreenRectangle rect, @Nullable ScreenRectangle scissorArea, BlockState blockState,
            boolean showAltFire) {
            return new State(rect.left(), rect.top(), rect.right(), rect.bottom(), scissorArea, rect, blockState, showAltFire);
        }

        @Override
        public float scale() {
            return 1;
        }
    }

    // custom wrapp
    private static class LiquidVertexConsumer extends VertexConsumerWrapper {
        private final PoseStack.Pose pose;

        public LiquidVertexConsumer(VertexConsumer delegate, PoseStack.Pose pose) {
            super(delegate);
            this.pose = pose;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            return parent.addVertex(pose, x, y, z);
        }
    }

}
