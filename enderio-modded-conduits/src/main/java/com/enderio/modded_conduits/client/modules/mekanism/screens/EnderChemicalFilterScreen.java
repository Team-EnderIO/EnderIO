package com.enderio.modded_conduits.client.modules.mekanism.screens;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.ChemicalFilterSlot;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilterMenu;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.NotImplementedException;

public class EnderChemicalFilterScreen extends EnderContainerScreen<EnderChemicalFilterMenu> {

    private static final int WIDTH = 183;
    private static final int HEIGHT = 199;

    // TODO: we need a central place for resource locations like these...
    private static final Identifier BG_2x9 = EnderIO.id("textures/gui/screens/filter_2x9.png");
    private static final Identifier BG_1x9 = EnderIO.id("textures/gui/screens/filter_1x9.png");
    private static final Identifier BG_3x9 = EnderIO.id("textures/gui/screens/filter_3x9.png");
    private static final Identifier BG_4x9 = EnderIO.id("textures/gui/screens/filter_4x9.png");

    private static final Identifier BACK_SPRITE = EnderIO.id("icon/back");

    private static final Identifier ICON_ALLOW_LIST = EnderIO.id("icon/allow_list");
    private static final Identifier ICON_DENY_LIST = EnderIO.id("icon/deny_list");

    private final Identifier backgroundTexture;

    public EnderChemicalFilterScreen(EnderChemicalFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT - (4 - menu.type.rowCount()) * 18);

        this.shouldRenderLabels = true;

        this.titleLabelX = 28;
        this.titleLabelY = 14;

        this.inventoryLabelX += 6;
        this.inventoryLabelY = 34 + menu.type.rowCount() * 18;

        switch (menu.type.rowCount()) {
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

        addRenderableWidget(
            new IconButton(getLeftPos() + 3, getTopPos() + 3, 16, 16, BACK_SPRITE, null, input -> handleButtonPress(AbstractFilterMenu.BACK_BUTTON_ID)));

        int xPos = getLeftPos() + WIDTH - 25;
        int yPos = getTopPos() + 27 + menu.type.rowCount() * 18;

        addRenderableWidget(new ToggleIconButton(xPos, yPos, 16, 16, (b) -> b ? ICON_DENY_LIST : ICON_ALLOW_LIST,
            (b) -> b ? FiltersLang.FILTER_DENY_LIST : FiltersLang.FILTER_ALLOW_LIST, getMenu()::isInverted,
            (b) -> handleButtonPress(EnderChemicalFilterMenu.IS_INVERTED_BUTTON_ID)));

    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture, getLeftPos(), getTopPos(), 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
        super.extractSlot(graphics, slot, mouseX, mouseY);

        if (!(slot instanceof ChemicalFilterSlot chemicalFilterSlot)) {
            return;
        }

        var chemicalStack = chemicalFilterSlot.getResource();
        if (chemicalStack.isEmpty()) {
            return;
        }

        TextureAtlasSprite sprite = minecraft.getAtlasManager()
            .getAtlasOrThrow(net.minecraft.data.AtlasIds.BLOCKS).getSprite(chemicalStack.getChemical().icon());
        int color = chemicalStack.getChemical().tint();

        int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
        int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
        graphics.blit(RenderPipelines.GUI_TEXTURED, TextureAtlas.LOCATION_BLOCKS, slot.x, slot.y, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight, 16,
            16, sprite.contents().width(), sprite.contents().height(), atlasWidth, atlasHeight, color);
    }

    @Override
    protected boolean renderCustomTooltip(GuiGraphicsExtractor graphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof ChemicalFilterSlot chemicalFilterSlot) {
            ChemicalStack value = chemicalFilterSlot.getResource();
            if (!value.isEmpty()) {
                graphics.setTooltipForNextFrame(this.font, Component.translatable(value.getTranslationKey()), x, y);
                return true;
            }
        }

        return false;
    }

}
