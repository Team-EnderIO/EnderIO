package com.enderio.machines.client.gui.screen;

import com.enderio.base.client.gui.screen.EIOScreen;
import com.enderio.base.client.gui.widgets.EnumIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.gui.widget.EnergyWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.menu.SagMillMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SagMillScreen extends EIOScreen<SagMillMenu> {
    public static final ResourceLocation BG_TEXTURE = EIOMachines.loc("textures/gui/sagmill.png");
    public static final ResourceLocation BG_TEXTURE_SIMPLE = EIOMachines.loc("textures/gui/simple_sagmill.png");

    public SagMillScreen(SagMillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new ProgressWidget(this, () -> menu.getBlockEntity().getProgress(), getGuiLeft() + 81, getGuiTop() + 31, 15, 23, 202, 0, false));

        addRenderableOnly(new EnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
    }

    @Override
    protected ResourceLocation getBackgroundImage() {
        if (getMenu().getBlockEntity().getTier() == MachineTier.SIMPLE) {
            return BG_TEXTURE_SIMPLE;
        }
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
}
