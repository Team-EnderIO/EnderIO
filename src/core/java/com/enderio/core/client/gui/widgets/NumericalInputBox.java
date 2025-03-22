package com.enderio.core.client.gui.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NumericalInputBox extends EditBox {

    private final Supplier<Integer> getter;
    private final Consumer<Integer> setter;

    public NumericalInputBox(Font font, int x, int y, int width, int height, Supplier<Integer> getter, Consumer<Integer> setter) {
        super(font, x, y, width, height, Component.empty());
        this.getter = getter;
        this.setter = setter;
        this.setValue("" + getter.get());
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return Character.isDigit(codePoint) && super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        String text = this.getValue();
        handleValueUpdate(text);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void setValue(String text) {
        handleValueUpdate(text);
    }

    private void handleValueUpdate(String text) {
        try {
            Integer value = Integer.parseInt(text);
            this.setter.accept(value);
            super.setValue(text);
        } catch (NumberFormatException e) {
            // TODO: log error ?
        }
    }
}
