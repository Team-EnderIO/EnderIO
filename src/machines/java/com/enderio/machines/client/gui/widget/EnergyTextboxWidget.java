package com.enderio.machines.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnergyTextboxWidget extends EditBox {
    private final Supplier<Integer> maxEnergy;

    protected final Screen displayOn;
    @Nullable
    private Consumer<String> focusResponder;

    public EnergyTextboxWidget(Screen displayOn, Supplier<Integer> maxEnergy, Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.maxEnergy = maxEnergy;
        this.displayOn = displayOn;
        setCanLoseFocus(true);
    }

    //Input only accepts digits (0, 1, ..., 9)
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (codePoint >= 48 && codePoint <= 57) {
            int cursorPos = getCursorPosition() + 1;
            boolean res = super.charTyped(codePoint, modifiers);
            setValue(getValue());
            moveCursorTo(cursorPos);

            return res;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean res = super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode == 259) {
            int cursorPos = getCursorPosition();
            setValue(formatEnergy(getValue()));
            moveCursorTo(cursorPos);
        }

        return res;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + height) {
            super.mouseClicked(mouseX, mouseY, button);

            //Clear content on right click
            if (button == 1) {
                moveCursorTo(1);
            }
            return true;
        } else {
            if (getValue().isEmpty()) {
                setValue("0");
            }
            setFocused(false);
            return false;
        }
    }

    public String formatEnergy(String value) {
        if (value.isEmpty()) {
            return "";
        }

        int energy;
        value = value.replace(",", "");

        try {
            energy = Integer.parseInt(value);
            energy = Math.min(energy, maxEnergy.get());
        } catch(Exception e) {
            energy = 0;
        }

        try {
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.applyPattern("#,###");
            return decimalFormat.format(energy);
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        renderToolTip(guiGraphics, mouseX, mouseY);
    }

    public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + height && !isFocused() && maxEnergy.get() != 0) {
            guiGraphics.renderTooltip(displayOn.getMinecraft().font, Component.literal(getValue() +"/" + maxEnergy.get()  +" µI"), mouseX, mouseY);
        }
    }

    public void OnFocusStoppedResponder(Consumer<String> responder) {
        this.focusResponder = responder;
    }

    @Override
    public void setValue(String text) {
        super.setValue(formatEnergy(text));
    }

    @Override
    public void setFocused(boolean focused) {
        if (!focused && focusResponder != null) {
            if (getValue().isEmpty()) {
                setValue("0");
            }
            focusResponder.accept(getValue());
        }
        super.setFocused(focused);
    }
}
