package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.core.common.util.Vector2i;
import com.enderio.machines.client.gui.widget.EnergyWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.common.menu.ZombieGeneratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ZombieGeneratorScreen extends EIOScreen<ZombieGeneratorMenu> {

    private static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/zombie_generator.png");
    public ZombieGeneratorScreen(ZombieGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new FluidStackWidget(this, getMenu().getBlockEntity()::getFluidTank, 80 + leftPos, 21 + topPos, 16, 47));

        addRenderableOnly(new EnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, 16 + leftPos, 14 + topPos, 9, 43));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
    }

    @Override
    protected ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
}
