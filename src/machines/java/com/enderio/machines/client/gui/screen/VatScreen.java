package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.menu.VatMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VatScreen extends EIOScreen<VatMenu> {

    private static final ResourceLocation VAT_BG = EnderIO.loc("textures/gui/vat.png");

    public VatScreen(VatMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, false);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new FluidStackWidget(this, getMenu().getBlockEntity()::getInputTank, 30 + leftPos, 12 + topPos, 15, 47));
        addRenderableOnly(new FluidStackWidget(this, getMenu().getBlockEntity()::getOutputTank, 132 + leftPos, 12 + topPos, 15, 47));
        addRenderableOnly(new ProgressWidget.BottomUp(this, () -> menu.getBlockEntity().getProgress(), getGuiLeft() + 82, getGuiTop() + 64, 14, 14, 176, 0));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, menu, this::addRenderableWidget, font));
        addRenderableWidget(new ActivityWidget(this, menu.getBlockEntity()::getMachineStates, leftPos + imageWidth - 6 - 16, topPos + 16 * 4));
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return VAT_BG;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
}
