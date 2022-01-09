package com.enderio.base.client.gui.widgets;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;

public abstract class EIOWidget extends GuiComponent implements Widget {
    public final int x;
    public final int y;
    protected int width;
    protected int height;

    public EIOWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
}
