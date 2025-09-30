package com.enderio.enderio.machines.client.rendering.blockentity;

import com.enderio.core.client.FluidRendererUtil;
import com.enderio.enderio.machines.common.blocks.fluid_tank.FluidTankBlockEntity;
import com.enderio.enderio.machines.common.io.fluid.MachineFluidTank;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTankBER implements BlockEntityRenderer<FluidTankBlockEntity> {
    public FluidTankBER(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(FluidTankBlockEntity blockEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        MachineFluidTank tank = blockEntity.getFluidTank();

        // Don't waste time if there's no fluid.
        if (!tank.getFluid().isEmpty()) {
            FluidStack fluidStack = tank.getFluid();

            // Use entity translucent cull sheet so fluid renders in Fabulous
            VertexConsumer buffer = bufferSource.getBuffer(Sheets.translucentCullBlockSheet());

            // Render the fluid
            PoseStack.Pose last = poseStack.last();
            float fillRatio = tank.getFluidAmount() / (float) tank.getCapacity();

            FluidRendererUtil.renderFluid(last, buffer, fluidStack, fillRatio, packedLight);
        }
    }
}
