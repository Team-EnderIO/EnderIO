package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.EIOPipelineModifiers;
import com.enderio.enderio.client.foundation.model.ModelRenderUtil;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;
import net.neoforged.neoforge.client.pipeline.PipelineModifier;
import net.neoforged.neoforge.client.pipeline.RegisterPipelineModifiersEvent;
import org.joml.Vector3f;

import java.util.SequencedMap;

@EventBusSubscriber
public class IOConfigSceneRenderer extends PictureInPictureRenderer<IOConfigSceneRenderState> {
    private static final Identifier SELECTED_ICON = EnderIO.id("block/overlay/selected_face");

    private final FeatureRenderDispatcher solidFeatureRenderDispatcher;
    private final SubmitNodeCollector solidCollector;

    private final GhostBufferSource ghostBufferSource;
    private final FeatureRenderDispatcher ghostFeatureRenderDispatcher;
    private final SubmitNodeCollector ghostCollector;

    public IOConfigSceneRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);

        var minecraft = Minecraft.getInstance();

        this.solidFeatureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        this.solidCollector = solidFeatureRenderDispatcher.getSubmitNodeStorage();

        ghostBufferSource = new GhostBufferSource(bufferSource);

        ghostFeatureRenderDispatcher = new FeatureRenderDispatcher(
            new SubmitNodeStorage(),
            minecraft.getModelManager(),
            ghostBufferSource,
            minecraft.getAtlasManager(),
            minecraft.renderBuffers().outlineBufferSource(),
            minecraft.renderBuffers().crumblingBufferSource(),
            minecraft.font,
            minecraft.gameRenderer.getGameRenderState()
        );

        ghostCollector = ghostFeatureRenderDispatcher.getSubmitNodeStorage();
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2f;
    }

    @Override
    public Class<IOConfigSceneRenderState> getRenderStateClass() {
        return IOConfigSceneRenderState.class;
    }

    @Override
    protected void renderToTexture(IOConfigSceneRenderState state, PoseStack poseStack) {
        poseStack.pushPose();

        poseStack.mulPose(state.viewMatrix());

        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);

        // Render neighbors transparently, if enabled
        if (state.shouldRenderNeighbors()) {
            RenderSystem.pushPipelineModifier(EIOPipelineModifiers.FORCE_TRANSLUCENT);

            for (var block : state.neighborBlocks()) {
                renderBlock(poseStack, ghostCollector, block);
            }

            ghostFeatureRenderDispatcher.renderAllFeatures();
            ghostBufferSource.endBatch();

            RenderSystem.popPipelineModifier();

            // Flush the depth texture now, all primary blocks should render over the top if required.
            // Note; we could consider seeing if we can sort in distance from the camera to do 'true' transparency?
            var device = RenderSystem.getDevice();
            device.createCommandEncoder().clearDepthTexture(this.depthTexture, 1f);
        }

        // Now render the primary blocks
        for (var block : state.primaryBlocks()) {
            renderBlock(poseStack, solidCollector, block);
        }

        solidFeatureRenderDispatcher.renderAllFeatures();
        bufferSource.endBatch();

        if (state.selection() != null) {
            renderSelection(poseStack, state.selection().getFirst(), state.selection().getSecond());
        }

        poseStack.popPose();
    }

    private void renderBlock(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, IOConfigSceneBlock block) {
        poseStack.pushPose();
        poseStack.translate(new Vec3(block.pos()));

        block.blockModelRenderState().submit(poseStack, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    private void renderSelection(PoseStack poseStack, BlockPos pos, Direction side) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(SELECTED_ICON);

        VertexConsumer builder = bufferSource.getBuffer(RenderTypes.blockScreenEffect(sprite.atlasLocation()));

        poseStack.pushPose();

        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

        Vector3f[] vec = ModelRenderUtil.createQuadVerts(side, 0, 1, 1);

        builder.addVertex(poseStack.last(), vec[0].x(), vec[0].y(), vec[0].z())
            .setColor(1F, 1F, 1F, 1F)
            .setUv(sprite.getU0(), sprite.getV0());
        builder.addVertex(poseStack.last(), vec[1].x(), vec[1].y(), vec[1].z())
            .setColor(1F, 1F, 1F, 1F)
            .setUv(sprite.getU0(), sprite.getV1());
        builder.addVertex(poseStack.last(), vec[2].x(), vec[2].y(), vec[2].z())
            .setColor(1F, 1F, 1F, 1F)
            .setUv(sprite.getU1(), sprite.getV1());
        builder.addVertex(poseStack.last(), vec[3].x(), vec[3].y(), vec[3].z())
            .setColor(1F, 1F, 1F, 1F)
            .setUv(sprite.getU1(), sprite.getV0());

        bufferSource.endBatch();

        poseStack.popPose();
    }

    @Override
    protected String getTextureLabel() {
        return "EnderIO IO Config Overlay";
    }

    // Transparency hack
    private static class GhostBufferSource extends MultiBufferSource.BufferSource {

        public GhostBufferSource(MultiBufferSource.BufferSource bufferSource) {
            super(bufferSource.sharedBuffer, bufferSource.fixedBuffers);
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            VertexConsumer consumer = super.getBuffer(renderType);
            return new AlphaWrapper(consumer);
        }

        private static class AlphaWrapper extends VertexConsumerWrapper {
            public AlphaWrapper(VertexConsumer vertexConsumer) {
                super(vertexConsumer);
            }

            @Override
            public VertexConsumer setColor(int r, int g, int b, int a) {
                super.setColor(r, g, b, MachinesConfig.CLIENT.IO_CONFIG_NEIGHBOUR_TRANSPARENCY.get().floatValue());
                return this;
            }

            @Override
            public VertexConsumer setColor(int packedColor) {
                super.setColor(ARGB.color(MachinesConfig.CLIENT.IO_CONFIG_NEIGHBOUR_TRANSPARENCY.get().floatValue(), packedColor));
                return this;
            }
        }
    }
}
