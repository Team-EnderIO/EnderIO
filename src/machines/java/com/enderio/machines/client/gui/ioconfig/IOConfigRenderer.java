package com.enderio.machines.client.gui.ioconfig;

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

import java.util.ArrayList;
import java.util.List;


public class IOConfigRenderer<S extends Screen & IEnderScreen> {
    public record SelectedFace(@NotNull BlockPos block, @NotNull Direction face) {}

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
        //                renderSelection(pPoseStack);
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
        Vector4f eye = new Vector4f(0, 0, -100, 0);
        eye.transform(rotMat);
        Vector3f rayStart = new Vector3f(eye);

        Vector3f rayEnd = convertPixelToRay(bounds, pMouseX, pMouseY);
        rayEnd.mul(100); //change later
        rayEnd.add(rayStart);

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

        // debug stuff
        //        EnderIO.LOGGER.debug(debugPixelToRay(bounds, 0, 0));
        debugRenderRay(rayStart, rayEnd);
        //        var diff = rayEnd.copy();
        //        diff.sub(rayStart);
        //        EnderIO.LOGGER.info("start:" + rayStart + " end:" + rayEnd + "diff:" + diff);
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
            renderBlockWithAlpha(poseStack, neighbour, pos, buffers);
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
        RenderType renderType = Minecraft.useShaderTransparency() ? Sheets.translucentItemSheet() : Sheets.translucentCullBlockSheet();
        var vertexConsumer = new GhostVertexConsumer(buffers.getBuffer(renderType), 100);
        var modelData = level.getModelDataManager().getAt(blockPos);
        var blockColor = mc.getBlockColors().getColor(blockState, level, blockPos, 0);
        var r = FastColor.ARGB32.red(blockColor) / 255F;
        var g = FastColor.ARGB32.green(blockColor) / 255F;
        var b = FastColor.ARGB32.blue(blockColor) / 255F;

        renderer
            .getModelRenderer()
            .renderModel(poseStack.last(), vertexConsumer, blockState, bakedModel, r, g, b, LightTexture.FULL_SKY, OverlayTexture.NO_OVERLAY, modelData,
                renderType);
        poseStack.popPose();
    }

    public Vector3f convertPixelToRay(Rect2i bounds, double x, double y) {
        // Calculate the pixel location in screen clip space (width and height from
        // -1 to 1)
        float screenX = (float) ((x - bounds.getX()) / bounds.getWidth());
        float screenY = (float) ((y - bounds.getY()) / bounds.getHeight());
        screenX = (float) ((screenX * 2.0) - 1.0);
        screenY = (float) ((screenY * 2.0) - 1.0);

        Vector4f ray_clip = new Vector4f(screenX, screenY, -1, 1);
        var proj = RenderSystem.getProjectionMatrix().copy();
        proj.invert();
        ray_clip.transform(proj);

        ray_clip = new Vector4f(ray_clip.x(), ray_clip.y(), -1, 0);
        var view = rotMat.copy();
        view.invert();
        ray_clip.transform(view);

        var world = new Vector3f(ray_clip);
        world.normalize();
        return world;
    }

    public Vector3f debugPixelToRay(Rect2i bounds, double x, double y) {
        // Calculate the pixel location in screen clip space (width and height from
        // -1 to 1)
        float screenX = (float) ((x - bounds.getX()) / bounds.getWidth());
        float screenY = (float) ((y - bounds.getY()) / bounds.getHeight());
        screenX = (float) ((screenX * 2.0) - 1.0);
        screenY = (float) ((screenY * 2.0) - 1.0);

        Vector4f ray_clip = new Vector4f(screenX, screenY, -1, 1);
        var proj = Matrix4f.orthographic(1, 1, 0.001f, 100);
        proj.invert();
        ray_clip.transform(proj);

        ray_clip = new Vector4f(ray_clip.x(), ray_clip.y(), -1, 0);
        var view = new Matrix4f();
        view.setIdentity();
        view.invert();
        ray_clip.transform(view);

        var world = new Vector3f(ray_clip);
        world.normalize();
        return world;
    }

    public void debugRenderRay(Vector3f start, Vector3f end) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(start.x(), start.y(), start.z()).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(end.x(), end.y(), end.z()).color(255, 255, 255, 255).endVertex();
        tessellator.end();
    }

}
