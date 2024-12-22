package com.enderio.machines.client.gui.widget;

import com.enderio.machines.client.gui.icon.MachineEnumIcons;
import com.enderio.machines.common.blocks.base.state.MachineState;
import com.enderio.machines.common.blocks.base.state.MachineStateType;
import com.enderio.machines.common.lang.MachineLang;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

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
        renderToolTip(guiGraphics, mouseX, mouseY);
    }

    private void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovered()) {
            Minecraft minecraft = Minecraft.getInstance();

            List<Component> list = state.get()
                    .stream()
                    .filter(s -> state.get().size() <= 1 || s.type() != MachineStateType.ACTIVE)
                    .map(s -> (Component) s.component())
                    .toList();
            if (list.isEmpty()) {
                list = List.of(MachineLang.TOOLTIP_IDLE);
            }

            guiGraphics.renderTooltip(minecraft.font, list, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
