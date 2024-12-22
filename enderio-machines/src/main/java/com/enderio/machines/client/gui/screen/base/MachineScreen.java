package com.enderio.machines.client.gui.screen.base;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigOverlay;
import com.enderio.machines.common.blockentity.base.MultiConfigurable;
import com.enderio.machines.common.blocks.base.menu.GhostMachineSlot;
import com.enderio.machines.common.blocks.base.menu.MachineMenu;
import com.enderio.machines.common.blocks.base.menu.PreviewMachineSlot;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public abstract class MachineScreen<T extends MachineMenu<?>> extends EnderContainerScreen<T> {
    public static final int SLOT_COLOR = -2130706433;

    protected MachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
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
