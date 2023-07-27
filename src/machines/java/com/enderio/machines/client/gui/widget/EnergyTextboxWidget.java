package com.enderio.machines.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Supplier;

public class EnergyTextboxWidget extends EditBox {
    private final Supplier<Integer> maxEnergy;
    
    public EnergyTextboxWidget(Supplier<Integer> maxEnergy, Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.maxEnergy = maxEnergy;
        setCanLoseFocus(true);
    }
	
	@Override
    public void setCanLoseFocus(boolean canLoseFocus) {
        super.setCanLoseFocus(canLoseFocus);
    }

    //Input only accepts numerals
    @Override
    public boolean charTyped(char codePoint, int modifiers) {

        if (codePoint >= 48 && codePoint <= 57) {
            int cursorPos = getCursorPosition() + 1;
            super.charTyped(codePoint, modifiers);
            setValue(formatEnergy(getValue()));
            moveCursorTo(cursorPos);
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode == 259) {
            int cursorPos = getCursorPosition();
            setValue(formatEnergy(getValue()));

            if (getValue().equals("0")) {
                moveCursorTo(1);
            } else {
                moveCursorTo(cursorPos);
            }
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + height) {
            super.mouseClicked(mouseX, mouseY, button);
            return true;
        } else {
            setFocused(false);
            return false;
        }
    }

    public String formatEnergy(String value) {

        if (value.isEmpty()) {
            return "0";
        }

        int energy = 0;
        value = value.replace(",", "");

        try {
            energy = Integer.parseInt(value);
            energy = Math.min(energy, maxEnergy.get());
        } catch(Exception e) {
            energy = 0;
        }

        try {
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            decimalFormat.applyPattern("#,###");
            return decimalFormat.format(energy);
        } catch (NumberFormatException e) {
            return "0";
        }
    }
	
	public int getInteger() {
        String integer = getValue().replace(",", "");

        try {
            return Integer.parseInt(integer);
        } catch(Exception e) {
            return 0;
        }
    }
}
