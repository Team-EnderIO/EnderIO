package com.enderio.machines.client.gui.widget;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.widgets.EIOWidget;
import com.enderio.machines.common.blockentity.MachineState;
import com.enderio.machines.common.blockentity.MachineStateType;
import com.enderio.machines.common.lang.MachineLang;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class ActiveWidget extends EIOWidget {
    protected static final ResourceLocation WIDGETS = EnderIO.loc("textures/gui/icons/machine_states.png");

    private final Screen displayOn;
    private final Supplier<Set<MachineState>> state;

    public ActiveWidget(Screen displayOn, Supplier<Set<MachineState>> state, int x, int y) {
        super(x, y, 16, 16);
        this.displayOn = displayOn;
        this.state = state;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        MachineState prio = null;
        for (MachineState machineState: state.get()) {
            if (prio == null || machineState.type().getPriority() > prio.type().getPriority()) {
                prio = machineState;
            }
        }
        guiGraphics.blit(WIDGETS, x, y, 0, prio == null ? 16 : prio.type().getPriority() * 16, 0, width, height, 64, 16);

        RenderSystem.disableDepthTest();
        renderToolTip(guiGraphics, mouseX, mouseY);
    }

    private void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            List<Component> list = state.get().stream().filter(s -> state.get().size() <= 1 || s.type() != MachineStateType.ACTIVE).map(s -> (Component) s.component()).toList();
            if (list.isEmpty()){
                list = List.of(MachineLang.TOOLTIP_IDLE);
            }
            guiGraphics.renderTooltip(displayOn.getMinecraft().font, list, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
