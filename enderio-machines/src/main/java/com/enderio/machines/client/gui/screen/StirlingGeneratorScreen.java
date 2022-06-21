package com.enderio.machines.client.gui.screen;

import com.enderio.base.client.gui.screen.EIOScreen;
import com.enderio.base.client.gui.widgets.EnumIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.gui.widget.EnergyWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.StirlingGeneratorBlockEntity;
import com.enderio.machines.common.menu.StirlingGeneratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StirlingGeneratorScreen extends EIOScreen<StirlingGeneratorMenu> {
    private static final ResourceLocation BG_TEXTURE_SIMPLE = EIOMachines.loc("textures/gui/simple_stirling_generator.png");
    private static final ResourceLocation BG_TEXTURE = EIOMachines.loc("textures/gui/stirling_generator.png");

    public StirlingGeneratorScreen(StirlingGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new ProgressWidget(this, () -> menu.getBlockEntity().getBurnProgress(), getGuiLeft() + 81, getGuiTop() + 53, 14, 14, 176, 0, ProgressWidget.Direction.BOTTOM_UP));

        addRenderableOnly(new EnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
    }

    @Override
    protected ResourceLocation getBackgroundImage() {
        StirlingGeneratorBlockEntity be = getMenu().getBlockEntity();
        return be.getTier() == MachineTier.SIMPLE ? BG_TEXTURE_SIMPLE : BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
}
