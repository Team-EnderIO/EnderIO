package com.enderio.core.client.gui.screen;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class BaseOverlay extends AbstractWidget implements StateRestoringWidget {

    public BaseOverlay(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }

    @Override
    public Object getValueForRestore() {
        return this.visible;
    }

    @Override
    public void restoreValue(Object value) {
        if (value instanceof Boolean isVisible) {
            this.visible = isVisible;
        }
    }
}
