package com.enderio.enderio.compat.jei.category;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

public class FireCraftingRenderer extends PictureInPictureRenderer<FireCraftingRenderer.State> {

    private final FeatureRenderDispatcher dispatcher;

    protected FireCraftingRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);

        this.dispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
    }

    @Override
    public Class<State> getRenderStateClass() {
        return State.class;
    }

    @Override
    protected void renderToTexture(State state, PoseStack poseStack) {
        var minecraft = Minecraft.getInstance();
        poseStack.pushPose();
        minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        var block = Blocks.BEDROCK;
        var blockRenderState = new BlockModelRenderState();
        minecraft.getBlockModelResolver().update(blockRenderState, block.defaultBlockState(), BlockDisplayContext.create());
        blockRenderState.submit(poseStack, dispatcher.getSubmitNodeStorage(), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

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

    public record State(
        int x0,
        int x1,
        int y0,
        int y1,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
    ) implements PictureInPictureRenderState {

        public static PictureInPictureRenderState create(int x0, int x1, int y0, int y1, @Nullable ScreenRectangle scissorArea) {
            var bounds = PictureInPictureRenderState.getBounds(x0, x1, y0, y1, scissorArea);
            return new State(x0, x1, y0, y1, scissorArea, bounds);
        }

        @Override
        public float scale() {
            return 1;
        }
    }

}
