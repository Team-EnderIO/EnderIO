package com.enderio.machines.client.gui.widget;

import com.enderio.core.client.gui.widgets.EIOWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public class MessageWidget extends EIOWidget {
    private final List<Component> messageList;
    private final Screen displayOn;

    public MessageWidget(Screen displayOn, List<Component> messageList, int x, int y) {
        super(x, y, 16, 16);
        this.displayOn = displayOn;
        this.messageList = messageList;
    }

    public MessageWidget(Screen displayOn, List<Component> messageList, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.displayOn = displayOn;
        this.messageList = messageList;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.defaultBlendFunc();
        renderMessages(guiGraphics, mouseX, mouseY);
    }

    private void renderMessages(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            guiGraphics.renderTooltip(displayOn.getMinecraft().font, messageList, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
