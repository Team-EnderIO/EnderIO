package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.machines.common.menu.TravelAnchorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TravelAnchorScreen extends EIOScreen<TravelAnchorMenu> {

    private static final ResourceLocation TRAVEL_ANCHOR_BG = EnderIO.loc("textures/gui/travel_accessible.png");

    public TravelAnchorScreen(TravelAnchorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, false);
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return TRAVEL_ANCHOR_BG;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 184);
    }
}
