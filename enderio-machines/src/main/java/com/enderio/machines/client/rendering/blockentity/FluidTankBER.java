package com.enderio.machines.client.rendering.blockentity;

import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.fluid.FluidTankMaster;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class FluidTankBER implements BlockEntityRenderer<FluidTankBlockEntity> {
    public FluidTankBER(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(FluidTankBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
        int packedOverlay) {

        FluidTankMaster tank = blockEntity.getFluidTank();

        // Don't waste time if there's no fluid.
        if (tank.getFluidAmount() > 0) {
            PoseStack.Pose last = poseStack.last();
            FluidStack fluid = tank.getFluid();

            // Determine the fluid's preferred buffer
            VertexConsumer buffer;
            if (ItemBlockRenderTypes.canRenderInLayer(fluid.getFluid().defaultFluidState(), RenderType.translucent())) {
                buffer = bufferSource.getBuffer(RenderType.translucent());
            } else {
                buffer = bufferSource.getBuffer(RenderType.solid());
            }

            // Render the fluid
            renderFluid(blockEntity, fluid, tank.getFluidAmount() / (float) tank.getCapacity(), last.pose(), last.normal(), buffer);
        }
    }

    // TODO: Generalise as much rendering crap as possible

    private void renderFluid(BlockEntity entity, FluidStack fluidStack, float fillAmount, Matrix4f pose, Matrix3f normal, VertexConsumer consumer) {
        // Get fluid crap
        FluidAttributes attributes = fluidStack.getFluid().getAttributes();
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(attributes.getStillTexture());
        int color = attributes.getColor(entity.getLevel(), entity.getBlockPos());

        // Get sizes
        float fluidHeight = (14 * fillAmount) / 16.0f;
        float depth = 0.0625F;

        float minX = depth;
        float maxX = 1.0f - depth;
        float minY = depth;
        float maxY = depth + fluidHeight;
        float minZ = depth;
        float maxZ = 1 - minZ;

        // Up
        this.renderFace(pose, normal, consumer, texture, color, 0.0625F, 1.0F - 0.0625F, 0.0625F + fluidHeight, 0.0625F + fluidHeight, 1.0F - 0.0625F, 1.0F - 0.0625F, 0.0625F, 0.0625F);

        // South
        this.renderFace(pose, normal, consumer, texture, color, 0.0625F, 1.0F - 0.0625f, 0.0625F, 0.0625f + fluidHeight, 1.0F - depth, 1.0F - depth, 1.0F - depth, 1.0F - depth);

        // North
        this.renderFace(pose, normal, consumer, texture, color, 0.0625F, 1.0F - 0.0625F, 0.0625F + fluidHeight, 0.0625F, depth, depth, depth, depth);

        // East
        this.renderFace(pose, normal, consumer, texture, color, depth, depth, 0.0625F, 0.0625F + fluidHeight, 0.0625f, 1.0F - 0.0625f, 1.0F - 0.0625f, 0.0625F);

        // West
        this.renderFace(pose, normal, consumer, texture, color, 1.0F - depth, 1.0F - depth, 0.0625F + fluidHeight, 0.0625F, 0.0625f, 1.0F - 0.0625f, 1.0F - 0.0625f, 0.0625F);
    }

    private void renderFace(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, TextureAtlasSprite texture, int color, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3) {
        // TODO: Get U/V's using the height of the face so it doesn't squash the texture.

        // Smarter method that doesn't need to run calculations every frame.
        float height;
        if (y0 > y1)
            height = y0 - y1;
        else height = y1 - y0;

        float minU = 0.0625f * texture.getWidth(); // pos 1 in a 16x16
        float maxU = (1.0f - 0.0625f) * texture.getWidth(); // pos 15 in a 16x16
        float minV = 0.0625f * texture.getHeight(); // pos 1 in a 16x16
        float maxV = height * texture.getHeight(); // pos 15 in a 16x16

        consumer.vertex(pose, x0, y0, z0).color(color).uv(texture.getU(minU), texture.getV(minV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
        consumer.vertex(pose, x1, y0, z1).color(color).uv(texture.getU(maxU), texture.getV(minV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
        consumer.vertex(pose, x1, y1, z2).color(color).uv(texture.getU(maxU), texture.getV(maxV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
        consumer.vertex(pose, x0, y1, z3).color(color).uv(texture.getU(minU), texture.getV(maxV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
    }
}
