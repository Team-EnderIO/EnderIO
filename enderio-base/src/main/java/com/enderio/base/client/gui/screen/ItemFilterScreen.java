package com.enderio.base.client.gui.screen;

import com.enderio.EnderIOBase;
import com.enderio.base.common.capability.ItemFilterCapability;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.menu.ItemFilterMenu;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector2i;

public class ItemFilterScreen extends EIOScreen<ItemFilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183,201);
    private static ResourceLocation BG_TEXTURE = EnderIOBase.loc("textures/gui/40/item_filter.png");
    private static final ResourceLocation BLACKLIST_TEXTURE = EnderIOBase.loc("textures/gui/icons/blacklist.png");
    private static final ResourceLocation NBT_TEXTURE = EnderIOBase.loc("textures/gui/icons/range_buttons.png");

    public ItemFilterScreen(ItemFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        BG_TEXTURE = switch (pMenu.getFilter().getEntries().size()) {
            case 5 -> EnderIOBase.loc("textures/gui/40/basic_item_filter.png");
            case 2*5 ->  EnderIOBase.loc("textures/gui/40/advanced_item_filter.png");
            case 4*9 ->  EnderIOBase.loc("textures/gui/40/big_item_filter.png");
            default -> throw new NotImplementedException();
        };
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new ToggleImageButton<>(this, getGuiLeft() + 110,getGuiTop() + 36, 16, 16, 0, 0, 16, 0, NBT_TEXTURE, getMenu().getFilter()::isNbt, getMenu()::setNbt, () -> getMenu().getFilter().isNbt() ? EIOLang.NBT_FILTER : EIOLang.NO_NBT_FILTER));
        addRenderableWidget(new ToggleImageButton<>(this, getGuiLeft() + 110,getGuiTop() + 36 + 20, 16, 16, 0, 0, 16, 0, BLACKLIST_TEXTURE, getMenu().getFilter()::isInvert, getMenu()::setInverted, () -> getMenu().getFilter().isInvert() ? EIOLang.BLACKLIST_FILTER : EIOLang.WHITELIST_FILTER));

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
