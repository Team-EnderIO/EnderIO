package com.enderio.conduits.client.gui;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.common.menu.RedstoneANDFilterMenu;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RedstoneANDFilterScreen extends EIOScreen<RedstoneANDFilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183,201);
    private static ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/40/item_filter.png");

    public RedstoneANDFilterScreen(RedstoneANDFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new EnumIconWidget<>(this, this.leftPos + 15, this.topPos + 30,
            getMenu().getFilter()::getFirstChannel,
            getMenu()::setFirstChannel,
            EIOLang.REDSTONE_CHANNEL));
        addRenderableWidget(new EnumIconWidget<>(this, this.leftPos + 15 + 60, this.topPos + 30,
            getMenu().getFilter()::getSecondChannel,
            getMenu()::setSecondChannel,
            EIOLang.REDSTONE_CHANNEL));
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return BG_SIZE;
    }
}
