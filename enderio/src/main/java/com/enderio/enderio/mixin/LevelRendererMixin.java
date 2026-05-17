package com.enderio.enderio.mixin;

import com.enderio.enderio.client.content.travel.TravelTargetRendering;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ResourceManagerReloadListener, AutoCloseable{

    @Final
    @Shadow
    LevelRenderState levelRenderState;

    @Inject(method = "lambda$addLateDebugPass$0", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/systems/RenderSystem;outputDepthTextureOverride:Lcom/mojang/blaze3d/textures/GpuTextureView;", opcode = Opcodes.PUTSTATIC, ordinal = 0, shift = At.Shift.AFTER))
    private void renderTravel(GpuBufferSlice fog, ResourceHandle<RenderTarget> mainTarget, CameraRenderState camera, Matrix4fc modelViewMatrix, CallbackInfo ci) {
        List<TravelTargetRendering.ExtractTravelTarget> renderStates = this.levelRenderState.getRenderData(TravelTargetRendering.DATA_KEY);
        if (renderStates == null) {
            return;
        }

        if (mainTarget.get().getDepthTexture() != null) {
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(mainTarget.get().getDepthTexture(), 1.0);
        }

        renderStates.sort(Comparator.comparingDouble(TravelTargetRendering.ExtractTravelTarget::distanceSquared).reversed());
        for (TravelTargetRendering.ExtractTravelTarget state : renderStates) {
            PoseStack poseStack = new PoseStack();
            poseStack.pushPose();
            Vec3 projectedView = camera.pos;
            poseStack.translate(
                state.target().pos().getX() - projectedView.x,
                state.target().pos().getY() - projectedView.y,
                state.target().pos().getZ() - projectedView.z);

            // needed for smooth rendering
            // the boolean value controls whether it's still smooth while the game world is
            // paused (e.g. /tick freeze)
            TravelTargetRendering.render(state.target(), (LevelRenderer) (Object) this, poseStack, state.distanceSquared(), state.active(), state.partialTick());
            poseStack.popPose();
        }
    }
}
