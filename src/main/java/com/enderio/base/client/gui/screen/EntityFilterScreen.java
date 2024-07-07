package com.enderio.base.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.attachment.StoredEntityData;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.menu.EntityFilterMenu;
import com.enderio.base.common.menu.ItemFilterMenu;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.core.common.capability.EntityFilterCapability;
import com.enderio.core.common.capability.FluidFilterCapability;
import com.enderio.core.common.capability.ItemFilterCapability;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public class EntityFilterScreen extends EIOScreen<EntityFilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183,201);
    private static ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/40/item_filter.png");
    private static final ResourceLocation BLACKLIST_TEXTURE = EnderIO.loc("textures/gui/icons/blacklist.png");
    private static final ResourceLocation NBT_TEXTURE = EnderIO.loc("textures/gui/icons/range_buttons.png");

    public EntityFilterScreen(EntityFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        BG_TEXTURE = switch (pMenu.getFilter().getEntries().size()) {
            case 5 -> EnderIO.loc("textures/gui/40/basic_item_filter.png");
            case 2*5 ->  EnderIO.loc("textures/gui/40/advanced_item_filter.png");
            case 4*9 ->  EnderIO.loc("textures/gui/40/big_item_filter.png");
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
    protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
        EntityFilterCapability entityFilterCapability = getMenu().getFilter();
        if (slot.index >= entityFilterCapability.getEntries().size()) {
            super.renderSlotContents(guiGraphics, itemstack, slot, countString);
            return;
        }
        StoredEntityData value = entityFilterCapability.getEntries().get(slot.index);
        if (!value.hasEntity()) {
            return;
        }
        ItemStack stack = new ItemStack(EIOItems.FILLED_SOUL_VIAL.asItem());
        stack.set(EIODataComponents.STORED_ENTITY, value);
        super.renderSlotContents(guiGraphics, stack, slot, null);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            ItemStack itemstack = this.hoveredSlot.getItem();
            EntityFilterCapability entityFilterCapability = getMenu().getFilter();
            if (hoveredSlot.index < entityFilterCapability.getEntries().size()) {
                StoredEntityData value = entityFilterCapability.getEntries().get(hoveredSlot.index);
                if (value.hasEntity()) {
                    itemstack = new ItemStack(EIOItems.FILLED_SOUL_VIAL.asItem());
                    itemstack.set(EIODataComponents.STORED_ENTITY, value);
                }
            }
            if (itemstack.isEmpty()) {
                return;
            }
            pGuiGraphics.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, pX, pY);
        }
    }

    @Override
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        if (getMenu().getFilter() instanceof EntityFilterCapability entityFilterCapability) {
            if (pSlot != null && pSlot.index < entityFilterCapability.getEntries().size()) {
                if (entityFilterCapability.getEntries().get(pSlot.index).hasEntity()) {
                    entityFilterCapability.setEntry(pSlotId, StoredEntityData.EMPTY);
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
