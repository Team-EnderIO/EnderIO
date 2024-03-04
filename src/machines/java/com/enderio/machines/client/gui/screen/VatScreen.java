package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.machines.common.menu.VatMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VatScreen extends EIOScreen<VatMenu> {

    private static final ResourceLocation VAT_BG = EnderIO.loc("textures/gui/vat.png");

    public VatScreen(VatMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, true);
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
