package com.enderio.core.client.gui.widgets;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class EIOWidget extends AbstractWidget {
    public final int x;
    public final int y;
    protected final int width;
    protected final int height;

    public EIOWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty()); // TODO: A11Y: Narrator messages.
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
}
