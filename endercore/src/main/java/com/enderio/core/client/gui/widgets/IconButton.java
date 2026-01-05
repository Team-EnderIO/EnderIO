package com.enderio.core.client.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class IconButton extends EnderButton {

    private final Identifier sprite;
    private final Consumer<InputWithModifiers> onPress;

    public IconButton(int x, int y, int width, int height, Identifier sprite, @Nullable Component tooltip,
        Consumer<InputWithModifiers> onPress) {
        super(x, y, width, height, Component.empty());
        this.sprite = sprite;
        this.onPress = onPress;

        if (tooltip != null) {
            setTooltip(Tooltip.create(tooltip));
        }
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.onPress.accept(input);
    }

    @Override
    public void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), width, height);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
