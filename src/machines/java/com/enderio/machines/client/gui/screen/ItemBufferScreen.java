package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.machines.client.gui.widget.EnergyTextboxWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.menu.ItemBufferMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ItemBufferScreen extends EIOScreen<ItemBufferMenu> {

    private static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/item_buffer.png");
    EnergyTextboxWidget widget;
    public ItemBufferScreen(ItemBufferMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth  - 8 - 12, topPos + 6, 16, 16, menu, this::addRenderableWidget, font));
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(175, 166);
    }
}
