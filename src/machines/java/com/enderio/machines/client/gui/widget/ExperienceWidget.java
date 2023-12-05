package com.enderio.machines.client.gui.widget;

import com.enderio.api.misc.Vector2i;
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
    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    private final Screen displayOn;
    private final Supplier<MachineFluidTank> getFluid;

    public ExperienceWidget(Screen displayOn, Supplier<MachineFluidTank> getFluid, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.displayOn = displayOn;
        this.getFluid = getFluid;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        Vector2i level = ExperienceUtil.getLevelFromFluidWithLeftover(getFluid.get().getFluidAmount());
        int fill = (int) ((((float) level.y()) / ExperienceUtil.getXpNeededForNextLevel(level.x())) * this.width) - 1;

        guiGraphics.blit(GUI_ICONS_LOCATION, this.x, this.y, 0, 0, 64, this.width-1, this.height, 256, 256);
        guiGraphics.blit(GUI_ICONS_LOCATION, this.x + this.width-1, this.y, 0, 181, 64, 1, this.height, 256, 256);
        guiGraphics.blit(GUI_ICONS_LOCATION, this.x, this.y, 0, 0, 69, fill, this.height, 256, 256);
        guiGraphics.blit(GUI_ICONS_LOCATION, this.x + this.width-1, this.y, 0, 181, 64, fill==this.width-1? 1 : 0, this.height, 256, 256);

        var font = Minecraft.getInstance().font;
        String text = "" + level.x();
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
