package com.enderio.base.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.menu.FilterMenu;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.CheckBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ItemFilterScreen extends EIOScreen<FilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183,201);
    private static ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/40/item_filter.png");

    public ItemFilterScreen(FilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        switch (pMenu.getFilter().getEntries().size()) {
            case 5 -> BG_TEXTURE = EnderIO.loc("textures/gui/40/basic_item_filter.png");
            case 2*5 -> BG_TEXTURE = EnderIO.loc("textures/gui/40/advanced_item_filter.png");
            case 4*9 -> BG_TEXTURE = EnderIO.loc("textures/gui/40/big_item_filter.png");
        }
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new CheckBox(new Vector2i(getGuiLeft() + 34,getGuiTop() + 34), getMenu().getFilter()::isNbt, getMenu()::setNbt));
        addRenderableWidget(new CheckBox(new Vector2i(getGuiLeft() + 34 + 20,getGuiTop() + 34), getMenu().getFilter()::isInvert, getMenu()::setInverted));

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);
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
