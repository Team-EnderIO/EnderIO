package com.enderio.base.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.menu.ItemFilterMenu;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.CheckBox;
import com.enderio.core.common.capability.ItemFilterCapability;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;

public class ItemFilterScreen extends EIOScreen<ItemFilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183,201);
    private static ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/40/item_filter.png");

    public ItemFilterScreen(ItemFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        BG_TEXTURE = switch (pMenu.getFilter().getEntries().size()) {
            case 5 -> EnderIO.loc("textures/gui/40/basic_item_filter.png");
            case 2*5 ->  EnderIO.loc("textures/gui/40/advanced_item_filter.png");
            case 4*9 ->  EnderIO.loc("textures/gui/40/big_item_filter.png");
            case default -> throw new NotImplementedException();
        };
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new CheckBox(new Vector2i(getGuiLeft() + 110,getGuiTop() + 34), getMenu().getFilter()::isNbt, getMenu()::setNbt));
        addRenderableWidget(new CheckBox(new Vector2i(getGuiLeft() + 110,getGuiTop() + 34 + 20), getMenu().getFilter()::isInvert, getMenu()::setInverted));

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);
    }

    @Override
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        if (getMenu().getFilter() instanceof ItemFilterCapability itemFilterCapability) {
            if (pSlot != null && pSlot.index < itemFilterCapability.getEntries().size()) {
                if (!itemFilterCapability.getEntries().get(pSlot.index).isEmpty()) {
                    itemFilterCapability.setEntry(pSlotId, ItemStack.EMPTY);
                }
            }
            super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
        }
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
