package com.enderio.core.client.gui.widgets;

import com.enderio.core.EnderCore;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ToggleIconButton extends EnderButton {

    private final ResourceLocation texture;
    private final int texU;
    private final int texV;
    private final int texW;
    private final int texH;

    private final int toggledTexUOffset;
    private final int toggledTexVOffset;

    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;

    @Nullable
    private final Function<Boolean, Component> tooltipFunction;

    public ToggleIconButton(int x, int y, int width, int height, ResourceLocation texture, int texU, int texV, int texW, int texH,
            int toggledTexUOffset, int toggledTexVOffset, @Nullable Function<Boolean, Component> tooltipFunction, Supplier<Boolean> getter,
            Consumer<Boolean> setter) {
        super(x, y, width, height, Component.empty());
        this.texture = texture;
        this.texU = texU;
        this.texV = texV;
        this.texW = texW;
        this.texH = texH;

        this.toggledTexUOffset = toggledTexUOffset;
        this.toggledTexVOffset = toggledTexVOffset;

        this.tooltipFunction = tooltipFunction;
        this.getter = getter;
        this.setter = setter;

        if (tooltipFunction != null) {
            setTooltip(Tooltip.create(tooltipFunction.apply(getter.get())));
        }
    }

    // region Presets and helpers

    private static final ResourceLocation CHECKMARK = new ResourceLocation(EnderCore.MOD_ID,
            "icon/checkmark");

    public static ToggleIconButton createCheckbox(int x, int y, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new ToggleIconButton(x, y, 16, 16, CHECKMARK, 0, 0, 16, 16, 16, 0, null, getter, setter);
    }

    public static ToggleIconButton of(int x, int y, int width, int height, ResourceLocation texture,
            int toggledTexUOffset, int toggledTexVOffset, Component checkedTooltip, Component uncheckedTooltip, Supplier<Boolean> getter,
            Consumer<Boolean> setter) {
        return new ToggleIconButton(x, y, width, height, texture, 0, 0, width, height, toggledTexUOffset, toggledTexVOffset,
            isChecked -> isChecked ? checkedTooltip : uncheckedTooltip, getter, setter);
    }

    // endregion

    @Override
    public void onPress() {
        boolean newValue = !getter.get();
        setter.accept(newValue);
        if (tooltipFunction != null) {
            setTooltip(Tooltip.create(tooltipFunction.apply(getter.get())));
        }
    }

    private boolean previousValue;

    @Override
    public void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        boolean value = getter.get();

        // Coordinates based on whether toggledOn or not
        int xTex = texU;
        int yTex = texV;

        if (value) {
            xTex += toggledTexUOffset;
            yTex += toggledTexVOffset;
        }

        guiGraphics.blit(texture, getX(), getY(), (float) xTex, (float) yTex, this.width, this.height, texW, texH);

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
