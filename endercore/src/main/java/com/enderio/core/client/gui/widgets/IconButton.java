package com.enderio.core.client.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class IconButton extends EnderButton {

    private final ResourceLocation sprite;
    private final Runnable onPress;

    public IconButton(int x, int y, int width, int height, ResourceLocation sprite, @Nullable Component tooltip,
            Runnable onPress) {
        super(x, y, width, height, Component.empty());
        this.sprite = sprite;
        this.onPress = onPress;

        if (tooltip != null) {
            setTooltip(Tooltip.create(tooltip));
        }
    }

    @Override
    public void onPress() {
        this.onPress.run();
    }

    @Override
    public void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blitSprite(sprite, getX(), getY(), width, height);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
