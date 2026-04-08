package com.enderio.enderio.client.foundation.widgets;

import com.enderio.core.client.gui.widgets.EIOWidget;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Supplier;

public class ExperienceWidget extends EIOWidget {
    protected static final Identifier EXPERIENCE_BAR_BACKGROUND_SPRITE = Identifier
            .withDefaultNamespace("hud/experience_bar_background");
    protected static final Identifier EXPERIENCE_BAR_PROGRESS_SPRITE = Identifier
            .withDefaultNamespace("hud/experience_bar_progress");

    private final Supplier<FluidStack> getFluid;

    public ExperienceWidget(int x, int y, int width, int height, Supplier<FluidStack> getFluid) {
        super(x, y, width, height);
        this.getFluid = getFluid;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        ExperienceUtil.ExperienceLevel expLevel = ExperienceUtil
                .getLevelFromFluidWithLeftover(getFluid.get().getAmount());
        int fill = (int) ((((float) expLevel.experience()) / ExperienceUtil.getXpNeededForNextLevel(expLevel.level()))
                * this.width) - 1;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_BACKGROUND_SPRITE, this.x, this.y, this.width, this.height);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, this.x, this.y, fill, 5);

        var font = Minecraft.getInstance().font;
        String text = "" + expLevel.level();
        int xOffset = font.width(text) / 2;
        graphics.text(font, text, (int) ((this.x + this.width / 2f + 1) - xOffset), this.y - this.height - 3,
            CommonColors.BLACK, false);
        graphics.text(font, text, (int) ((this.x + this.width / 2f - 1) - xOffset), this.y - this.height - 3,
            CommonColors.BLACK, false);
        graphics.text(font, text, (int) (this.x + this.width / 2f - xOffset), (this.y - this.height - 3 + 1),
            CommonColors.BLACK, false);
        graphics.text(font, text, (int) (this.x + this.width / 2f - xOffset), (this.y - this.height - 3 - 1),
            CommonColors.BLACK, false);
        graphics.text(font, text, (int) (this.x + this.width / 2f - xOffset), this.y - this.height - 3,
            0xFF80FF20, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
