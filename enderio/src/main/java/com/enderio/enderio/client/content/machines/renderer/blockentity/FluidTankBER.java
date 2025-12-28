package com.enderio.enderio.client.content.machines.renderer.blockentity;

import com.enderio.core.client.FluidRendererUtil;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FluidTankBER implements BlockEntityRenderer<FluidTankBlockEntity, FluidTankRenderState> {
    public FluidTankBER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public FluidTankRenderState createRenderState() {
        return new FluidTankRenderState();
    }

    @Override
    public void extractRenderState(FluidTankBlockEntity blockEntity, FluidTankRenderState renderState, float partialTick, Vec3 cameraPosition,
        ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        var tank = blockEntity.getFluidTank();
        renderState.fluidStack = tank.getFluid().copy();
        renderState.tankCapacity = tank.getCapacity();
    }

    @Override
    public void submit(FluidTankRenderState fluidTankRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
        CameraRenderState cameraRenderState) {

        float fillRatio = fluidTankRenderState.fluidStack.getAmount() / (float) fluidTankRenderState.tankCapacity;
        FluidRendererUtil.submitFluid(poseStack, Sheets.translucentItemSheet(), submitNodeCollector, fluidTankRenderState.fluidStack, fillRatio, fluidTankRenderState.lightCoords);
    }
}
