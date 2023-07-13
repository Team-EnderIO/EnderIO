package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.menu.PaintingMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PaintingMachineScreen extends EIOScreen<PaintingMachineMenu> {

    private static final ResourceLocation PAINTING_MACHINE_BG = EnderIO.loc("textures/gui/painting_machine.png");

    public PaintingMachineScreen(PaintingMachineMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, true);
        this.inventoryLabelY = this.imageHeight - 106;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(addRenderableOnly(new CapacitorEnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, getMenu().getBlockEntity()::isCapacitorInstalled, 16 + leftPos, 14 + topPos, 9, 42)));
        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 14, topPos + 52, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableOnly(new ProgressWidget.LeftRight(this, () -> menu.getBlockEntity().getCraftingProgress(), getGuiLeft() + 89, getGuiTop() + 35, 22, 16, 177, 14));
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return PAINTING_MACHINE_BG;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }

}
