package com.enderio.machines.client.gui.widget;

import com.enderio.EnderIO;
import com.enderio.core.client.RenderUtil;
import com.enderio.core.client.gui.screen.IEnderScreen;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class IOConfigRenderer<S extends Screen & IEnderScreen> {

    private final S addedOn;
    private final Rect2i bounds;

    // Camera Variables
    private float pitch;
    private float yaw;
    private float distance;

    private final @NotNull Vector3f origin;
    private Vector4f eyePosition = new Vector4f();
    private Vector3f size;
    private final @NotNull Matrix4f rotMat = new Matrix4f();

    private @Nullable SelectedFace selection;
    private final List<BlockPos> configurables = new ArrayList<>();
    private final List<BlockPos> neighbours = new ArrayList<>();

    private static final ResourceLocation SELECTED_ICON = EnderIO.loc("block/overlay/selected_face");

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
        // render black bg
        GuiComponent.fill(pPoseStack, bounds.getX(), bounds.getY(), bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), 0xFF000000);

        renderScene(pPartialTick, pPoseStack, vp);
        //        renderSelection(pPoseStack);
        //        renderOverlay();
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

    public void handleMouseMove(double pMouseX, double pMouseY) {
        Vector3f rayStart = new Vector3f(eyePosition.x(), eyePosition.y(), eyePosition.z());
        Vector3f rayEnd = new Vector3f();
        var invProjMat = RenderSystem.getProjectionMatrix().copy();
        invProjMat.invert();
        //        var invViewMat = RenderSystem.getModelViewMatrix().copy();
        var invViewMat = rotMat.copy();
        invViewMat.setTranslation(eyePosition.x(), eyePosition.y(), eyePosition.z());
        invViewMat.invert();
        convertPixelToRay(bounds, invProjMat, invViewMat, pMouseX, pMouseY, rayEnd);

        rayEnd.mul(100 * 2); //change later
        rayEnd.add(rayStart);
        rayEnd.add(0.5f, 0.5f, 0.5f); // offset to block center

        //update Selection
        var mc = Minecraft.getInstance();
        rayStart.add(origin);
        rayEnd.add(origin);
        List<BlockHitResult> hits = new ArrayList<>();

        // improve later
        configurables.forEach(blockPos -> {
            var blockState = mc.level.getBlockState(blockPos);
            var voxelShape = ClipContext.Block.COLLIDER.get(blockState, mc.level, blockPos, CollisionContext.of(mc.player));
            BlockHitResult hitResult = mc.level.clipWithInteractionOverride(new Vec3(rayStart), new Vec3(rayEnd), blockPos, voxelShape, blockState);
            if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                hits.add(hitResult);
            }
        });
        var _origin = new Vec3(origin);
        var opt = hits.stream().min((a, b) -> (int) (a.getBlockPos().distToCenterSqr(_origin) - b.getBlockPos().distToCenterSqr(_origin))); // minimum

        if (opt.isPresent()) {
            var closest = opt.get();
            var face = closest.getDirection();
            selection = new SelectedFace(closest.getBlockPos(), face);
            EnderIO.LOGGER.info(face.getName());
        }
        EnderIO.LOGGER.info("end" + rayEnd);
    }

    private void renderSelection(PoseStack poseStack) {
        if (selection == null) {
            return;
        }
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        var mc = Minecraft.getInstance();

        // do I need a texture atlas ?
        TextureAtlasSprite tex = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SELECTED_ICON);
        RenderSystem.setShaderTexture(0, tex.atlas().location());

        poseStack.pushPose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        var blockPos = selection.block;
        poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        RenderUtil.getVerticesForFace(bufferbuilder, selection.face, new AABB(selection.block), tex.getU0(), tex.getU1(), tex.getV0(), tex.getV1());
        BufferUploader.drawWithShader(bufferbuilder.end());
        poseStack.popPose();

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }

    private void renderScene(float partialTick, PoseStack ps, Rect2i scaledBounds) {
        // TODO: Fix scaling issues
        float sizeX = size.x();
        float sizeY = size.y();
        float sizeZ = size.z();
        float diagonal = (float) Math.sqrt(sizeX * sizeX + sizeZ * sizeZ); //change later
        float scaleX = scaledBounds.getWidth() / diagonal;
        float scaleY = (float) scaledBounds.getHeight() / sizeY;
        float scale = -Math.min(scaleX, scaleY);

        int xPos = bounds.getX() + (bounds.getWidth() / 2);
        int yPos = bounds.getY() + (bounds.getHeight() / 2);

        ps.pushPose();
        ps.translate(xPos, yPos, 100);
        ps.scale(scale, scale, scale);
        //        ps.translate(-sizeX / 2, -sizeY / 2, 0); //change later

        eyePosition = new Vector4f(0, 0, -100, 1); //
        rotMat.setIdentity();

        // Camera orientation
        ps.mulPose(Vector3f.XP.rotationDegrees(-pitch));
        rotMat.multiply(Vector3f.XP.rotationDegrees(pitch));
        ps.mulPose(Vector3f.YP.rotationDegrees(-yaw));
        rotMat.multiply(Vector3f.YP.rotationDegrees(yaw));

        eyePosition.transform(rotMat);
        eyePosition.perspectiveDivide();
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
            // switch to alpha later
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

        renderer.renderSingleBlock(blockState, poseStack, buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
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

    public void convertPixelToRay(Rect2i bounds, Matrix4f ipm, Matrix4f ivm, double x, double y, Vector3f normalOut) {

        Matrix4f vpm = new Matrix4f();
        vpm.load(ivm);
        vpm.multiply(ipm);

        // Calculate the pixel location in screen clip space (width and height from
        // -1 to 1)
        float screenX = (float) ((x - bounds.getX()) / bounds.getWidth());
        float screenY = (float) ((y - bounds.getY()) / bounds.getHeight());
        screenX = (float) ((screenX * 2.0) - 1.0);
        screenY = (float) ((screenY * 2.0) - 1.0);

        // Now calculate the XYZ location of this point on the near plane
        Vector4f tmp = new Vector4f();
        tmp.setX(screenX);
        tmp.setY(screenY);
        tmp.setZ(-1);
        tmp.setW(1.0f);
        tmp.transform(vpm);

        float w = tmp.w();
        Vector3f nearXYZ = new Vector3f(tmp.x() / w, tmp.y() / w, tmp.z() / w);

        // and then on the far plane
        tmp.setX(screenX);
        tmp.setY(screenY);
        tmp.setZ(1);
        tmp.setW(1.0f);
        tmp.transform(vpm);

        w = tmp.w();
        Vector3f farXYZ = new Vector3f(tmp.x() / w, tmp.y() / w, tmp.z() / w);

        normalOut.load(farXYZ);
        normalOut.sub(nearXYZ);
        normalOut.normalize();
    }

    public record SelectedFace(@NotNull BlockPos block, @NotNull Direction face) {
        @Override
        public String toString() {
            return "SelectedFace [config=" + block + ", face=" + face + "]";
        }

    }

}
