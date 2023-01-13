package com.enderio.machines.client.gui.widget;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.screen.IEnderScreen;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import org.jetbrains.annotations.NotNull;

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
    private final @NotNull Vector4f eye = new Vector4f();
    private final @NotNull Matrix4f rotMat = new Matrix4f();
    private final List<BlockPos> configurables = new ArrayList<>();
    private final NonNullList<BlockPos> neighbours = NonNullList.create();

    public IOConfigRenderer(S addedOn, Rect2i bounds, MachineBlockEntity configurable) {
        this(addedOn, bounds, List.of(configurable.getBlockPos()));
    }

    public IOConfigRenderer(S addedOn, Rect2i bounds, List<BlockPos> _configurables) {
        this.addedOn = addedOn;
        this.bounds = bounds;
        this.configurables.addAll(_configurables);

        Vector3f c;
        Vector3f size;
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

        var player = Minecraft.getInstance().player;
        //        pitch = -player.getXRot();
        //        yaw = 180 - player.getYRot();

        distance = Math.max(Math.max(size.x(), size.y()), size.z()) + 4;

        configurables.forEach(pos -> {
            for (Direction dir : Direction.values()) {
                BlockPos loc = pos.offset(dir.getNormal());
                if (!configurables.contains(loc)) {
                    neighbours.add(loc);
                }
            }

        });
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick, Rect2i vp) {
        GuiComponent.fill(pPoseStack, bounds.getX(), bounds.getY(), bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), 0xFF000000);

        updateCamera(pPartialTick, pPoseStack, vp);
    }

    private boolean updateCamera(float partialTick, PoseStack ps, Rect2i vp) {
        ps.pushPose();

        eye.set(0, 0, distance, 1);
        rotMat.setIdentity();

        // Camera orientation
        ps.mulPose(Vector3f.XP.rotation(-pitch));
        rotMat.multiply(Vector3f.XP.rotation(pitch));
        ps.mulPose(Vector3f.YP.rotation(-yaw));
        rotMat.multiply(Vector3f.YP.rotation(yaw));

        eye.transform(rotMat);
        eye.perspectiveDivide();
        renderWorld(ps, partialTick);

        ps.popPose();

        return true;
    }

    private void renderWorld(PoseStack ms, float tick) {
        ms.pushPose();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

        ms.pushPose();
        ms.translate(bounds.getX() + (bounds.getWidth() / 2), bounds.getY() + (bounds.getHeight() / 2), -10);
        var mc = Minecraft.getInstance();
        var level = mc.level;
        var bs = level.getBlockState(configurables.get(0));
        var renderer = mc.getBlockRenderer();
        try {
            renderer.renderSingleBlock(bs, ms, buffers, LightTexture.FULL_BLOCK, OverlayTexture.NO_OVERLAY);
        } catch (Exception e) {
            EnderIO.LOGGER.error("render fail", e);
        }

        ms.popPose();

        buffers.endBatch();
        ms.popPose();
    }

    private void applyCamera(float partialTick) {
        //        Rect2i vp = camera.getViewport();
        RenderSystem.viewport(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        //        final Matrix4d cameraViewMatrix = camera.getTransposeProjectionMatrix();
        //        RenderSystem.setProjectionMatrix(cameraViewMatrix);
        //        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        //        final Matrix4d cameraViewMatrix = camera.getTransposeViewMatrix();
        //        if (cameraViewMatrix != null) {
        //            RenderUtil.loadMatrix(cameraViewMatrix);
        //        }
        //        GL11.glTranslatef(-(float) eye.x, -(float) eye.y, -(float) eye.z);
    }
}
