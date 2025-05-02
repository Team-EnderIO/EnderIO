package com.enderio.base.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.menu.SimpleItemFilterMenu;
import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.NotImplementedException;

public class NewItemFilterScreen extends EnderContainerScreen<SimpleItemFilterMenu> {

    private static final int WIDTH = 183;
    private static final int HEIGHT = 201;

    private static final ResourceLocation BASIC_BG_TEXTURE = EnderIO.loc("textures/gui/40/item_filter.png");
    private static final ResourceLocation ADVANCED_BG_TEXTURE = EnderIO.loc("textures/gui/40/item_filter.png");
    private static final ResourceLocation BIG_BG_TEXTURE = EnderIO.loc("textures/gui/40/item_filter.png");

    private static final ResourceLocation BLACKLIST_TEXTURE = EnderIO.loc("textures/gui/icons/blacklist.png");
    private static final ResourceLocation NBT_TEXTURE = EnderIO.loc("textures/gui/icons/range_buttons.png");

    private final ResourceLocation backgroundTexture;

    public NewItemFilterScreen(SimpleItemFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;

        backgroundTexture = switch (pMenu.slotCount) {
            case 5 -> EnderIO.loc("textures/gui/40/basic_item_filter.png");
            case 2*5 ->  EnderIO.loc("textures/gui/40/advanced_item_filter.png");
            case 4*9 ->  EnderIO.loc("textures/gui/40/big_item_filter.png");
            default -> throw new NotImplementedException();
        };
    }

    @Override
    protected void init() {
        super.init();
        // TODO: Finish re-implementing these, this is a proof of concept.
//        addRenderableWidget(new ToggleImageButton<>(this, getGuiLeft() + 110,getGuiTop() + 36, 16, 16, 0, 0, 16, 0, NBT_TEXTURE, getMenu()::shouldCompareComponents, (b) -> handleButtonPress(SimpleItemFilterMenu.SHOULD_COMPARE_COMPONENTS_BUTTON_ID), () -> getMenu().shouldCompareComponents() ? EIOLang.NBT_FILTER : EIOLang.NO_NBT_FILTER));
        addRenderableWidget(new ToggleIconButton(getGuiLeft() + 110, getGuiTop() + 36, 16, 16, (b) -> NBT_TEXTURE, (b) -> b ? EIOLang.NBT_FILTER : EIOLang.NO_NBT_FILTER, getMenu()::shouldCompareComponents, (b) -> handleButtonPress(SimpleItemFilterMenu.SHOULD_COMPARE_COMPONENTS_BUTTON_ID)));
//        addRenderableWidget(new ToggleImageButton<>(this, getGuiLeft() + 110,getGuiTop() + 36 + 20, 16, 16, 0, 0, 16, 0, BLACKLIST_TEXTURE, getMenu()::isInverted, (b) -> handleButtonPress(SimpleItemFilterMenu.IS_INVERTED_BUTTON_ID), () -> getMenu().isInverted() ? EIOLang.BLACKLIST_FILTER : EIOLang.WHITELIST_FILTER));
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
