package com.enderio.enderio.client.gui.screens.filters.redstone;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.enderio.client.gui.widgets.DyeColorPickerWidget;
import com.enderio.enderio.conduits.common.init.ConduitLang;
import com.enderio.enderio.common.filter.redstone.RedstoneDoubleChannelFilterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

public class RedstoneDoubleChannelFilterScreen extends EIOScreen<RedstoneDoubleChannelFilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183, 201);
    private static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/40/item_filter.png");

    public RedstoneDoubleChannelFilterScreen(RedstoneDoubleChannelFilterMenu pMenu, Inventory pPlayerInventory,
            Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
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
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return BG_SIZE;
    }
}
