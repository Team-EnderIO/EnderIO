package com.enderio.enderio.client.content.filters.redstone;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.foundation.widgets.DyeColorPickerWidget;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.content.filters.redstone.RedstoneDoubleChannelFilterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class RedstoneDoubleChannelFilterScreen extends EnderContainerScreen<RedstoneDoubleChannelFilterMenu> {

    private static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/40/item_filter.png");

    public RedstoneDoubleChannelFilterScreen(RedstoneDoubleChannelFilterMenu menu, Inventory playerInventory,
            Component title) {
        super(menu, playerInventory, title, 183, 201);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new IconButton(getLeftPos() + 3, getTopPos() + 3, 16, 16, EnderIO.id("icon/back"), null,
            input -> handleButtonPress(AbstractFilterMenu.BACK_BUTTON_ID)));
        addRenderableWidget(new DyeColorPickerWidget(this.leftPos + 15, this.topPos + 30,
                getMenu()::getFirstChannel, getMenu()::setFirstChannel, ConduitLang.REDSTONE_CHANNEL));
        addRenderableWidget(new DyeColorPickerWidget(this.leftPos + 15 + 60, this.topPos + 30,
                getMenu()::getSecondChannel, getMenu()::setSecondChannel, ConduitLang.REDSTONE_CHANNEL));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, getLeftPos(), getTopPos(), 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
