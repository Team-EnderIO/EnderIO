package com.enderio.machines.client.gui.screen;

import com.enderio.base.client.gui.screen.EIOScreen;
import com.enderio.base.client.gui.widgets.EnumIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.gui.widget.EnergyWidget;
import com.enderio.machines.common.menu.ImpulseHopperMenu;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ImpulseHopperScreen extends EIOScreen<ImpulseHopperMenu> {
    private static final ResourceLocation BG_TEXTURE = EIOMachines.loc("textures/gui/impulse_hopper.png");

    public ImpulseHopperScreen(ImpulseHopperMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    
    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new EnergyWidget(this, getMenu().getBlockEntity()::getGuiEnergy, 15 + leftPos, 9 + topPos, 9, 47));

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
