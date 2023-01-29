package com.enderio.machines.client.gui.widget;

import com.enderio.core.client.gui.screen.IEnderScreen;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class IOConfigRenderer<S extends Screen & IEnderScreen> {

    private final S addedOn;
    private final Rect2i bounds;

    // Camera Variables
    private boolean dragging = false;
    private float pitch = 0;
    private float yaw = 0;
    private float distance;

    private final @NotNull Vector3f origin;
    private Vector3f size;
    private final @NotNull Matrix4f rotMat = new Matrix4f();
    private final List<BlockPos> configurables = new ArrayList<>();
    private final List<BlockPos> neighbours = new ArrayList<>();

    public IOConfigRenderer(S addedOn, Rect2i bounds, MachineBlockEntity configurable) {
        this(addedOn, bounds, List.of(configurable.getBlockPos()));
    }

    public IOConfigRenderer(S addedOn, Rect2i bounds, List<BlockPos> _configurables) {
        this.addedOn = addedOn;
        this.bounds = bounds;
        this.configurables.addAll(_configurables);

        Vector3f c;
        if (configurables.size() == 1) {
            BlockPos bc = configurables.get(0);
            c = new Vector3f(bc.getX() + 0.5f, bc.getY() + 0.5f, bc.getZ() + 0.5f);
            size = new Vector3f(1, 1, 1);
        } else {
            Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
            Vector3f max = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
            for (BlockPos bc : configurables) {
                min.set(Math.min(bc.getX(), min.x()), Math.min(bc.getY(), min.y()), Math.min(bc.getZ(), min.z()));
                max.set(Math.max(bc.getX(), max.x()), Math.max(bc.getY(), max.y()), Math.max(bc.getZ(), max.z()));
            }
            size = max;
            size.sub(min);
            size.mul(0.5f);
            c = new Vector3f(min.x() + size.x(), min.y() + size.y(), min.z() + size.z());
            size.mul(2);
        }

        origin = new Vector3f(c.x(), c.y(), c.z());
        rotMat.setIdentity();

        distance = Math.max(Math.max(size.x(), size.y()), size.z()) + 4;

        configurables.forEach(pos -> {
            for (Direction dir : Direction.values()) {
                BlockPos loc = pos.offset(dir.getNormal());
                if (!configurables.contains(loc)) {
                    neighbours.add(loc);
                }
            }

        });
        pitch = 30;
        yaw = 30;
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick, Rect2i vp) {
        GuiComponent.fill(pPoseStack, bounds.getX(), bounds.getY(), bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), 0xFF000000);

        renderScene(pPartialTick, pPoseStack, vp);
    }

    public void handleMouseClick(double pMouseX, double pMouseY) {
        //        EnderIO.LOGGER.info("mouse click! " + pMouseX + " " + pMouseY);
    }

    public void handleMouseDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        var screen = Minecraft.getInstance().screen;
        double dx = pDragX / (double) screen.width;
        double dy = pDragY / (double) screen.height;
        yaw -= 4 * dx * 180;
        pitch += 2 * dy * 180;
        pitch = Math.min(80, Math.max(-80, pitch)); //clamp
    }

    private void renderScene(float partialTick, PoseStack ps, Rect2i vp) {
        float sizeX = size.x() + 2;
        float sizeY = size.y() + 2;
        float sizeZ = size.z() + 2;
        int xPos = bounds.getX() + (bounds.getWidth() / 2);
        int yPos = bounds.getY() + (bounds.getHeight() / 2);
        float diag = (float) Math.sqrt(sizeX * sizeX + sizeZ * sizeZ); //change later
        float scaleX = bounds.getWidth() / diag;
        float scaleY = (float) bounds.getHeight() / sizeY;
        float scale = -Math.min(scaleX, scaleY);

        ps.pushPose();
        ps.translate(xPos, yPos, 100);
        ps.scale(scale, scale, scale);
        //        ps.translate(-sizeX / 2, -sizeY / 2, 0); //change later

        Vector4f eye = new Vector4f(0, 0, -100, 1); //
        rotMat.setIdentity();

        // Camera orientation
        ps.mulPose(Vector3f.XP.rotationDegrees(-pitch));
        rotMat.multiply(Vector3f.XP.rotationDegrees(pitch));
        ps.mulPose(Vector3f.YP.rotationDegrees(-yaw));
        rotMat.multiply(Vector3f.YP.rotationDegrees(yaw));

        eye.transform(rotMat);
        eye.perspectiveDivide();
        renderWorld(ps, partialTick);

        ps.popPose();
    }

    private void renderWorld(PoseStack poseStack, float tick) {
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        // Render configurables
        //TODO: recheck when capacitor banks
        for (var configurable : configurables) {
            var pos = new Vector3f(configurable.getX() - origin.x(), configurable.getY() - origin.y(), configurable.getZ() - origin.z());
            renderBlock(poseStack, configurable, pos, buffers);
        }

        // RenderNeighbours
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        for (var neighbour : neighbours) {
            var pos = new Vector3f(neighbour.getX() - origin.x(), neighbour.getY() - origin.y(), neighbour.getZ() - origin.z());
            renderBlock(poseStack, neighbour, pos, buffers);
        }

        poseStack.popPose();
        buffers.endBatch();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    private void renderBlock(PoseStack poseStack, BlockPos blockPos, Vector3f renderPos, MultiBufferSource.BufferSource buffers) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        poseStack.pushPose();
        poseStack.translate(renderPos.x(), renderPos.y(), renderPos.z());
        var blockState = level.getBlockState(blockPos);
        var renderer = mc.getBlockRenderer();

        //        var bakedModel = renderer.getBlockModel(blockState);
        //        var vertexConsumer = new GhostVertexConsumer(buffers.getBuffer(TransparentRenderType.TRANSPARENT), 255);
        //        var modelData = level.getModelDataManager().getAt(blockPos);

        //        renderer
        //            .getModelRenderer()
        //            .renderModel(poseStack.last(), vertexConsumer, blockState, bakedModel, r, g, b, LightTexture.FULL_BLOCK, OverlayTexture.NO_OVERLAY, ModelData.EMPTY,
        //                TransparentRenderType.TRANSPARENT);
        renderer.renderSingleBlock(blockState, poseStack, buffers, LightTexture.FULL_BLOCK, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

    }

    private void renderBlockWithAlpha(PoseStack poseStack, BlockPos blockPos, Vector3f renderPos, MultiBufferSource.BufferSource buffers) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        poseStack.pushPose();
        poseStack.translate(renderPos.x(), renderPos.y(), renderPos.z());
        var blockState = level.getBlockState(blockPos);
        var renderer = mc.getBlockRenderer();

        var bakedModel = renderer.getBlockModel(blockState);
        var vertexConsumer = new GhostVertexConsumer(buffers.getBuffer(TransparentRenderType.TRANSPARENT), 240);
        var modelData = level.getModelDataManager().getAt(blockPos);
        var blockColor = mc.getBlockColors().getColor(blockState, level, blockPos, 0);
        var r = FastColor.ARGB32.red(blockColor) / 255F;
        var g = FastColor.ARGB32.green(blockColor) / 255F;
        var b = FastColor.ARGB32.blue(blockColor) / 255F;

        //        renderer
        //            .getModelRenderer()
        //            .tesselateWithoutAO(level, bakedModel, blockState, blockPos, poseStack, vertexConsumer, false, RandomSource.create(), blockState.getSeed(blockPos),
        //                LightTexture.FULL_BLOCK, modelData, TransparentRenderType.TRANSPARENT);
        renderer
            .getModelRenderer()
            .renderModel(poseStack.last(), vertexConsumer, blockState, bakedModel, r, g, b, LightTexture.FULL_BLOCK, OverlayTexture.NO_OVERLAY, modelData,
                TransparentRenderType.TRANSPARENT);
        //                renderer.renderSingleBlock(bs, ms, buffers, LightTexture.FULL_BLOCK, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    /**
     * Thanks XFactHD/FramedBlocks and ApexStudios-Dev/FantasyFurniture/
     * Modification to default {@link VertexConsumer} which overrides alpha to allow semi-transparent rendering
     */
    record GhostVertexConsumer(VertexConsumer delegate, int alpha) implements VertexConsumer {
        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            return delegate.vertex(x, y, z);
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return delegate.color(red, green, blue, (alpha * this.alpha) / 0xFF);
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
        public void defaultColor(int defaultR, int defaultG, int defaultB, int defaultA) {
            delegate.defaultColor(defaultR, defaultG, defaultB, defaultA);
        }

        @Override
        public void unsetDefaultColor() {
            delegate.unsetDefaultColor();
        }
    }

    class TransparentRenderType extends RenderType {

        public TransparentRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling,
            boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
            super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
        }

        public static final RenderType TRANSPARENT = create("enderio_ioconfig_transparent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false,
            true, RenderType.CompositeState.builder()
                // block texture
                .setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
                // translucency
                .setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                //                .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
                .setLightmapState(RenderStateShard.LIGHTMAP) // render with proper block lighting
                //                .setOverlayState(new CustomOverlay()) // used for overlay color (red when invalid placement)

                .setCullState(RenderStateShard.CULL)
                // disable depth test (see through walls)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                //                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setDepthTestState(new RenderStateShard.DepthTestStateShard("enderio_not_equal", GL11.GL_NOTEQUAL))
                .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING) // fixes z-fighting?, when in same space as other blocks
                .createCompositeState(false));
    }
}
