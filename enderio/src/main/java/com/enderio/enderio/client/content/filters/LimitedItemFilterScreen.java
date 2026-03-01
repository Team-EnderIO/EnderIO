package com.enderio.enderio.client.content.filters;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.content.filters.item.limited.LimitedItemFilterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class LimitedItemFilterScreen extends EnderContainerScreen<LimitedItemFilterMenu> {

    private static final int WIDTH = 183;
    private static final int HEIGHT = 163;

    private static final ResourceLocation BG = EnderIO.rl("textures/gui/screens/filter_2x9.png");

    private static final ResourceLocation BACK_SPRITE = EnderIO.rl("icon/back");

    private static final ResourceLocation ICON_MATCH_COMPONENTS = EnderIO.rl("icon/match_components");
    private static final ResourceLocation ICON_IGNORE_COMPONENTS = EnderIO.rl("icon/ignore_components");

    public LimitedItemFilterScreen(LimitedItemFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.shouldRenderLabels = true;

        this.titleLabelX = 28;
        this.titleLabelY = 14;

        this.inventoryLabelX += 6;
        this.inventoryLabelY = 34 + 2 * 18;

        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new IconButton(getGuiLeft() + 3, getGuiTop() + 3, 16, 16, BACK_SPRITE, null,
                () -> handleButtonPress(AbstractFilterMenu.BACK_BUTTON_ID)));

        int xPos = getGuiLeft() + WIDTH - 25;
        int yPos = getGuiTop() + 27 + 2 * 18;

        addRenderableWidget(new DamageFilterModePickerWidget(xPos, yPos, getMenu()::damageFilterMode,
                getMenu()::setDamageFilterMode, FiltersLang.DAMAGE_FILTER_MODE));

        xPos -= 18;

        addRenderableWidget(new ToggleIconButton(xPos, yPos, 16, 16,
                (b) -> b ? ICON_MATCH_COMPONENTS : ICON_IGNORE_COMPONENTS,
                (b) -> b ? FiltersLang.FILTER_MATCH_COMPONENTS : FiltersLang.FILTER_IGNORE_COMPONENTS,
                getMenu()::shouldCompareComponents,
                (b) -> handleButtonPress(LimitedItemFilterMenu.SHOULD_COMPARE_COMPONENTS_BUTTON_ID)));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(BG, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, net.minecraft.world.inventory.ClickType type) {
        super.slotClicked(slot, slotId, mouseButton, type);
    }
}
