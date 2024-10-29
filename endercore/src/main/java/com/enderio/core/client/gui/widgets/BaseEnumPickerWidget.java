package com.enderio.core.client.gui.widgets;

import com.enderio.core.client.gui.screen.EnderScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

public abstract class BaseEnumPickerWidget<T extends Enum<T>> extends EnderButton {

    private final Class<T> clazz;
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private final Map<T, SelectionWidget> icons = new HashMap<>();

    private final Vector2i expandTopLeft;
    private final Vector2i expandBottomRight;

    private static final int ELEMENTS_IN_ROW = 5;
    private static final int SPACE_BETWEEN_ELEMENTS = 6;

    private int mouseButton = 0;

    private final SelectionScreen selection;

    private final Component optionName;

    public BaseEnumPickerWidget(int pX, int pY, int width, int height, Class<T> clazz, Supplier<T> getter,
            Consumer<T> setter, Component optionName) {
        super(pX, pY, width, height, Component.empty());

        this.clazz = clazz;
        this.getter = getter;
        this.setter = setter;
        this.optionName = optionName;

        T[] values = getValues();
        Vector2i pos = calculateFirstPosition(values[0], values.length);
        Vector2i elementDistance = new Vector2i(width, height).add(SPACE_BETWEEN_ELEMENTS, SPACE_BETWEEN_ELEMENTS);
        for (int i = 0; i < values.length; i++) {
            T value = values[i];
            Vector2i subWidgetPos = new Vector2i(pos.x() + getColumn(i) * elementDistance.x() + pX,
                    pos.y() + getRow(i) * elementDistance.y() + pY);
            SelectionWidget widget = new SelectionWidget(subWidgetPos, width + 2, height + 2, value);

            Component tooltip = getValueTooltip(value);
            if (tooltip != null) {
                widget.setTooltip(Tooltip.create(tooltip));
            }

            icons.put(value, widget);
        }

        Vector2i topLeft = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2i bottomRight = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (SelectionWidget widget : icons.values()) {
            topLeft = new Vector2i(Math.min(topLeft.x(), widget.getX()), topLeft.y());
            topLeft = new Vector2i(topLeft.x(), Math.min(topLeft.y(), widget.getY()));
            bottomRight = new Vector2i(Math.max(bottomRight.x(), widget.getX() + widget.getWidth()), bottomRight.y());
            bottomRight = new Vector2i(bottomRight.x(), Math.max(bottomRight.y(), widget.getY() + widget.getHeight()));
        }

        expandTopLeft = topLeft.sub(SPACE_BETWEEN_ELEMENTS, SPACE_BETWEEN_ELEMENTS);
        expandBottomRight = bottomRight.add(SPACE_BETWEEN_ELEMENTS, SPACE_BETWEEN_ELEMENTS);
        this.selection = new SelectionScreen(this);

        updateTooltip(getValue());
    }

    @Nullable
    public abstract Component getValueTooltip(T value);

    public abstract ResourceLocation getValueIcon(T value);

    public T[] getValues() {
        return clazz.getEnumConstants();
    }

    private T getValue() {
        return getter.get();
    }

    private void setValue(T value) {
        setter.accept(value);
        updateTooltip(value);
    }

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
    public void onPress() {
        if (isExpanded()) {
            selectNext(mouseButton != InputConstants.MOUSE_BUTTON_RIGHT);
        } else {
            Minecraft.getInstance().pushGuiLayer(selection);
        }
    }

    private void selectNext(boolean isForward) {
        T[] values = getValues();
        int index = getValue().ordinal() + (isForward ? 1 : -1) + values.length;
        setValue(values[index % values.length]);
    }

    private static int getColumn(int index) {
        return index % ELEMENTS_IN_ROW;
    }

    private static int getRow(int index) {
        return index / ELEMENTS_IN_ROW;
    }

    @Nullable
    private T tooltipDisplayCache;

    @Override
    public void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        T value = getValue();
        guiGraphics.blitSprite(getValueIcon(value), getX(), getY(), getWidth(), getHeight());
    }

    private void updateTooltip(T value) {
        // Update tooltip
        Component valueTooltip = getValueTooltip(value);

        Component tooltip;
        if (valueTooltip != null) {
            tooltip = optionName.copy().append("\n").append(valueTooltip.copy().withStyle(ChatFormatting.GRAY));
        } else {
            tooltip = optionName;
        }

        setTooltip(Tooltip.create(tooltip));
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    public boolean isExpanded() {
        return Minecraft.getInstance().screen instanceof SelectionScreen;
    }

    private static class SelectionScreen extends Screen implements EnderScreen {

        private final BaseEnumPickerWidget<?> parentWidget;

        protected SelectionScreen(BaseEnumPickerWidget<?> parentWidget) {
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
            // Don't make background dark
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!(parentWidget.expandTopLeft.x() <= mouseX && parentWidget.expandBottomRight.x() >= mouseX
                    && parentWidget.expandTopLeft.y() <= mouseY && parentWidget.expandBottomRight.y() >= mouseY
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

    private class SelectionWidget extends EnderButton {

        private final T value;
        private final int iconWidth;
        private final int iconHeight;

        SelectionWidget(Vector2i pos, int width, int height, T value) {
            super(pos.x(), pos.y(), width, height, Component.empty());
            this.value = value;
            this.iconWidth = width;
            this.iconHeight = height;
        }

        @Override
        public void onPress() {
            setValue(value);
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        }

        @Override
        public void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            guiGraphics.blitSprite(getValueIcon(value), getX(), getY(), iconWidth, iconHeight);
        }
    }
}
