package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.menu.ImpulseHopperMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ImpulseHopperScreen extends EIOScreen<ImpulseHopperMenu> {
    private static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/impulse_hopper.png");

    public ImpulseHopperScreen(ImpulseHopperMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new CapacitorEnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, getMenu().getBlockEntity()::isCapacitorInstalled, 15 + leftPos, 9 + topPos, 9, 47));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 22, 16, 16, menu, this::addRenderableWidget, font));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);

        //for all ghost slots
        for (int i = 0; i < 6; i++) {
            if (getMenu().getBlockEntity().ghostSlotHasItem(i)) {
                if (getMenu().getBlockEntity().canPass(i)) {
                    guiGraphics.blit(getBackgroundImage(), getGuiLeft() + 43 + (18 * i), getGuiTop() + 26, 200, 9, 18, 9);
                } else {
                    guiGraphics.blit(getBackgroundImage(), getGuiLeft() + 43 + (18 * i), getGuiTop() + 26, 200, 0, 18, 9);
                }
                if (getMenu().getBlockEntity().canHoldAndMerge(i)) {
                    guiGraphics.blit(getBackgroundImage(), getGuiLeft() + 43 + (18 * i), getGuiTop() + 53, 200, 9, 18, 9);
                } else {
                    guiGraphics.blit(getBackgroundImage(), getGuiLeft() + 43 + (18 * i), getGuiTop() + 53, 200, 0, 18, 9);
                }
            }
        }
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
