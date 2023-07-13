package com.enderio.core.client.gui.widgets;

import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.IEnderScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToggleImageButton<U extends Screen & IEnderScreen> extends AbstractWidget {

    private final U addedOn;

    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int xDiffTex;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;
    private final Supplier<Component> tooltip;

    public ToggleImageButton(U addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex,
        ResourceLocation resourceLocation, Supplier<Boolean> getter, Consumer<Boolean> setter, Supplier<Component> tooltip) {
        this(addedOn, x, y, width, height, xTexStart, yTexStart, xDiffTex, yDiffTex, resourceLocation, 256, 256, getter, setter, tooltip);
    }

    public ToggleImageButton(U addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTex, int yDiffTex,
        ResourceLocation resourceLocation, int textureWidth, int textureHeight, Supplier<Boolean> getter, Consumer<Boolean> setter,
        Supplier<Component> tooltip) {
        super(x, y, width, height, Component.empty());
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.xDiffTex = xDiffTex;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = resourceLocation;
        this.addedOn = addedOn;
        this.getter = getter;
        this.setter = setter;
        this.tooltip = tooltip;
    }

    @Nullable
    private Component tooltipCache;

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float partialTick) {
        Vector2i pos = new Vector2i(getX(), getY());
        addedOn.renderSimpleArea(guiGraphics, pos, pos.add(new Vector2i(width, height)));

        // Coordinates based on whether toggledOn or not
        int xTex = this.xTexStart;
        int yTex = this.yTexStart;

        if (getter.get()) {
            xTex += xDiffTex;
            yTex += yDiffTex;
        }

        RenderSystem.enableDepthTest();
        guiGraphics.blit(this.resourceLocation, getX(), getY(), (float) xTex, (float) yTex, this.width, this.height, this.textureWidth, this.textureHeight);

        if (this.isHovered && tooltipCache != tooltip.get()) {

            tooltipCache = tooltip.get();

            setTooltip(Tooltip.create(this.tooltip.get().copy().withStyle(ChatFormatting.WHITE)));
        }
    }

    @Override
    protected ClientTooltipPositioner createTooltipPositioner() {
        return DefaultTooltipPositioner.INSTANCE;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        setter.accept(!getter.get());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

}
