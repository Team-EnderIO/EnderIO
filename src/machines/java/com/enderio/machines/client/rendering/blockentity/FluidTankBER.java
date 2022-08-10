package com.enderio.machines.client.rendering.blockentity;

import java.util.HashMap;

import com.enderio.core.client.RenderUtil;
import com.enderio.core.client.rendering.Animation;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidTankBER implements BlockEntityRenderer<FluidTankBlockEntity> {
    private static float ANIMATION_TICKS = 20.0f;
    private static HashMap<BlockPos, AnimationInformation> map = new HashMap<BlockPos, AnimationInformation>();

    public FluidTankBER(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(FluidTankBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
        int packedOverlay) {

        FluidTank tank = blockEntity.getFluidTank();
        Fluid fluid = tank.getFluid().getFluid();
        int fluidAmount = tank.getFluidAmount();
        float capacity = (float)tank.getCapacity();

        AnimationInformation information = map.get(blockEntity.getBlockPos());
        float frameTarget;
        if (information == null) {
            frameTarget = fluidAmount / capacity;
        } else if (information.currentTarget == fluidAmount) {
            if (information.animation == null) {
                frameTarget = fluidAmount / capacity;
            } else {
                information.animation.updateByPartialTick(partialTick);
                frameTarget = information.animation.getCurrent();
                if (information.animation.isComplete()) {
                    information.animation = null;
                    information.fluid = fluid;
                }
            }
        } else {
            try {
                float start = information.animation == null ? information.currentTarget / capacity : information.animation.getCurrent();
                float target = fluidAmount / capacity;
                information.animation = new Animation(start, target, ANIMATION_TICKS);

                if (fluid != Fluids.EMPTY) {
                    information.fluid = fluid;
                }

                information.currentTarget = fluidAmount;
                frameTarget = information.animation.getCurrent();
            } catch (Exception e) {
                frameTarget = fluidAmount / capacity;
            }
        }

        // Don't waste time if there's no fluid.
        if (frameTarget > 0) {
            Fluid fluidToRender = information == null ? fluid : information.fluid;

            // Get the preferred render buffer
            VertexConsumer buffer = bufferSource.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidToRender.defaultFluidState()));

            // Render the fluid
            PoseStack.Pose last = poseStack.last();
            renderFluid(last.pose(), last.normal(), buffer, blockEntity, fluidToRender, frameTarget);
        }
    }

    private static void renderFluid(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, BlockEntity entity, Fluid fluid, float fillAmount) {
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid);
        renderFluid(pose, normal, consumer, fluid, fillAmount, props.getTintColor(null, entity.getLevel(), entity.getBlockPos()));
    }

    public static void renderFluid(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, Fluid fluid, float fillAmount, int color) {
        // Get fluid texture
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(props.getStillTexture());

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

    public static void addBlock(FluidTankBlockEntity entity) {
        FluidTank tank = entity.getFluidTank();

        BlockPos blockPos = entity.getBlockPos();
        if (!map.containsKey(blockPos)) {
            AnimationInformation information = new AnimationInformation(tank.getFluidAmount());
            information.fluid = tank.getFluid().getFluid();
            map.put(blockPos, information);
        }
    }

    public static boolean removeBlock(BlockPos blockPos) {
        return map.remove(blockPos) != null;
    }

    private static class AnimationInformation {
        public int currentTarget;
        public Animation animation = null;
        public Fluid fluid = null;

        public AnimationInformation(int currentTarget) {
            this.currentTarget = currentTarget;
        }
    }
}
