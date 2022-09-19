package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.common.util.Vector2i;
import com.enderio.machines.common.menu.SoulBinderMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SoulBinderScreen extends EIOScreen<SoulBinderMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/slice_and_splice.png");

    public SoulBinderScreen(SoulBinderMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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
