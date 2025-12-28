package com.enderio.enderio.client.content.filters;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilterMenu;
import com.enderio.enderio.content.filters.fluid.FluidFilterSlot;
import com.enderio.enderio.content.filters.item.general.EnderItemFilterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.NotImplementedException;

public class EnderFluidFilterScreen extends EnderContainerScreen<EnderFluidFilterMenu> {

    private static final int WIDTH = 183;
    private static final int HEIGHT = 199;

    // TODO: we need a central place for resource locations like these...
    private static final Identifier BG_2x9 = EnderIO.rl("textures/gui/screens/filter_2x9.png");
    private static final Identifier BG_1x9 = EnderIO.rl("textures/gui/screens/filter_1x9.png");
    private static final Identifier BG_3x9 = EnderIO.rl("textures/gui/screens/filter_3x9.png");
    private static final Identifier BG_4x9 = EnderIO.rl("textures/gui/screens/filter_4x9.png");

    private static final Identifier BACK_SPRITE = EnderIO.rl("icon/back");

    private static final Identifier ICON_MATCH_COMPONENTS = EnderIO.rl("icon/match_components");
    private static final Identifier ICON_IGNORE_COMPONENTS = EnderIO.rl("icon/ignore_components");

    private static final Identifier ICON_ALLOW_LIST = EnderIO.rl("icon/allow_list");
    private static final Identifier ICON_DENY_LIST = EnderIO.rl("icon/deny_list");

    private final Identifier backgroundTexture;

    public EnderFluidFilterScreen(EnderFluidFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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

        if (getMenu().type.canMatchComponents()) {
            addRenderableWidget(new ToggleIconButton(xPos, yPos, 16, 16,
                    (b) -> b ? ICON_MATCH_COMPONENTS : ICON_IGNORE_COMPONENTS,
                    (b) -> b ? FiltersLang.FILTER_MATCH_COMPONENTS : FiltersLang.FILTER_IGNORE_COMPONENTS,
                    getMenu()::shouldCompareComponents,
                    (b) -> handleButtonPress(EnderItemFilterMenu.SHOULD_COMPARE_COMPONENTS_BUTTON_ID)));

            xPos -= 18;
        }

        addRenderableWidget(
                new ToggleIconButton(xPos, yPos, 16, 16, (b) -> b ? ICON_DENY_LIST : ICON_ALLOW_LIST,
                        (b) -> b ? FiltersLang.FILTER_DENY_LIST : FiltersLang.FILTER_ALLOW_LIST, getMenu()::isInverted,
                        (b) -> handleButtonPress(EnderItemFilterMenu.IS_INVERTED_BUTTON_ID)));

        xPos -= 18;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY) {
        super.renderSlot(guiGraphics, slot, mouseX, mouseY);

        if (!(slot instanceof FluidFilterSlot fluidFilterSlot)) {
            return;
        }

        var fluidStack = fluidFilterSlot.getResource();
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        Identifier still = props.getStillTexture(fluidStack);
        if (still != null) {
            //TODO Blend pipeline?
            AbstractTexture texture = minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
            if (texture instanceof TextureAtlas atlas) {
                TextureAtlasSprite sprite = atlas.getSprite(still);

                int color = props.getTintColor();

                int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
                int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TextureAtlas.LOCATION_BLOCKS, slot.x, slot.y, sprite.getU0() * atlasWidth,
                        sprite.getV0() * atlasHeight, 16, 16, sprite.contents().width(), sprite.contents().height(), atlasWidth,
                        atlasHeight, color);
            }
        }
    }

    @Override
    protected boolean renderCustomTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof FluidFilterSlot fluidFilterSlot) {
            FluidStack value = fluidFilterSlot.getResource();
            if (!value.isEmpty()) {
                guiGraphics.setTooltipForNextFrame(this.font, value.getHoverName(), x, y);
                return true;
            }
        }

        return false;
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
