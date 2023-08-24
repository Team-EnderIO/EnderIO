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

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ActiveWidget extends EIOWidget {
    protected static final ResourceLocation WIDGETS = EnderIO.loc("textures/gui/40/widgetsv2.png");

    private final Supplier<Map<ActiveWidget.MachineState, List<MutableComponent>>> blocked;
    private final Screen displayOn;

    public ActiveWidget(Screen displayOn, Supplier<Map<MachineState, List<MutableComponent>>> blocked, int x, int y) {
        super(x, y, 16, 16);
        this.displayOn = displayOn;
        this.blocked = blocked;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        Map<MachineState, List<MutableComponent>> map = blocked.get();
        if (map.containsKey(MachineState.STOPPED) && !map.get(MachineState.STOPPED).isEmpty()) {
            guiGraphics.blit(WIDGETS, x, y, 0, 4*16, 28*16, width, height, 256, 256);
        } else if (map.containsKey(MachineState.ERROR) && !map.get(MachineState.ERROR).isEmpty()) {
            guiGraphics.blit(WIDGETS, x, y, 0, 16, 28*16, width, height, 256, 256);
        } else {
            guiGraphics.blit(WIDGETS, x, y, 0, 0, 28*16, width, height, 256, 256);
        }

        RenderSystem.disableDepthTest();
        guiGraphics.pose().popPose();
        renderToolTip(guiGraphics, mouseX, mouseY);
    }

    private void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            Map<MachineState, List<MutableComponent>> map = blocked.get();
            if (map.containsKey(MachineState.STOPPED) && !map.get(MachineState.STOPPED).isEmpty()) {
                guiGraphics.renderTooltip(displayOn.getMinecraft().font, map.get(MachineState.STOPPED).get(0), mouseX, mouseY);
            } else if (map.containsKey(MachineState.ERROR) && !map.get(MachineState.ERROR).isEmpty()) {
                guiGraphics.renderTooltip(displayOn.getMinecraft().font, map.get(MachineState.ERROR).get(0), mouseX, mouseY);

            } else if (map.containsKey(MachineState.ACTIVE) && !map.get(MachineState.ACTIVE).isEmpty()){
                guiGraphics.renderTooltip(displayOn.getMinecraft().font, map.get(MachineState.ACTIVE).get(0), mouseX, mouseY);
            } else {
                guiGraphics.renderTooltip(displayOn.getMinecraft().font, MachineLang.TOOLTIP_ACTIVE, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public enum MachineState {
        ACTIVE,
        ERROR,
        STOPPED;
    }
}
