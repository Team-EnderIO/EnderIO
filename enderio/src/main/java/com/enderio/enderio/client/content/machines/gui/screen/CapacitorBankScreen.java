package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.widget.EnergyWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.client.foundation.widgets.ioconfig.IOConfigButton;
import com.enderio.enderio.client.foundation.widgets.ioconfig.IOConfigOverlay;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankManager;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorBankMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CapacitorBankScreen extends EnderContainerScreen<CapacitorBankMenu> {

    public static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/screen/capacitor_bank.png");

    private static final int WIDTH = 176;
    private static final int HEIGHT = 208;

    public CapacitorBankScreen(CapacitorBankMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);
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
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256,  256);
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
