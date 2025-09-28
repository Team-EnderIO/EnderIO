package com.enderio.base.client.gui.screen;

import com.enderio.base.common.filter.item.mod_id.ModIdItemFilterMenu;
import com.enderio.core.client.gui.screen.EnderContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ModIdItemFilterScreen extends EnderContainerScreen<ModIdItemFilterMenu> {
    public ModIdItemFilterScreen(ModIdItemFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }
}
