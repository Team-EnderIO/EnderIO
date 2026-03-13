package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.CapacitorEnergyWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.impulse_hopper.ImpulseHopperMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ImpulseHopperScreen extends MachineScreen<ImpulseHopperMenu> {
    private static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/screen/impulse_hopper.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public ImpulseHopperScreen(ImpulseHopperMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new CapacitorEnergyWidget(15 + leftPos, 9 + topPos, 9, 47, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight, 256, 256);

        // for all ghost slots
        for (int i = 0; i < 6; i++) {
            if (getMenu().getBlockEntity().ghostSlotHasItem(i)) {
                if (getMenu().getBlockEntity().canPass(i)) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, getGuiLeft() + 43 + (18 * i), getGuiTop() + 26, 200, 9, 18, 9, 256, 256);
                } else {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, getGuiLeft() + 43 + (18 * i), getGuiTop() + 26, 200, 0, 18, 9, 256, 256);
                }
                if (getMenu().getBlockEntity().canHoldAndMerge(i)) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, getGuiLeft() + 43 + (18 * i), getGuiTop() + 53, 200, 9, 18, 9, 256, 256);
                } else {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, getGuiLeft() + 43 + (18 * i), getGuiTop() + 53, 200, 0, 18, 9, 256, 256);
                }
            }
        }
    }
}
