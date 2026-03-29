package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.enderio.client.foundation.icon.MachineEnumIcons;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.state.MachineStateType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class ActivityWidget extends AbstractWidget {
    private final Supplier<Set<MachineState>> state;
    private final boolean useNewIcons;

    public ActivityWidget(int x, int y, Supplier<Set<MachineState>> state) {
        this(x, y, state, false);
    }

    public ActivityWidget(int x, int y, Supplier<Set<MachineState>> state, boolean useNewIcons) {
        super(x, y, 16, 16, Component.empty());
        this.state = state;
        this.useNewIcons = useNewIcons;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        MachineState prio = null;
        for (MachineState machineState : state.get()) {
            if (prio == null || machineState.type().getPriority() > prio.type().getPriority()) {
                prio = machineState;
            }
        }

        if (prio == null) {
            prio = MachineState.IDLE;
        }

        if (useNewIcons) {
            guiGraphics.blitSprite(Objects.requireNonNull(MachineEnumIcons.NEW_MACHINE_STATE_TYPE.get(prio.type())),
                    getX(), getY(), 16, 16);
        } else {
            guiGraphics.blitSprite(Objects.requireNonNull(MachineEnumIcons.MACHINE_STATE_TYPE.get(prio.type())), getX(),
                    getY(), 16, 16);
        }

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        updateTooltip();
    }

    private void updateTooltip() {
        if (isHovered()) {
            MutableComponent component;

            List<MutableComponent> list = state.get()
                    .stream()
                    .filter(s -> state.get().size() <= 1 || s.type() != MachineStateType.ACTIVE)
                    .map(MachineState::component)
                    .toList();

            if (list.isEmpty()) {
                component = MachinesLang.STATUS_IDLE;
            } else {
                component = MutableComponent.create(list.getFirst().getContents());

                for (int i = 1; i < list.size(); i++) {
                    component = component.append("\n").append(list.get(i));
                }
            }

            setTooltip(Tooltip.create(component));
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
