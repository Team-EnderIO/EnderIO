package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.foundation.widgets.FluidStackWidget;
import com.enderio.enderio.content.machines.killer_joe.KillerJoeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KillerJoeScreen extends MachineScreen<KillerJoeMenu> {

    private static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/screen/killer_joe.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public KillerJoeScreen(KillerJoeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        
        // Add fluid tank widget
        addRenderableOnly(new FluidStackWidget(18 + leftPos, 11 + topPos, 16, 47, menu::getFluidTank));
        
        // Add activity widget to show machine states
        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));
        
        // Add IO config overlay
        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
