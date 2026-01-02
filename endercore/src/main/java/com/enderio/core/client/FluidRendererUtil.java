package com.enderio.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidRendererUtil {

    public static void submitFluid(PoseStack poseStack, RenderType renderType, SubmitNodeCollector nodeCollector, FluidStack fluidStack,
        float fillAmount, int packedLight) {
        if (fluidStack.isEmpty()) return;

        Fluid fluid = fluidStack.getFluid();
        int color = IClientFluidTypeExtensions.of(fluid)
            .getTintColor(fluidStack);
        if (color == -1) color = 0xFFFFFFFF;
        submitFluid(poseStack, renderType, nodeCollector, fluid, fillAmount, color, packedLight);
    }

    public static void submitFluid(PoseStack poseStack, RenderType renderType, SubmitNodeCollector nodeCollector, Fluid fluid, float fillAmount,
        int color, int packedLight) {
        // Get fluid texture
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite texture = Minecraft.getInstance()
            .getAtlasManager()
            .getAtlasOrThrow(AtlasIds.BLOCKS)
            .getSprite(props.getStillTexture());

        // Get sizes
        float fluidHeight = (14 * fillAmount) / 16.0f;
        float inset = 0.0625F;
        float faceSize = 14 / 16.0f;

        // Top
        RenderUtil.submitFace(Direction.UP, poseStack, renderType, nodeCollector, texture, inset, inset, inset + fluidHeight, faceSize,
            faceSize, color, packedLight);

        // Sides
        RenderUtil.submitFace(Direction.SOUTH, poseStack, renderType, nodeCollector, texture, inset, inset, inset, faceSize, fluidHeight,
            color, packedLight);
        RenderUtil.submitFace(Direction.NORTH, poseStack, renderType, nodeCollector, texture, inset, inset, inset, faceSize, fluidHeight,
            color, packedLight);
        RenderUtil.submitFace(Direction.EAST, poseStack, renderType, nodeCollector, texture, inset, inset, inset, faceSize, fluidHeight,
            color, packedLight);
        RenderUtil.submitFace(Direction.WEST, poseStack, renderType, nodeCollector, texture, inset, inset, inset, faceSize, fluidHeight,
            color, packedLight);
    }
}
