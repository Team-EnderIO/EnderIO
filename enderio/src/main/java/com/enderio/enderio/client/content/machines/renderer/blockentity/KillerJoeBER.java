package com.enderio.enderio.client.content.machines.renderer.blockentity;

import com.enderio.core.client.FluidRendererUtil;
import com.enderio.enderio.content.machines.killer_joe.KillerJoeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class KillerJoeBER implements BlockEntityRenderer<KillerJoeBlockEntity, KillerJoeRenderState> {

    // How long the sword swing animation lasts, in ticks.
    private static final float SWING_TICKS = 6;
    private static final float SWING_ANGLE = 45;

    // Sword placement, relative to the block centre with Joe facing -Z. Joe's right hand is just outside the +X face, low and towards the back.
    private static final float HAND_X = 0.5f + 1 / 16f;
    private static final float HAND_Y = 4.5f / 16f;
    private static final float HAND_Z = 1.5f / 16f;
    private static final float WEAPON_SCALE = 0.7f;

    // Offset from the item centre to the hilt, along both sprite axes (the hilt is near the bottom left of the sprite), unscaled.
    private static final float HILT_OFFSET = 6 / 16f;

    // The tank inside the model spans y = 4px to 12px.
    private static final float TANK_BOTTOM = 4 / 16f;
    private static final float TANK_HEIGHT = 8 / 16f;

    public KillerJoeBER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public KillerJoeRenderState createRenderState() {
        return new KillerJoeRenderState();
    }

    @Override
    public void extractRenderState(KillerJoeBlockEntity blockEntity, KillerJoeRenderState renderState, float partialTick, Vec3 cameraPosition,
        ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.fluidStack = blockEntity.getStoredFluid().copy();
        renderState.tankCapacity = blockEntity.getFluidCapacity();
        renderState.facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

        // NONE so the sprite is unmirrored (FIXED turns flat items around), keeping the hilt at the bottom left.
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(renderState.weapon, blockEntity.getDisplayWeapon(),
            ItemDisplayContext.NONE, blockEntity.getLevel(), null, 0);

        renderState.swingProgress = 0;
        if (blockEntity.getLevel() != null) {
            float ticksSinceAttack = blockEntity.getLevel().getGameTime() - blockEntity.getLastAttackTime() + partialTick;
            if (ticksSinceAttack >= 0 && ticksSinceAttack < SWING_TICKS) {
                renderState.swingProgress = ticksSinceAttack / SWING_TICKS;
            }
        }
    }

    @Override
    public void submit(KillerJoeRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
        CameraRenderState cameraRenderState) {

        submitFluid(renderState, poseStack, submitNodeCollector);
        submitWeapon(renderState, poseStack, submitNodeCollector);
    }

    private void submitFluid(KillerJoeRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (renderState.fluidStack.isEmpty() || renderState.tankCapacity <= 0) {
            return;
        }

        float fillRatio = Mth.clamp(renderState.fluidStack.getAmount() / (float) renderState.tankCapacity, 0, 1);

        // FluidRendererUtil fills a 14px cube from y = 1px, squash it into the glass tank.
        poseStack.pushPose();
        poseStack.translate(0.5f, TANK_BOTTOM, 0.5f);
        poseStack.scale(0.98f, TANK_HEIGHT / (14 / 16f), 0.98f);
        poseStack.translate(-0.5f, -1 / 16f, -0.5f);
        FluidRendererUtil.submitFluid(poseStack, Sheets.translucentBlockItemSheet(), submitNodeCollector, renderState.fluidStack, fillRatio,
            renderState.lightCoords);
        poseStack.popPose();
    }

    private void submitWeapon(KillerJoeRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (renderState.weapon.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        // Rotate so that -Z (north in the model) is the front of the machine, where Joe faces.
        poseStack.translate(0.5f, 0, 0.5f);
        poseStack.rotate(Axis.YP.rotationDegrees(180 - renderState.facing.toYRot()));

        // Joe's right hand. Swing forwards (towards -Z) about the hilt.
        poseStack.translate(HAND_X, HAND_Y, HAND_Z);
        float swing = Mth.sin(renderState.swingProgress * Mth.PI) * SWING_ANGLE;
        poseStack.rotate(Axis.XP.rotationDegrees(-swing));

        // Lay the sword flat against the side at its natural diagonal, tip pointing up and forwards, with the hilt at the pivot.
        poseStack.translate(0, HILT_OFFSET * WEAPON_SCALE, -HILT_OFFSET * WEAPON_SCALE);
        poseStack.rotate(Axis.YP.rotationDegrees(90));
        poseStack.scale(WEAPON_SCALE, WEAPON_SCALE, WEAPON_SCALE);

        renderState.weapon.submit(poseStack, submitNodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
