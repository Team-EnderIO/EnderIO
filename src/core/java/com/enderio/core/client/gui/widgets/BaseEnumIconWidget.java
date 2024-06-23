package com.enderio.core.client.gui.widgets;

import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.EnderScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BaseEnumIconWidget<T extends Enum<T>> extends AbstractWidget {

    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private final Map<T, SelectionWidget> icons = new HashMap<>();

    private final Vector2i expandTopLeft;
    private final Vector2i expandBottomRight;

    private static final int ELEMENTS_IN_ROW = 5;
    private static final int SPACE_BETWEEN_ELEMENTS = 4;

    private int mouseButton = 0;

    private final SelectionScreen selection;

    // TODO: I don't like that this is separate, maybe we need an IOptionIcon for holding the option name?
    private final Component optionName;

    public BaseEnumIconWidget(int pX, int pY, int width, int height, Supplier<T> getter, Consumer<T> setter, Component optionName) {
        super(pX, pY, width, height, Component.empty());
        this.getter = getter;
        this.setter = setter;
        this.optionName = optionName;

        T[] values = getter.get().getDeclaringClass().getEnumConstants();
        Vector2i pos = calculateFirstPosition(values[0], values.length);
        Vector2i elementDistance = new Vector2i(width, height).expand(SPACE_BETWEEN_ELEMENTS);
        for (int i = 0; i < values.length; i++) {
            T value = values[i];
            Vector2i subWidgetPos = pos.add(getColumn(i) * elementDistance.x(), getRow(i) * elementDistance.y()).add(pX, pY);
            SelectionWidget widget = new SelectionWidget(subWidgetPos, width + 2, height + 2, value);
            icons.put(value, widget);
        }

        Vector2i topLeft = Vector2i.MAX;
        Vector2i bottomRight = Vector2i.MIN;
        for (SelectionWidget widget : icons.values()) {
            topLeft = topLeft.withX(Math.min(topLeft.x(), widget.getX()));
            topLeft = topLeft.withY(Math.min(topLeft.y(), widget.getY()));
            bottomRight = bottomRight.withX(Math.max(bottomRight.x(), widget.getX() + widget.getWidth()));
            bottomRight = bottomRight.withY(Math.max(bottomRight.y(), widget.getY() + widget.getHeight()));
        }

        expandTopLeft = topLeft.expand(-SPACE_BETWEEN_ELEMENTS);
        expandBottomRight = bottomRight.expand(SPACE_BETWEEN_ELEMENTS);
        this.selection = new SelectionScreen(this);
    }

    public abstract Component getValueTooltip(T value);
    public abstract ResourceLocation getValueIcon(T value);

    private Vector2i calculateFirstPosition(T icon, int amount) {
        int maxColumns = Math.min(amount, ELEMENTS_IN_ROW);
        int width = (maxColumns - 1) * (getWidth() + SPACE_BETWEEN_ELEMENTS);
        return new Vector2i(-width / 2, 2 * SPACE_BETWEEN_ELEMENTS + getHeight());
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        mouseButton = pButton;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected boolean isValidClickButton(int pButton) {
        return pButton == InputConstants.MOUSE_BUTTON_LEFT || pButton == InputConstants.MOUSE_BUTTON_RIGHT;
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        if (isExpanded()) {
            selectNext(mouseButton != InputConstants.MOUSE_BUTTON_RIGHT);
        } else {
            Minecraft.getInstance().pushGuiLayer(selection);
        }
    }

    private void selectNext(boolean isForward) {
        T[] values = getter.get().getDeclaringClass().getEnumConstants();
        int index = getter.get().ordinal() + (isForward ? 1 : -1) + values.length;
        setter.accept(values[index % values.length]);
    }

    private static int getColumn(int index) {
        return index % ELEMENTS_IN_ROW;
    }

    private static int getRow(int index) {
        return index / ELEMENTS_IN_ROW;
    }

    @Nullable private T tooltipDisplayCache;

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        T value = getter.get();

        GuiRenderUtil.renderSlotArea(guiGraphics, getX(), getY(), getWidth(), getHeight());
        guiGraphics.blitSprite(getValueIcon(value), getX(), getY(), getWidth(), getHeight());

        if (isHovered() && tooltipDisplayCache != getter.get()) {
            // Cache the last value of the tooltip so we don't append strings over and over.
            tooltipDisplayCache = getter.get();

            // Update tooltip
            setTooltip(Tooltip.create(optionName.copy().append("\n").append(getValueTooltip(value).copy().withStyle(ChatFormatting.GRAY))));
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

    public boolean isExpanded() {
        return Minecraft.getInstance().screen instanceof SelectionScreen;
    }

    private static class SelectionScreen extends Screen implements EnderScreen {

        private final BaseEnumIconWidget<?> parentWidget;

        protected SelectionScreen(BaseEnumIconWidget<?> parentWidget) {
            super(Component.empty());
            this.parentWidget = parentWidget;
        }

        @Override
        protected void init() {
            addWidget(parentWidget);
            parentWidget.icons.values().forEach(this::addRenderableWidget);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
            // TODO: is the depth test disable required?
            RenderSystem.disableDepthTest();
            super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
            RenderSystem.enableDepthTest();
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            renderSimpleArea(guiGraphics, parentWidget.expandTopLeft, parentWidget.expandBottomRight);
        }

        @Override
        public void renderTransparentBackground(GuiGraphics pGuiGraphics) {
            //Don't make background dark
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!(parentWidget.expandTopLeft.x() <= mouseX && parentWidget.expandBottomRight.x() >= mouseX && parentWidget.expandTopLeft.y() <= mouseY && parentWidget.expandBottomRight.y() >= mouseY
                || parentWidget.isMouseOver(mouseX, mouseY))) {
                Minecraft.getInstance().popGuiLayer();
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }

        @Override
        public void resize(Minecraft minecraft, int width, int height) {
            minecraft.popGuiLayer();
            minecraft.screen.resize(minecraft, width, height);
        }
    }

    private class SelectionWidget extends AbstractWidget {

        private final T value;
        private final int iconWidth;
        private final int iconHeight;

        SelectionWidget(Vector2i pos, int width, int height, T value) {
            super(pos.x(), pos.y(), width + 2, height + 2, getValueTooltip(value));
            this.value = value;
            this.iconWidth = width;
            this.iconHeight = height;
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            super.onClick(pMouseX, pMouseY);
            setter.accept(value);
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
            if (getter.get() != value) {
                GuiRenderUtil.renderSlotArea(guiGraphics, getX(), getY(), iconWidth, iconHeight);
            } else {
                guiGraphics.fill(getX(), getY(),getX() + width - 2,getY() + height - 2, 0xFF0020FF);
                guiGraphics.fill(getX() +1, getY()+1, getX() + width - 3, getY() + height - 3, 0xFF8B8B8B);
            }

            guiGraphics.blitSprite(getValueIcon(value), getX(), getY(), iconWidth, iconHeight);

            if (isMouseOver(pMouseX, pMouseY)) {
                Component tooltip = getValueTooltip(value);
                if (tooltip != null && !Component.empty().equals(tooltip)) {
                    selection.setTooltipForNextRenderPass(tooltip);
                }
            }
        }
    }
}
