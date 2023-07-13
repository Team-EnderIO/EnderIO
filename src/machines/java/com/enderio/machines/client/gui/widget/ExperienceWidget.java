package com.enderio.machines.client.gui.widget;

import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.client.gui.widgets.EIOWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Supplier;

public class ExperienceWidget extends EIOWidget {
    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
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
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int k = 1;
        if (maxXP.get() > 0) {
            k = (int) (((getFluid.get().getFluidAmount() / ((float) ExperienceUtil.getFluidFromLevel(maxXP.get()))) * this.width)-1);
            if (k > this.width-1) {
                k = this.width-1;
            }
        }
        guiGraphics.blit(GUI_ICONS_LOCATION, this.x, this.y, 0, 0, 64, this.width-1, this.height, 256, 256);
        guiGraphics.blit(GUI_ICONS_LOCATION, this.x + this.width-1, this.y, 0, 181, 64, 1, this.height, 256, 256);
        guiGraphics.blit(GUI_ICONS_LOCATION, this.x, this.y, 0, 0, 69, k, this.height, 256, 256);
        guiGraphics.blit(GUI_ICONS_LOCATION, this.x + this.width-1, this.y, 0, 181, 64, k==this.width-1? 1 : 0, this.height, 256, 256);

        var font = Minecraft.getInstance().font;
        String s = "" + maxXP.get();
        guiGraphics.drawString(font, s, (this.x + this.width/2 + 1), (float)this.y - this.height - 3, 0, false);
        guiGraphics.drawString(font, s, (this.x + this.width/2 - 1), (float)this.y - this.height - 3, 0, false);
        guiGraphics.drawString(font, s, this.x + this.width/2, (float)(this.y - this.height - 3 + 1), 0, false);
        guiGraphics.drawString(font, s, this.x + this.width/2, (float)(this.y - this.height - 3 - 1), 0, false);
        guiGraphics.drawString(font, s, this.x + this.width/2, (float)this.y - this.height - 3, 8453920, false);

        if (isHovered(pMouseX, pMouseY)) {
            setTooltip(Tooltip.create(Component.literal(getFluid.get().getFluidAmount() + " mb / " + ExperienceUtil.getFluidFromLevel(maxXP.get()) + " mb")));
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }
}
