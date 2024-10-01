package com.enderio.base.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.capability.StoredEntityData;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.menu.EntityFilterMenu;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.core.common.capability.EntityFilterCapability;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;

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
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        EntityFilterCapability entityFilterCapability = getMenu().getFilter();
        if (slot.index >= entityFilterCapability.getEntries().size()) {
            super.renderSlot(guiGraphics, slot);
            return;
        }

        StoredEntityData value = entityFilterCapability.getEntries().get(slot.index);
        if (value.getEntityType().isEmpty()) {
            return;
        }

        ItemStack stack = new ItemStack(EIOItems.FILLED_SOUL_VIAL.asItem());
        stack.getCapability(EIOCapabilities.ENTITY_STORAGE)
            .ifPresent(cap -> cap.setStoredEntityData(value));

        // renderSlotContents method does not exist in 1.20.1, so below is an imitation of its function.
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        guiGraphics.renderItem(stack, slot.x, slot.y, slot.x + slot.y * this.imageWidth);
        guiGraphics.renderItemDecorations(this.font, stack, slot.x, slot.y, null);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            ItemStack itemstack = this.hoveredSlot.getItem();
            EntityFilterCapability entityFilterCapability = getMenu().getFilter();
            if (hoveredSlot.index < entityFilterCapability.getEntries().size()) {
                StoredEntityData value = entityFilterCapability.getEntries().get(hoveredSlot.index);
                if (value.getEntityType().isPresent()) {
                    itemstack = new ItemStack(EIOItems.FILLED_SOUL_VIAL.asItem());
                    itemstack.getCapability(EIOCapabilities.ENTITY_STORAGE)
                        .ifPresent(cap -> cap.setStoredEntityData(value));
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
        EntityFilterCapability entityFilterCapability = getMenu().getFilter();
        if (entityFilterCapability instanceof EntityFilterCapability) {
            if (pSlot != null && pSlot.index < entityFilterCapability.getEntries().size()) {
                if (entityFilterCapability.getEntries().get(pSlot.index).getEntityType().isPresent()) {
                    entityFilterCapability.setEntry(pSlotId, StoredEntityData.empty());
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
