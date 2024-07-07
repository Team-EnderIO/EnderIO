package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.menu.FluidTankMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FluidTankScreen extends MachineScreen<FluidTankMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/tank.png");
    public FluidTankScreen(FluidTankMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new FluidStackWidget(this, getMenu().getBlockEntity()::getFluidTank, 80 + leftPos, 21 + topPos, 16, 47));
        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
        addRenderableWidget(new ActivityWidget(this, menu.getBlockEntity()::getMachineStates, leftPos + imageWidth - 6 - 16, topPos + 16 * 4));
        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, menu, this::addRenderableWidget, font));
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
}
