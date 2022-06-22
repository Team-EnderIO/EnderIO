package com.enderio.machines.client.rendering.blockentity;

import com.enderio.base.client.renderer.RenderUtil;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidTankBER implements BlockEntityRenderer<FluidTankBlockEntity> {
    public FluidTankBER(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(FluidTankBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
        int packedOverlay) {

        FluidTank tank = blockEntity.getFluidTank();

        // Don't waste time if there's no fluid.
        if (tank.getFluidAmount() > 0) {
            FluidStack fluidStack = tank.getFluid();

            // Determine the fluid's preferred buffer
            VertexConsumer buffer;
            if (ItemBlockRenderTypes.canRenderInLayer(fluidStack.getFluid().defaultFluidState(), RenderType.translucent())) {
                buffer = bufferSource.getBuffer(RenderType.translucent());
            } else {
                buffer = bufferSource.getBuffer(RenderType.solid());
            }

            // Render the fluid
            PoseStack.Pose last = poseStack.last();
            renderFluid(last.pose(), last.normal(), buffer, blockEntity, fluidStack.getFluid(), tank.getFluidAmount() / (float) tank.getCapacity());
        }
    }

    private static void renderFluid(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, BlockEntity entity, Fluid fluid, float fillAmount) {
        renderFluid(pose, normal, consumer, fluid, fillAmount, fluid.getAttributes().getColor(entity.getLevel(), entity.getBlockPos()));
    }

    public static void renderFluid(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, Fluid fluid, float fillAmount, int color) {
        // Get fluid texture
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluid.getAttributes().getStillTexture());

        // Get sizes
        float fluidHeight = (14 * fillAmount) / 16.0f;
        float inset = 0.0625F;
        float faceSize = 14 / 16.0f;


        // Top
        RenderUtil.renderFace(Direction.UP, pose, normal, consumer, texture, inset, inset, inset + fluidHeight, faceSize, faceSize, color);

        // Sides
        RenderUtil.renderFace(Direction.SOUTH, pose, normal, consumer, texture, inset, inset, inset, faceSize, fluidHeight, color);
        RenderUtil.renderFace(Direction.NORTH, pose, normal, consumer, texture, inset, inset, inset, faceSize, fluidHeight, color);
        RenderUtil.renderFace(Direction.EAST, pose, normal, consumer, texture, inset, inset, inset, faceSize, fluidHeight, color);
        RenderUtil.renderFace(Direction.WEST, pose, normal, consumer, texture, inset, inset, inset, faceSize, fluidHeight, color);
    }
}
