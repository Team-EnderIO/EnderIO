package com.enderio.core.client.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ToggleButton extends AbstractWidget {

    private final Function<Boolean, ResourceLocation> spriteFunction;
    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;

    public ToggleButton(int x, int y, int width, int height, Function<Boolean, ResourceLocation> spriteFunction, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(x, y, width, height, Component.empty());
        this.spriteFunction = spriteFunction;
        this.getter = getter;
        this.setter = setter;
    }

    // region Presets

    // TODO: Looks bad, leaving current version for now but I would like to iterate on this.
    private static final ResourceLocation CHECKMARK = ResourceLocation.fromNamespaceAndPath("enderio", "icons/misc/checkmark");

    public static ToggleButton createCheckbox(int x, int y, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new ToggleButton(x, y, 16, 16, checked -> checked ? CHECKMARK : null, getter, setter);
    }

    // endregion

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        GuiRenderUtil.renderSlotArea(guiGraphics, getX(), getY(), width, height);

        ResourceLocation sprite = spriteFunction.apply(getter.get());
        if (sprite != null) {
            guiGraphics.blitSprite(sprite, getX(), getY(), width, height);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        setter.accept(!getter.get());
    }
}
