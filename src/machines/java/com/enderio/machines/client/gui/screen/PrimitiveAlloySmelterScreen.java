package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.menu.PrimitiveAlloySmelterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PrimitiveAlloySmelterScreen extends EIOScreen<PrimitiveAlloySmelterMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/primitive_alloy_smelter.png");

    public PrimitiveAlloySmelterScreen(PrimitiveAlloySmelterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new ProgressWidget.BottomUp(this, () -> menu.getBlockEntity().getBurnProgress(), getGuiLeft() + 41, getGuiTop() + 37, 14, 14, 176, 0, false));
        addRenderableOnly(new ProgressWidget.LeftRight(this, () -> menu.getBlockEntity().getCraftingProgress(), getGuiLeft() + 79, getGuiTop() + 35, 24, 17, 176, 14));
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
}
