package com.enderio.base.client.renderer.item;

import com.enderio.base.common.util.EnergyUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class ItemBarRenderer {

    public static final int ENERGY_BAR_RGB = 0x00B168E4;
    public static final int FLUID_BAR_RGB = 0x99BD42; // TODO: Defer to the item to decide colour based on fluid?

    public static void renderEnergyOverlay(ItemStack pStack, int pXPosition, int pYPosition) {
        // Hide bar if no energy
        if (EnergyUtil.getMaxEnergyStored(pStack) <= 0) {
            return;
        }

        // Determine fill ratio
        double fillRatio = pStack
            .getCapability(CapabilityEnergy.ENERGY)
            .map(energyStorage -> 1.0d - (double) energyStorage.getEnergyStored() / (double) energyStorage.getMaxEnergyStored())
            .orElse(0d);

        // Render the bar overlay
        renderOverlay(pXPosition, pYPosition, fillRatio, ENERGY_BAR_RGB);
    }

    public static void renderFluidOverlay(ItemStack pStack, int tank, int pXPosition, int pYPosition) {
        pStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
            if (handler.getFluidInTank(tank).getAmount() <= 0) {
                return;
            }

            double fillRatio = 1.0D - (double) handler.getFluidInTank(tank).getAmount() / (double) handler.getTankCapacity(tank);

            IFluidTypeRenderProperties props = RenderProperties.get(handler.getFluidInTank(tank).getFluid());
            renderOverlay(pXPosition, pYPosition, fillRatio, props.getColorTint()); // TODO: Test this out!
        });
    }

    public static void renderOverlay(int pXPosition, int pYPosition, double fillRatio, int pColor) {
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        int offset = 12;
        int barWidth = Math.round(13.0F - (float) fillRatio * 13.0F);
        fillRect(bufferbuilder, pXPosition + 2, pYPosition + offset, 13, 1, 0, 0, 0);
        fillRect(bufferbuilder, pXPosition + 2, pYPosition + offset, barWidth, 1, FastColor.ARGB32.red(pColor), FastColor.ARGB32.green(pColor),
            FastColor.ARGB32.blue(pColor));

        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }

    private static void fillRect(BufferBuilder pRenderer, int pX, int pY, int pWidth, int pHeight, int pRed, int pGreen, int pBlue) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        pRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        pRenderer.vertex(pX, pY, 0.0D).color(pRed, pGreen, pBlue, 255).endVertex();
        pRenderer.vertex(pX, pY + pHeight, 0.0D).color(pRed, pGreen, pBlue, 255).endVertex();
        pRenderer.vertex(pX + pWidth, pY + pHeight, 0.0D).color(pRed, pGreen, pBlue, 255).endVertex();
        pRenderer.vertex(pX + pWidth, pY, 0.0D).color(pRed, pGreen, pBlue, 255).endVertex();
        BufferUploader.draw(pRenderer.end());
    }

}
