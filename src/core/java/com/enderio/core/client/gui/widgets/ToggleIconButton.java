package com.enderio.core.client.gui.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ToggleIconButton extends EnderButton {

    private final Function<Boolean, ResourceLocation> spriteFunction;
    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;

    @Nullable
    private final Function<Boolean, Component> tooltipFunction;

    public ToggleIconButton(int x, int y, int width, int height, Function<Boolean, ResourceLocation> spriteFunction,
        @Nullable Function<Boolean, Component> tooltipFunction, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(x, y, width, height, Component.empty());
        this.spriteFunction = spriteFunction;
        this.tooltipFunction = tooltipFunction;
        this.getter = getter;
        this.setter = setter;

        if (tooltipFunction != null) {
            setTooltip(Tooltip.create(tooltipFunction.apply(getter.get())));
        }
    }

    // region Presets and helpers

    private static final ResourceLocation CHECKMARK = ResourceLocation.fromNamespaceAndPath("enderio", "icon/checkmark");

    public static ToggleIconButton createCheckbox(int x, int y, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new ToggleIconButton(x, y, 16, 16, isChecked -> isChecked ? CHECKMARK : null, null, getter, setter);
    }

    public static ToggleIconButton of(int x, int y, int width, int height, ResourceLocation checked, ResourceLocation unchecked,
        Component checkedTooltip, Component uncheckedTooltip, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new ToggleIconButton(x, y, width, height, isChecked -> isChecked ? checked : unchecked, isChecked -> isChecked ? checkedTooltip : uncheckedTooltip,
            getter, setter);
    }

    // endregion

    @Override
    public void onPress() {
        boolean newValue = !getter.get();
        setter.accept(newValue);
        if (tooltipFunction != null) {
            setTooltip(Tooltip.create(tooltipFunction.apply(newValue)));
        }
    }

    @Override
    public void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        ResourceLocation sprite = spriteFunction.apply(getter.get());
        if (sprite != null) {
            guiGraphics.blitSprite(sprite, getX(), getY(), width, height);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
