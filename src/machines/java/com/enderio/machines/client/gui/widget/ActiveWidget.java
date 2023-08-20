package com.enderio.machines.client.gui.widget;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.widgets.EIOWidget;
import com.enderio.machines.common.lang.MachineLang;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ActiveWidget extends EIOWidget {
    protected static final ResourceLocation WIDGETS = EnderIO.loc("textures/gui/40/widgetsv2.png");

    private final Supplier<MutableComponent> blocked;
    private final Screen displayOn;

    public ActiveWidget(Screen displayOn, Supplier<MutableComponent> blocked, int x, int y) {
        super(x, y, 16, 16);
        this.displayOn = displayOn;
        this.blocked = blocked;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        if (blocked.get().equals(MachineLang.TOOLTIP_ACTIVE)) {
            guiGraphics.blit(WIDGETS, x, y, 0, 0, 28*16, width, height, 256, 256);
        } else {
            guiGraphics.blit(WIDGETS, x, y, 0, 4*16, 28*16, width, height, 256, 256);
        }

        RenderSystem.disableDepthTest();
        guiGraphics.pose().popPose();
        renderToolTip(guiGraphics, mouseX, mouseY);
    }

    private void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
                guiGraphics.renderTooltip(displayOn.getMinecraft().font, blocked.get(), mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
