package com.enderio.enderio.client.content.machines.renderer.blockentity;

import com.enderio.core.client.FluidRendererUtil;
import com.enderio.enderio.content.machines.niard.NiardBlockEntity;
import com.enderio.enderio.foundation.io.fluid.MachineFluidTank;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
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

        var tank = blockEntity.getFluidTank();
        renderState.fluidStack = tank.getFluid().copy();
        renderState.tankCapacity = tank.getCapacity();
    }

    @Override
    public void submit(NiardRenderState niardRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        float fillRatio = niardRenderState.fluidStack.getAmount() / (float) niardRenderState.tankCapacity;
        FluidRendererUtil.submitFluid(poseStack, Sheets.translucentItemSheet(), submitNodeCollector, niardRenderState.fluidStack, fillRatio, niardRenderState.lightCoords);
    }
}
