package com.enderio.core.client.gui.widgets;

import com.enderio.api.misc.Icon;
import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.FullScreenListener;
import com.enderio.core.client.gui.screen.EnderScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumIconWidget<T extends Enum<T> & Icon, U extends Screen & EnderScreen> extends AbstractWidget implements FullScreenListener {

    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private final Map<T, SelectionWidget> icons = new HashMap<>();

    private final Vector2i expandTopLeft;
    private final Vector2i expandBottomRight;

    private static final int ELEMENTS_IN_ROW = 5;
    private static final int SPACE_BETWEEN_ELEMENTS = 4;

    private boolean isExpanded = false;

    private boolean expandNext = false;

    private int mouseButton = 0;

    private final U screen;

    private final SelectionScreen selection;

    // TODO: I don't like that this is separate, maybe we need an IOptionIcon for holding the option name?
    private final Component optionName;

    public EnumIconWidget(U screen, int pX, int pY, Supplier<T> getter, Consumer<T> setter, Component optionName) {
        super(pX, pY, getter.get().getRenderSize().x(), getter.get().getRenderSize().y(), Component.empty());
        this.getter = getter;
        this.setter = setter;
        this.optionName = optionName;
        T[] values = getter.get().getDeclaringClass().getEnumConstants();
        Vector2i pos = calculateFirstPosition(values[0], values.length);
        Vector2i elementDistance = values[0].getRenderSize().expand(SPACE_BETWEEN_ELEMENTS);
        for (int i = 0; i < values.length; i++) {
            T value = values[i];
            Vector2i subWidgetPos = pos.add(getColumn(i) * elementDistance.x(), getRow(i) * elementDistance.y()).add(pX, pY);
            SelectionWidget widget = new SelectionWidget(subWidgetPos, value);
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
        this.screen = screen;
        this.selection = new SelectionScreen();
    }

    private Vector2i calculateFirstPosition(T icon, int amount) {
        int maxColumns = Math.min(amount, ELEMENTS_IN_ROW);
        int width = (maxColumns - 1) * (icon.getRenderSize().x() + SPACE_BETWEEN_ELEMENTS);
        return new Vector2i(-width / 2, 2 * SPACE_BETWEEN_ELEMENTS + icon.getRenderSize().y());
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
        if (isExpanded) {
            selectNext(mouseButton != InputConstants.MOUSE_BUTTON_RIGHT);
        } else {
            isExpanded = true;
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
        if (expandNext && Minecraft.getInstance().screen == screen) {
            Minecraft.getInstance().pushGuiLayer(selection);
            expandNext = false;
            isExpanded = true;
        }
        T icon = getter.get();
        screen.renderIconBackground(guiGraphics, new Vector2i(getX(), getY()), icon);
        EnderScreen.renderIcon(guiGraphics, new Vector2i(getX(), getY()), icon);

        if (isHovered() && tooltipDisplayCache != getter.get()) {
            // Cache the last value of the tooltip so we don't append strings over and over.
            tooltipDisplayCache = getter.get();

            // Update tooltip
            setTooltip(Tooltip.create(optionName.copy().append("\n").append(getter.get().getTooltip().copy().withStyle(ChatFormatting.GRAY))));
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

    @Override
    public void onGlobalClick(double mouseX, double mouseY) {
        if (isExpanded && !(expandTopLeft.x() <= mouseX && expandBottomRight.x() >= mouseX && expandTopLeft.y() <= mouseY && expandBottomRight.y() >= mouseY
            || isMouseOver(mouseX, mouseY))) {
            isExpanded = false;
            Minecraft.getInstance().popGuiLayer();
        }
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        expandNext = expanded;
        isExpanded = expanded;
    }

    public Component getOptionName() {
        return optionName;
    }

    private class SelectionScreen extends Screen implements EnderScreen {

        protected SelectionScreen() {
            super(Component.empty());
        }

        @Override
        protected void init() {
            addWidget(EnumIconWidget.this);
            EnumIconWidget.this.icons.values().forEach(this::addRenderableWidget);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
            RenderSystem.disableDepthTest();
            renderSimpleArea(guiGraphics, expandTopLeft, expandBottomRight);
            super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

            RenderSystem.enableDepthTest();
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            for (GuiEventListener widget : children()) {
                if (widget instanceof AbstractWidget abstractWidget && abstractWidget.isActive() && widget instanceof FullScreenListener fullScreenListener) {
                    fullScreenListener.onGlobalClick(pMouseX, pMouseY);
                }
            }
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public void setTooltipForNextRenderPass(Component pTooltip) {
            super.setTooltipForNextRenderPass(pTooltip);
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }

        @Override
        public void renderTransparentBackground(GuiGraphics pGuiGraphics) {} //Don't make background dark

        @Override
        public void onClose() {
            EnumIconWidget.this.setFocused(false);
            EnumIconWidget.this.isExpanded = false;
            super.onClose();
        }

        @Override
        public void resize(Minecraft minecraft, int width, int height) {
            minecraft.popGuiLayer();
        }
    }

    private class SelectionWidget extends AbstractWidget {

        private final T value;

        SelectionWidget(Vector2i pos, T value) {
            super(pos.x(), pos.y(), value.getRenderSize().x() + 2, value.getRenderSize().y() + 2, value.getTooltip());
            this.value = value;
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
                selection.renderIconBackground(guiGraphics, new Vector2i(getX(), getY()), value);
            } else {
                guiGraphics.fill(getX(), getY(),getX() + width - 2,getY() + height - 2, 0xFF0020FF);
                guiGraphics.fill(getX() +1, getY()+1, getX() + width - 3, getY() + height - 3, 0xFF8B8B8B);
            }
            EnderScreen.renderIcon(guiGraphics, new Vector2i(getX(), getY()), value);

            if (isMouseOver(pMouseX, pMouseY)) {
                Component tooltip = value.getTooltip();
                if (tooltip != null && !Component.empty().equals(tooltip)) {
                    selection.setTooltipForNextRenderPass(tooltip);
                }
            }
        }
    }
}
