package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.common.util.Vector2i;
import com.enderio.machines.common.menu.PoweredSpawnerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PoweredSpawnerScreen extends EIOScreen<PoweredSpawnerMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/slice_and_splice.png");

    public PoweredSpawnerScreen(PoweredSpawnerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

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
