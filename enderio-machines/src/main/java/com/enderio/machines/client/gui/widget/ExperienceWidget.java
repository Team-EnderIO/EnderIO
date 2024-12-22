package com.enderio.machines.client.gui.widget;

import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.client.gui.widgets.EIOWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

public class ExperienceWidget extends EIOWidget {
    protected static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = ResourceLocation
            .withDefaultNamespace("hud/experience_bar_background");
    protected static final ResourceLocation EXPERIENCE_BAR_PROGRESS_SPRITE = ResourceLocation
            .withDefaultNamespace("hud/experience_bar_progress");

    private final Supplier<FluidStack> getFluid;

    public ExperienceWidget(int x, int y, int width, int height, Supplier<FluidStack> getFluid) {
        super(x, y, width, height);
        this.getFluid = getFluid;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        ExperienceUtil.ExperienceLevel expLevel = ExperienceUtil
                .getLevelFromFluidWithLeftover(getFluid.get().getAmount());
        int fill = (int) ((((float) expLevel.experience()) / ExperienceUtil.getXpNeededForNextLevel(expLevel.level()))
                * this.width) - 1;

        guiGraphics.blitSprite(EXPERIENCE_BAR_BACKGROUND_SPRITE, this.x, this.y, this.width, this.height);
        guiGraphics.blitSprite(EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, this.x, this.y, fill, 5);

        var font = Minecraft.getInstance().font;
        String text = "" + expLevel.level();
        int xOffset = font.width(text) / 2;
        guiGraphics.drawString(font, text, (this.x + this.width / 2f + 1) - xOffset, (float) this.y - this.height - 3,
                0, false);
        guiGraphics.drawString(font, text, (this.x + this.width / 2f - 1) - xOffset, (float) this.y - this.height - 3,
                0, false);
        guiGraphics.drawString(font, text, this.x + this.width / 2f - xOffset, (float) (this.y - this.height - 3 + 1),
                0, false);
        guiGraphics.drawString(font, text, this.x + this.width / 2f - xOffset, (float) (this.y - this.height - 3 - 1),
                0, false);
        guiGraphics.drawString(font, text, this.x + this.width / 2f - xOffset, (float) this.y - this.height - 3,
                8453920, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }
}
