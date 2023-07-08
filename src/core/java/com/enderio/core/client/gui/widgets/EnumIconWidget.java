package com.enderio.core.client.gui.widgets;

import com.enderio.api.misc.IIcon;
import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.IEnderScreen;
import com.enderio.core.client.gui.screen.IFullScreenListener;
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
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumIconWidget<T extends Enum<T> & IIcon, U extends Screen & IEnderScreen> extends AbstractWidget implements IFullScreenListener {

    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private final Map<T, SelectionWidget> icons = new HashMap<>();

    private final Vector2i expandTopLeft, expandBottomRight;

    private static final int ELEMENTS_IN_ROW = 5;
    private static final int SPACE_BETWEEN_ELEMENTS = 4;

    private boolean isExpanded = false;

    private int mouseButton = 0;

    private final U addedOn;

    private final SelectionScreen selection;

    // TODO: I don't like that this is separate, maybe we need an IOptionIcon for holding the option name?
    private final Component optionName;

    public EnumIconWidget(U addedOn, int pX, int pY, Supplier<T> getter, Consumer<T> setter, Component optionName) {
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
        this.addedOn = addedOn;
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
        if (isHovered && isActive()) {
            // @formatter:off
           addedOn.renderTooltipAfterEverything(guiGraphics, List.of(optionName, getter.get().getTooltip().copy().withStyle(ChatFormatting.GRAY)), pMouseX, pMouseY);
            // @formatter:on
        }

        T icon = getter.get();
        addedOn.renderIconBackground(guiGraphics, new Vector2i(getX(), getY()), icon);
        IEnderScreen.renderIcon(guiGraphics, new Vector2i(getX(), getY()).expand(1), icon);

        if (isHoveredOrFocused() && tooltipDisplayCache != getter.get()) {
            // Cache the last value of the tooltip so we don't append strings over and over.
            tooltipDisplayCache = getter.get();

            // Update tooltip
            setTooltip(Tooltip.create(optionName.copy().append("\n").append(getter.get().getTooltip().copy().withStyle(ChatFormatting.GRAY))));
        }
    }

    @Override
    protected ClientTooltipPositioner createTooltipPositioner() {
        return DefaultTooltipPositioner.INSTANCE;
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

    private class SelectionScreen extends Screen implements IEnderScreen {

        private final List<LateTooltipData> tooltips = new ArrayList<>();

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
            tooltips.clear();
            renderSimpleArea(guiGraphics, expandTopLeft, expandBottomRight);
            super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

            for (LateTooltipData tooltip : tooltips) {
                guiGraphics.renderTooltip(this.font, tooltip.getText(), Optional.empty(), tooltip.getMouseX(), tooltip.getMouseY());
            }
            RenderSystem.enableDepthTest();
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            for (GuiEventListener widget : children()) {
                if (widget instanceof AbstractWidget abstractWidget && abstractWidget.isActive() && widget instanceof IFullScreenListener fullScreenListener) {
                    fullScreenListener.onGlobalClick(pMouseX, pMouseY);
                }
            }
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }

        @Override
        public void addTooltip(LateTooltipData data) {
            tooltips.add(data);
        }

        @Override
        public void onClose() {
            EnumIconWidget.this.setFocused(false);
            EnumIconWidget.this.isExpanded = false;
            super.onClose();
        }
    }

    private class SelectionWidget extends AbstractWidget {

        private final T value;

        public SelectionWidget(Vector2i pos, T value) {
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
                guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF0020FF);
                guiGraphics.fill(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1, 0xFF8B8B8B);
            }
            IEnderScreen.renderIcon(guiGraphics, new Vector2i(getX(), getY()).expand(1), value);

            if (isMouseOver(pMouseX, pMouseY)) {
                Component tooltip = value.getTooltip();
                if (tooltip != null && !Component.empty().equals(tooltip)) {
                    selection.renderTooltipAfterEverything(guiGraphics, List.of(tooltip), pMouseX, pMouseY);
                }
            }
        }
    }
}
