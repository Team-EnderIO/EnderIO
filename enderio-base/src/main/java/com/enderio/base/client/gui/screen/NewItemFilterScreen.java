package com.enderio.base.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.client.gui.widget.DamageFilterModePickerWidget;
import com.enderio.base.common.item.filter.EnderItemFilterItem;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.menu.EnderItemFilterMenu;
import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.NotImplementedException;

public class NewItemFilterScreen extends EnderContainerScreen<EnderItemFilterMenu> {

    private static final int WIDTH = 183;
    private static final int HEIGHT = 201;

    private static final ResourceLocation BASIC_BG_TEXTURE = EnderIO.loc("textures/gui/40/basic_item_filter.png");
    private static final ResourceLocation ADVANCED_BG_TEXTURE = EnderIO.loc("textures/gui/40/advanced_item_filter.png");
    private static final ResourceLocation BIG_BG_TEXTURE = EnderIO.loc("textures/gui/40/big_item_filter.png");

    private static final ResourceLocation NBT_TEXTURE = EnderIO.loc("textures/gui/icons/range_buttons.png");

    private static final ResourceLocation ALLOW_LIST_SPRITE = EnderIO.loc("icon/allow_list");
    private static final ResourceLocation DENY_LIST_SPRITE = EnderIO.loc("icon/deny_list");

    private final ResourceLocation backgroundTexture;

    public NewItemFilterScreen(EnderItemFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.shouldRenderLabels = true;

        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;

        // TODO: I don't like how this looks tbh
        //       Maybe the sizes should be discussed in a number of rows instead.
        if (pMenu.type.slotCount() == EnderItemFilterItem.Type.BASIC.slotCount()) {
            backgroundTexture = BASIC_BG_TEXTURE;
        } else if (pMenu.type.slotCount() == EnderItemFilterItem.Type.ADVANCED.slotCount()) {
            backgroundTexture = ADVANCED_BG_TEXTURE;
        } else if (pMenu.type.slotCount() == EnderItemFilterItem.Type.BIG.slotCount() || pMenu.type.slotCount() == EnderItemFilterItem.Type.BIG_ADVANCED.slotCount()) {
            backgroundTexture = BIG_BG_TEXTURE;
        } else {
            throw new NotImplementedException();
        }
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new ToggleIconButton(getGuiLeft() + 110, getGuiTop() + 36 + 20, 16, 16, (b) -> b ? DENY_LIST_SPRITE : ALLOW_LIST_SPRITE, (b) -> b ? EIOLang.FILTER_DENY_LIST : EIOLang.FILTER_ALLOW_LIST, getMenu()::isInverted, (b) -> handleButtonPress(
            EnderItemFilterMenu.IS_INVERTED_BUTTON_ID)));

        if (getMenu().type.canMatchComponents()) {
            addRenderableWidget(new ToggleIconButton(getGuiLeft() + 110, getGuiTop() + 36, 16, 16, (b) -> NBT_TEXTURE, (b) -> b ? EIOLang.NBT_FILTER : EIOLang.NO_NBT_FILTER, getMenu()::shouldCompareComponents, (b) -> handleButtonPress(
                EnderItemFilterMenu.SHOULD_COMPARE_COMPONENTS_BUTTON_ID)));
        }

        if (getMenu().type.canFilterByDamage()) {
            addRenderableWidget(new DamageFilterModePickerWidget(getGuiLeft() + 110, getGuiTop() + 36 + 40, getMenu()::damageFilterMode, getMenu()::setDamageFilterMode, EIOLang.DAMAGE_FILTER_MODE));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(backgroundTexture, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
//        if (getMenu().getFilter() instanceof ItemFilterCapability itemFilterCapability) {
//            if (pSlot != null && pSlot.index < itemFilterCapability.getEntries().size()) {
//                if (!itemFilterCapability.getEntries().get(pSlot.index).isEmpty()) {
//                    itemFilterCapability.setEntry(pSlotId, ItemStack.EMPTY);
//                }
//            }
            super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
//        }
    }
}
