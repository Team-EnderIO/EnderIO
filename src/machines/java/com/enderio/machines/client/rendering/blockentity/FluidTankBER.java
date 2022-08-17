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
    public void render(FluidTankBlockEntity blockEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight,
            int packedOverlay) {

        FluidTank tank = blockEntity.getFluidTank();
        Fluid fluid = tank.getFluid().getFluid();
        int fluidAmount = tank.getFluidAmount();
        float capacity = (float) tank.getCapacity();

        // Check to see if there is an existing animation for this block.
        AnimationInformation information = map.get(blockEntity.getBlockPos());
        float frameTarget;
        if (information == null) {
            // No existing animation information (an unexpected state), so render as normal.
            frameTarget = fluidAmount / capacity;
        } else if (information.currentTarget == fluidAmount) {
            // The amount in the FluidTank hasn't changed (i.e. no fluid has been added or
            // removed).
            if (information.animation == null) {
                // There is no current animation on-going, so render as normal.
                frameTarget = fluidAmount / capacity;
            } else {
                // Advance the animation and update the level to animate at.
                information.animation.updateByPartialTick(partialTick);
                frameTarget = information.animation.getCurrent();

                // If the animation has completed, clear it from the information.
                if (information.animation.isComplete()) {
                    information.animation = null;
                    information.fluid = fluid;
                }
            }
        } else {
            // The amount in the FluidTank has changed, so begin animation to new value.
            try {
                // Start at the current level, or whatever level the previous animation got to.
                float start = information.animation == null ? information.currentTarget / capacity
                        : information.animation.getCurrent();
                float target = fluidAmount / capacity;
                information.animation = new Animation(start, target, ANIMATION_TICKS);

                // If the new fluid is EMPTY (i.e. the FluidTank is now empty), then we need to
                // retain the 'old' fluid so that the emptying animates correctly.
                if (fluid != Fluids.EMPTY) {
                    information.fluid = fluid;
                }

                information.currentTarget = fluidAmount;
                frameTarget = information.animation.getCurrent();
            } catch (Exception e) {
                // Catching exception from Animation constructor, just render as normal.
                frameTarget = fluidAmount / capacity;
            }
        }

        // Don't waste time rendering if there's no fluid.
        if (frameTarget > 0) {
            Fluid fluidToRender = information == null ? fluid : information.fluid;

            // Get the preferred render buffer
            VertexConsumer buffer = bufferSource
                    .getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidToRender.defaultFluidState()));

            // Render the fluid
            PoseStack.Pose last = poseStack.last();
            renderFluid(last.pose(), last.normal(), buffer, blockEntity, fluidToRender, frameTarget);
        }
    }

    private static void renderFluid(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, BlockEntity entity,
            Fluid fluid, float fillAmount) {
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid);
        renderFluid(pose, normal, consumer, fluid, fillAmount,
                props.getTintColor(null, entity.getLevel(), entity.getBlockPos()));
    }

    public static void renderFluid(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, Fluid fluid,
            float fillAmount, int color) {
        // Get fluid texture
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(props.getStillTexture());

        // Get sizes
        float fluidHeight = (14 * fillAmount) / 16.0f;
        float inset = 0.0625F;
        float faceSize = 14 / 16.0f;

        // Top
        RenderUtil.renderFace(Direction.UP, pose, normal, consumer, texture, inset, inset, inset + fluidHeight,
                faceSize, faceSize, color);

        // Sides
        RenderUtil.renderFace(Direction.SOUTH, pose, normal, consumer, texture, inset, inset, inset, faceSize,
                fluidHeight, color);
        RenderUtil.renderFace(Direction.NORTH, pose, normal, consumer, texture, inset, inset, inset, faceSize,
                fluidHeight, color);
        RenderUtil.renderFace(Direction.EAST, pose, normal, consumer, texture, inset, inset, inset, faceSize,
                fluidHeight, color);
        RenderUtil.renderFace(Direction.WEST, pose, normal, consumer, texture, inset, inset, inset, faceSize,
                fluidHeight, color);
    }

    /**
     * Registers a {@link FluidTankBlockEntity} so that it can be animated.
     * 
     * @param entity {@link FluidTankBlockEntity} to be animated.
     */
    public static void addBlock(FluidTankBlockEntity entity) {
        BlockPos blockPos = entity.getBlockPos();
        if (map.containsKey(blockPos)) {
            return;
        }

        FluidTank tank = entity.getFluidTank();
        AnimationInformation information = new AnimationInformation(tank.getFluidAmount());
        information.fluid = tank.getFluid().getFluid();
        map.put(blockPos, information);
    }

    /**
     * Removes a {@link BlockPos} from the collection of items to be animated.
     * 
     * @param blockPos {@link BlockPos} to be removed.
     * @return Whether the blockPos was in the collection of items to be animated.
     */
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
