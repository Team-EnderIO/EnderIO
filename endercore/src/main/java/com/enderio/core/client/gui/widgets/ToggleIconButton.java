package com.enderio.core.client.gui.widgets;

import com.enderio.core.EnderCore;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ToggleIconButton extends EnderButton {

    private final Function<Boolean, Identifier> spriteFunction;
    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;

    @Nullable
    private final Function<Boolean, Component> tooltipFunction;

    public ToggleIconButton(int x, int y, int width, int height, Function<Boolean, Identifier> spriteFunction,
            @Nullable Function<Boolean, Component> tooltipFunction, Supplier<Boolean> getter,
            Consumer<Boolean> setter) {
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

    private static final Identifier CHECKMARK = Identifier.fromNamespaceAndPath(EnderCore.MOD_ID,
            "icon/checkmark");

    public static ToggleIconButton createCheckbox(int x, int y, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new ToggleIconButton(x, y, 16, 16, isChecked -> isChecked ? CHECKMARK : null, null, getter, setter);
    }

    public static ToggleIconButton of(int x, int y, int width, int height, Identifier checked,
            Identifier unchecked, Component checkedTooltip, Component uncheckedTooltip, Supplier<Boolean> getter,
            Consumer<Boolean> setter) {
        return new ToggleIconButton(x, y, width, height, isChecked -> isChecked ? checked : unchecked,
                isChecked -> isChecked ? checkedTooltip : uncheckedTooltip, getter, setter);
    }

    // endregion

    @Override
    public void onPress(InputWithModifiers input) {
        boolean newValue = !getter.get();
        setter.accept(newValue);
        if (tooltipFunction != null) {
            setTooltip(Tooltip.create(tooltipFunction.apply(getter.get())));
        }
    }

    private boolean previousValue;

    @Override
    public void renderButtonFace(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        boolean value = getter.get();
        Identifier sprite = spriteFunction.apply(getter.get());
        if (sprite != null) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), width, height);
        }

        // TODO: Temp solution for the value changing externally (data sync)
        if (tooltipFunction != null && previousValue != value) {
            previousValue = value;
            setTooltip(Tooltip.create(tooltipFunction.apply(getter.get())));
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
