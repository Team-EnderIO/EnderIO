package com.enderio.enderio.client.foundation.widgets;

import com.enderio.core.client.gui.widgets.EIOWidget;
import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;

import java.util.function.Supplier;

public class ExperienceCraftingWidget extends EIOWidget {
    protected static final Identifier EXPERIENCE_BAR_BACKGROUND_SPRITE = Identifier
            .withDefaultNamespace("hud/experience_bar_background");
    protected static final Identifier EXPERIENCE_BAR_PROGRESS_SPRITE = Identifier
            .withDefaultNamespace("hud/experience_bar_progress");
    private final Supplier<FluidStorageInfo> fluidStorageSupplier;
    private final Supplier<Integer> maxXP;

    public ExperienceCraftingWidget(int x, int y, int width, int height,
            Supplier<FluidStorageInfo> fluidStorageSupplier, Supplier<Integer> maxXP) {
        super(x, y, width, height);
        this.fluidStorageSupplier = fluidStorageSupplier;
        this.maxXP = maxXP;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        //TODO blend depth pipeline

        int k = 1;
        if (maxXP.get() > 0) {
            k = (int) (((fluidStorageSupplier.get().contents().getAmount()
                    / ((float) ExperienceUtil.getFluidFromLevel(maxXP.get()))) * this.width) - 1);
            if (k > this.width - 1) {
                k = this.width - 1;
            }
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_BACKGROUND_SPRITE, this.x, this.y, this.width, this.height);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, this.x, this.y, k, 5);

        var font = Minecraft.getInstance().font;
        String s = "" + maxXP.get();
        graphics.text(font, s, (int) (this.x + this.width / 2f + 1), this.y - this.height - 3, CommonColors.BLACK, false);
        graphics.text(font, s, (int) (this.x + this.width / 2f - 1), (int) this.y - this.height - 3, CommonColors.BLACK, false);
        graphics.text(font, s, (int) (this.x + this.width / 2f), (this.y - this.height - 3 + 1), CommonColors.BLACK, false);
        graphics.text(font, s, (int) (this.x + this.width / 2f), (this.y - this.height - 3 - 1), CommonColors.BLACK, false);
        graphics.text(font, s, (int) (this.x + this.width / 2f),  this.y - this.height - 3, 0xFF80FF20, false);

        if (isHovered(mouseX, mouseY)) {
            Minecraft minecraft = Minecraft.getInstance();
            graphics
                    .setTooltipForNextFrame(
                            minecraft.font, Component.literal(fluidStorageSupplier.get().contents().getAmount()
                                    + " mb / " + ExperienceUtil.getFluidFromLevel(maxXP.get()) + " mb"),
                            mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
