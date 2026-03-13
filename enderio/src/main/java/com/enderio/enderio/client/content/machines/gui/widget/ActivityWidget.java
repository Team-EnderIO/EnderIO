package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.enderio.client.foundation.icon.MachineEnumIcons;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.state.MachineStateType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

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
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        //TODO blend depth pipeline

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
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Objects.requireNonNull(MachineEnumIcons.NEW_MACHINE_STATE_TYPE.get(prio.type())),
                    getX(), getY(), 16, 16);
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Objects.requireNonNull(MachineEnumIcons.MACHINE_STATE_TYPE.get(prio.type())), getX(),
                    getY(), 16, 16);
        }

        renderToolTip(graphics, mouseX, mouseY);
    }

    private void renderToolTip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (isHovered()) {
            Minecraft minecraft = Minecraft.getInstance();

            List<Component> list = state.get()
                    .stream()
                    .filter(s -> state.get().size() <= 1 || s.type() != MachineStateType.ACTIVE)
                    .map(s -> (Component) s.component())
                    .toList();
            if (list.isEmpty()) {
                list = List.of(MachinesLang.STATUS_IDLE);
            }

            //TODO which is what we need? widget has it's own tooltip stuff, but it's a bit different
            graphics.tooltip(minecraft.font, list.stream().map(c -> ClientTooltipComponent.create(c.getVisualOrderText())).toList(), mouseX, mouseY,
                DefaultTooltipPositioner.INSTANCE, null);

        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
