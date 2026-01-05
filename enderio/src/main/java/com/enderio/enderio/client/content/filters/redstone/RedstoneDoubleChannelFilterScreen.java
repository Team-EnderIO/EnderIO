package com.enderio.enderio.client.content.filters.redstone;

import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.foundation.widgets.DyeColorPickerWidget;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.content.filters.redstone.RedstoneDoubleChannelFilterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class RedstoneDoubleChannelFilterScreen extends EIOScreen<RedstoneDoubleChannelFilterMenu> {

    private static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/40/item_filter.png");

    public RedstoneDoubleChannelFilterScreen(RedstoneDoubleChannelFilterMenu menu, Inventory playerInventory,
            Component title) {
        super(menu, playerInventory, title, 183, 201);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new DyeColorPickerWidget(this.leftPos + 15, this.topPos + 30,
                getMenu().getChannels()::getFirstChannel, getMenu()::setFirstChannel, ConduitLang.REDSTONE_CHANNEL));
        addRenderableWidget(new DyeColorPickerWidget(this.leftPos + 15 + 60, this.topPos + 30,
                getMenu().getChannels()::getSecondChannel, getMenu()::setSecondChannel, ConduitLang.REDSTONE_CHANNEL));
    }

    @Override
    public Identifier getBackgroundImage() {
        return BG_TEXTURE;
    }
}
