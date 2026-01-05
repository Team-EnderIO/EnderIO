package com.enderio.enderio.client.content.machines.gui.screen.base;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.enderio.client.foundation.widgets.ioconfig.IOConfigButton;
import com.enderio.enderio.client.foundation.widgets.ioconfig.IOConfigOverlay;
import com.enderio.enderio.foundation.block.entity.MultiConfigurable;
import com.enderio.enderio.foundation.menu.GhostMachineSlot;
import com.enderio.enderio.foundation.menu.PreviewMachineSlot;
import com.enderio.enderio.foundation.menu.legacy.LegacyMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;

@Deprecated(forRemoval = true, since = "7.1")
public abstract class LegacyMachineScreen<T extends LegacyMachineMenu<?>> extends EnderContainerScreen<T> {
    public static final int SLOT_COLOR = -2130706433;

    protected LegacyMachineScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    protected IOConfigOverlay addIOConfigOverlay(int layer, int x, int y, int width, int height) {
        // TODO: getConfigurables on MachineMenu.
        List<BlockPos> configurables = menu.getBlockEntity() instanceof MultiConfigurable multiConfigurable
                ? multiConfigurable.getConfigurables()
                : List.of(menu.getBlockEntity().getBlockPos());

        var widget = addOverlayRenderable(layer, new IOConfigOverlay(x, y, width, height, configurables));
        addRestorableState("io_config", widget);
        widget.setVisible(false);
        return widget;
    }

    protected IOConfigButton addIOConfigButton(int x, int y, IOConfigOverlay configRenderer) {
        return addRenderableWidget(new IOConfigButton(x, y, configRenderer));
    }

    @Override
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);

        if (slot instanceof GhostMachineSlot || slot instanceof PreviewMachineSlot) {
            if (slot.hasItem()) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0.0F, 0.0F, 300F);
                guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, SLOT_COLOR);
                guiGraphics.pose().popPose();
            }
        }
    }
}
