package com.enderio.base.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.client.gui.widget.DamageFilterModePickerWidget;
import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.filter.item.general.EnderItemFilterMenu;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.NotImplementedException;

public class EnderItemFilterScreen extends EnderContainerScreen<EnderItemFilterMenu> {

    private static final int WIDTH = 183;
    private static final int HEIGHT = 199;

    private static final ResourceLocation BG_2x9 = EnderIO.loc("textures/gui/screens/filter_2x9.png");
    private static final ResourceLocation BG_1x9 = EnderIO.loc("textures/gui/screens/filter_1x9.png");
    private static final ResourceLocation BG_3x9 = EnderIO.loc("textures/gui/screens/filter_3x9.png");
    private static final ResourceLocation BG_4x9 = EnderIO.loc("textures/gui/screens/filter_4x9.png");

    private static final ResourceLocation BACK_SPRITE = EnderIO.loc("icon/back");

    private static final ResourceLocation ICON_MATCH_COMPONENTS = EnderIO.loc("icon/match_components");
    private static final ResourceLocation ICON_IGNORE_COMPONENTS = EnderIO.loc("icon/ignore_components");

    private static final ResourceLocation ICON_ALLOW_LIST = EnderIO.loc("icon/allow_list");
    private static final ResourceLocation ICON_DENY_LIST = EnderIO.loc("icon/deny_list");

    private final ResourceLocation backgroundTexture;

    public EnderItemFilterScreen(EnderItemFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.shouldRenderLabels = true;

        this.titleLabelX = 28;
        this.titleLabelY = 14;

        this.inventoryLabelX += 6;
        this.inventoryLabelY = 34 + menu.type.rowCount() * 18;

        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT - (4 - menu.type.rowCount()) * 18;

        switch (pMenu.type.rowCount()) {
        case 1 -> backgroundTexture = BG_1x9;
        case 2 -> backgroundTexture = BG_2x9;
        case 3 -> backgroundTexture = BG_3x9;
        case 4 -> backgroundTexture = BG_4x9;
        default -> throw new NotImplementedException();
        }
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new IconButton(getGuiLeft() + 3, getGuiTop() + 3, 16, 16, BACK_SPRITE, null,
                () -> handleButtonPress(AbstractFilterMenu.BACK_BUTTON_ID)));

        int xPos = getGuiLeft() + WIDTH - 25;
        int yPos = getGuiTop() + 27 + menu.type.rowCount() * 18;

        if (getMenu().type.canFilterByDamage()) {
            addRenderableWidget(new DamageFilterModePickerWidget(xPos, yPos, getMenu()::damageFilterMode,
                    getMenu()::setDamageFilterMode, EIOLang.DAMAGE_FILTER_MODE));

            xPos -= 18;
        }

        if (getMenu().type.canMatchComponents()) {
            addRenderableWidget(new ToggleIconButton(xPos, yPos, 16, 16,
                    (b) -> b ? ICON_MATCH_COMPONENTS : ICON_IGNORE_COMPONENTS,
                    (b) -> b ? EIOLang.FILTER_MATCH_COMPONENTS : EIOLang.FILTER_IGNORE_COMPONENTS,
                    getMenu()::shouldCompareComponents,
                    (b) -> handleButtonPress(EnderItemFilterMenu.SHOULD_COMPARE_COMPONENTS_BUTTON_ID)));

            xPos -= 18;
        }

        addRenderableWidget(
                new ToggleIconButton(xPos, yPos, 16, 16, (b) -> b ? ICON_DENY_LIST : ICON_ALLOW_LIST,
                        (b) -> b ? EIOLang.FILTER_DENY_LIST : EIOLang.FILTER_ALLOW_LIST, getMenu()::isInverted,
                        (b) -> handleButtonPress(EnderItemFilterMenu.IS_INVERTED_BUTTON_ID)));

        xPos -= 18;
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
