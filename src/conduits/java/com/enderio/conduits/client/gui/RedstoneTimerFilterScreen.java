package com.enderio.conduits.client.gui;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.menu.RedstoneTimerFilterMenu;
import com.enderio.core.client.gui.screen.EIOScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RedstoneTimerFilterScreen extends EIOScreen<RedstoneTimerFilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183,201);
    private static ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/40/item_filter.png");

    public RedstoneTimerFilterScreen(RedstoneTimerFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        EditBox pWidget = new EditBox(this.font, this.leftPos, this.topPos, Component.literal("" + getMenu().getFilter().getMaxTicks()));
        addRenderableWidget(pWidget);
        addRenderableWidget(Button.builder(Component.empty(), pButton -> getMenu().setTimer(pWidget.getValue()))
            .pos(this.leftPos, this.topPos)
            .build());
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
