package com.enderio.core.client.gui.widgets;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class EIOWidget extends AbstractWidget {
    protected final int width;
    protected final int height;

    public EIOWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty()); // TODO: A11Y: Narrator messages.
        this.width = width;
        this.height = height;
    }

    protected boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
    }
}
