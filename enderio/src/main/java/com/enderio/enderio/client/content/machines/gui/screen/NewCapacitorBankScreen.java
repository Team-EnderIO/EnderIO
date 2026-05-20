package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.widget.EnergyWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.client.foundation.widgets.ioconfig.IOConfigButton;
import com.enderio.enderio.client.foundation.widgets.ioconfig.IOConfigOverlay;
import com.enderio.enderio.content.machines.capacitor_bank.rework.CapacitorBankManager;
import com.enderio.enderio.content.machines.capacitor_bank.rework.NewCapacitorBankMenu;
import com.enderio.enderio.foundation.block.entity.MultiConfigurable;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class NewCapacitorBankScreen extends EnderContainerScreen<NewCapacitorBankMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/screen/capacitor_bank.png");

    public NewCapacitorBankScreen(NewCapacitorBankMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new EnergyWidget(8 + leftPos, 9 + topPos, 9, 68, menu::getEnergyStorage));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
            menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    protected IOConfigOverlay addIOConfigOverlay(int layer, int x, int y, int width, int height) {
        List<BlockPos> configurables = new ArrayList<>();

        CapacitorBankManager.CapacitorSyncData data = CapacitorBankManager.getData(getMenu().getBlockEntity().getUuid());
        if (data != null) {
            configurables.addAll(data.nodes());
        }

        var widget = addOverlayRenderable(layer, new IOConfigOverlay(x, y, width, height, configurables));
        addRestorableState("io_config", widget);
        widget.setVisible(false);
        return widget;
    }

    protected IOConfigButton addIOConfigButton(int x, int y, IOConfigOverlay configRenderer) {
        return addRenderableWidget(new IOConfigButton(x, y, configRenderer));
    }
}
