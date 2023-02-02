package com.enderio.machines.client.gui.widget;

import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.client.gui.widgets.EIOWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Supplier;

public class ExperienceWidget extends EIOWidget {

    private final Screen displayOn;
    private final Supplier<FluidTank> getFluid;
    private final Supplier<Integer> maxXP;

    public ExperienceWidget(Screen displayOn, Supplier<FluidTank> getFluid, Supplier<Integer> maxXP, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.displayOn = displayOn;
        this.getFluid = getFluid;
        this.maxXP = maxXP;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        int k = 1;
        if (maxXP.get() > 0) {
            k = (int) ((ExperienceUtil.getLevelFromFluid(getFluid.get().getFluidAmount(), 0, maxXP.get()) /(float)maxXP.get()) * this.width);
        }
        blit(pPoseStack, this.x, this.y, this.displayOn.getBlitOffset(), 0, 64, this.width-1, this.height, 256, 256);
        blit(pPoseStack, this.x + this.width-1, this.y, this.displayOn.getBlitOffset(), 181, 64, 1, this.height, 256, 256);
        blit(pPoseStack, this.x, this.y, this.displayOn.getBlitOffset(), 0, 69, k, this.height, 256, 256);
        blit(pPoseStack, this.x + this.width-1, this.y, this.displayOn.getBlitOffset(), 181, 64, k==this.width? 1 : 0, this.height, 256, 256);

        String s = "" + maxXP.get();
        Minecraft.getInstance().font.draw(pPoseStack, s, (this.x + this.width/2 + 1), (float)this.y - this.height - 3, 0);
        Minecraft.getInstance().font.draw(pPoseStack, s, (this.x + this.width/2 - 1), (float)this.y - this.height - 3, 0);
        Minecraft.getInstance().font.draw(pPoseStack, s, this.x + this.width/2, (float)(this.y - this.height - 3 + 1), 0);
        Minecraft.getInstance().font.draw(pPoseStack, s, this.x + this.width/2, (float)(this.y - this.height - 3 - 1), 0);
        Minecraft.getInstance().font.draw(pPoseStack, s, this.x + this.width/2, (float)this.y - this.height - 3, 8453920);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    }
}
