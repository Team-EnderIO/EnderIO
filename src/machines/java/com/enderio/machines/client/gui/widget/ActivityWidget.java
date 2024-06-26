package com.enderio.machines.client.gui.widget;

import com.enderio.machines.client.gui.icon.MachineEnumIcons;
import com.enderio.machines.common.blockentity.MachineState;
import com.enderio.machines.common.blockentity.MachineStateType;
import com.enderio.machines.common.lang.MachineLang;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class ActivityWidget extends AbstractWidget {
    private final Supplier<Set<MachineState>> state;

    public ActivityWidget(int x, int y, Supplier<Set<MachineState>> state) {
        super(x, y, 16, 16, Component.empty());
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

        if (prio == null) {
            prio = MachineState.IDLE;
        }

        guiGraphics.blitSprite(Objects.requireNonNull(MachineEnumIcons.MACHINE_STATE_TYPE.get(prio.type())), getX(), getY(), 16, 16);

        RenderSystem.disableDepthTest();
        renderToolTip(guiGraphics, mouseX, mouseY);
    }

    private void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovered()) {
            Minecraft minecraft = Minecraft.getInstance();

            List<Component> list = state.get().stream().filter(s -> state.get().size() <= 1 || s.type() != MachineStateType.ACTIVE).map(s -> (Component) s.component()).toList();
            if (list.isEmpty()){
                list = List.of(MachineLang.TOOLTIP_IDLE);
            }

            guiGraphics.renderTooltip(minecraft.font, list, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
