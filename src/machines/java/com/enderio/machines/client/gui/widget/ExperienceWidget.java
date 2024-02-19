package com.enderio.machines.client.gui.widget;

import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.client.gui.widgets.EIOWidget;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ExperienceWidget extends EIOWidget {
    protected static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = new ResourceLocation("hud/experience_bar_background");
    protected static final ResourceLocation EXPERIENCE_BAR_PROGRESS_SPRITE = new ResourceLocation("hud/experience_bar_progress");
    private final Screen screen;
    private final Supplier<MachineFluidTank> getFluid;

    public ExperienceWidget(Screen screen, Supplier<MachineFluidTank> getFluid, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.screen = screen;
        this.getFluid = getFluid;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        ExperienceUtil.ExperienceLevel expLevel = ExperienceUtil.getLevelFromFluidWithLeftover(getFluid.get().getFluidAmount());
        int fill = (int) ((((float) expLevel.experience()) / ExperienceUtil.getXpNeededForNextLevel(expLevel.level())) * this.width) - 1;

        guiGraphics.blitSprite(EXPERIENCE_BAR_BACKGROUND_SPRITE, this.x, this.y, this.width, this.height);
        guiGraphics.blitSprite(EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, this.x, this.y, fill, 5);

        var font = Minecraft.getInstance().font;
        String text = "" + expLevel.level();
        int xOffset = font.width(text) / 2 ;
        guiGraphics.drawString(font, text, (this.x + this.width/2 + 1) - xOffset, (float)this.y - this.height - 3, 0, false);
        guiGraphics.drawString(font, text, (this.x + this.width/2 - 1) - xOffset, (float)this.y - this.height - 3, 0, false);
        guiGraphics.drawString(font, text, this.x + this.width/2 - xOffset, (float)(this.y - this.height - 3 + 1), 0, false);
        guiGraphics.drawString(font, text, this.x + this.width/2 - xOffset, (float)(this.y - this.height - 3 - 1), 0, false);
        guiGraphics.drawString(font, text, this.x + this.width/2 - xOffset, (float)this.y - this.height - 3, 8453920, false);

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }
}
