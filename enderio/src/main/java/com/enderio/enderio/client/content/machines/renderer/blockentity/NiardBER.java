package com.enderio.enderio.client.content.machines.renderer.blockentity;

import com.enderio.core.client.FluidRendererUtil;
import com.enderio.enderio.content.machines.niard.NiardBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class NiardBER implements BlockEntityRenderer<NiardBlockEntity, NiardRenderState> {
    public NiardBER(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public NiardRenderState createRenderState() {
        return new NiardRenderState();
    }

    @Override
    public void extractRenderState(NiardBlockEntity blockEntity, NiardRenderState renderState, float partialTick, Vec3 cameraPosition,
        ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.fluidStack = blockEntity.getStoredFluid().copy();
        renderState.tankCapacity = NiardBlockEntity.CAPACITY;
    }

    @Override
    public void submit(NiardRenderState niardRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        float fillRatio = niardRenderState.fluidStack.getAmount() / (float) niardRenderState.tankCapacity;
        FluidRendererUtil.submitFluid(poseStack, Sheets.translucentItemSheet(), submitNodeCollector, niardRenderState.fluidStack, fillRatio, niardRenderState.lightCoords);
    }
}
