package com.enderio.conduits.client.model.facades;

import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ConduitFacadeRendering {

    private static final ThreadLocal<RandomSource> RANDOM = ThreadLocal
            .withInitial(() -> new SingleThreadedRandomSource(42L));

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        // Render during translucent stage when facades are transparent
        // Otherwise render during solid stage
        boolean isTransparent = !ClientFacadeVisibility.areFacadesVisible();
        
        if (isTransparent && event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        
        if (!isTransparent && event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            return;
        }

        var camera = event.getCamera();
        var frustum = event.getFrustum();
        
        // Get facades near the camera
        BlockPos cameraPos = camera.getBlockPosition();
        SectionPos cameraSection = SectionPos.of(cameraPos);
        
        // Check nearby chunk sections for facades
        Map<BlockPos, BlockState> facadesToRender = new Object2ObjectOpenHashMap<>();
        
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    long sectionKey = SectionPos.asLong(
                        cameraSection.x() + x,
                        cameraSection.y() + y,
                        cameraSection.z() + z
                    );
                    
                    LongSet blockList = ConduitBlockEntity.CHUNK_FACADES.get(sectionKey);
                    if (blockList != null) {
                        for (long entry : blockList) {
                            BlockPos pos = BlockPos.of(entry);
                            BlockState state = ConduitBlockEntity.FACADES.get(entry);
                            if (state != null && frustum.isVisible(
                                new AABB(pos))) {
                                facadesToRender.put(pos, state);
                            }
                        }
                    }
                }
            }
        }

        if (facadesToRender.isEmpty()) {
            return;
        }

        renderFacades(event.getPoseStack(), event.getPartialTick(), facadesToRender, isTransparent);
    }

    private static void renderFacades(PoseStack poseStack, float partialTick, Map<BlockPos, BlockState> facades, boolean isTransparent) {
        var minecraft = Minecraft.getInstance();
        var level = minecraft.level;
        if (level == null) {
            return;
        }

        var camera = minecraft.gameRenderer.getMainCamera();
        var cameraPos = camera.getPosition();

        RandomSource random = RANDOM.get();
        var blockRenderer = minecraft.getBlockRenderer();
        var renderBuffers = minecraft.renderBuffers();
        var bufferSource = renderBuffers.bufferSource();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (Map.Entry<BlockPos, BlockState> entry : facades.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

            random.setSeed(42L);

            var model = blockRenderer.getBlockModel(state);

            for (RenderType renderType : model.getRenderTypes(state, random, ModelData.EMPTY)) {
                VertexConsumer consumer;
                
                if (isTransparent) {
                    // Use translucent render type with alpha modification
                    consumer = new AlphaModifyingVertexConsumer(
                        bufferSource.getBuffer(RenderType.translucent()),
                        85 // 33% opacity (255 * 0.33 ≈ 85)
                    );
                } else {
                    consumer = bufferSource.getBuffer(renderType);
                }

                blockRenderer.getModelRenderer().tesselateBlock(
                    level,
                    model,
                    state,
                    pos,
                    poseStack,
                    consumer,
                    true,
                    random,
                    state.getSeed(pos),
                    OverlayTexture.NO_OVERLAY,
                    ModelData.EMPTY,
                    renderType
                );
            }

            poseStack.popPose();
        }

        poseStack.popPose();
        bufferSource.endBatch();
    }

    /**
     * Vertex consumer wrapper that modifies alpha values for transparent facade rendering.
     */
    private static class AlphaModifyingVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final int alpha;

        public AlphaModifyingVertexConsumer(VertexConsumer delegate, int alpha) {
            this.delegate = delegate;
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            return delegate.vertex(x, y, z);
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            return delegate.color(r, g, b, alpha);
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            return delegate.uv(u, v);
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return delegate.overlayCoords(u, v);
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            return delegate.uv2(u, v);
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return delegate.normal(x, y, z);
        }

        @Override
        public void endVertex() {
            delegate.endVertex();
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
            delegate.defaultColor(r, g, b, alpha);
        }

        @Override
        public void unsetDefaultColor() {
            delegate.unsetDefaultColor();
        }
    }
}
