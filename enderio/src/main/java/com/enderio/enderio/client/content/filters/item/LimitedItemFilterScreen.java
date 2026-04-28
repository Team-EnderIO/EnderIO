package com.enderio.enderio.client.content.filters.item;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.content.filters.item.limited.LimitedItemFilterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class LimitedItemFilterScreen extends EnderContainerScreen<LimitedItemFilterMenu> {

    private static final int WIDTH = 183;
    private static final int HEIGHT = 163;

    private static final Identifier BG = EnderIO.id("textures/gui/screens/filter_2x9.png");

    private static final Identifier BACK_SPRITE = EnderIO.id("icon/back");

    private static final Identifier ICON_MATCH_COMPONENTS = EnderIO.id("icon/match_components");
    private static final Identifier ICON_IGNORE_COMPONENTS = EnderIO.id("icon/ignore_components");

    public LimitedItemFilterScreen(LimitedItemFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);

        this.shouldRenderLabels = true;

        this.titleLabelX = 28;
        this.titleLabelY = 14;

        this.inventoryLabelX += 6;
        this.inventoryLabelY = 34 + 2 * 18;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new IconButton(getGuiLeft() + 3, getGuiTop() + 3, 16, 16, BACK_SPRITE, null,
                _ -> handleButtonPress(AbstractFilterMenu.BACK_BUTTON_ID)));

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
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(BG, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
