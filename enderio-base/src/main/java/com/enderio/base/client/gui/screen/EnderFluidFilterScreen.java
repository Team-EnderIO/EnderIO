package com.enderio.base.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.filter.fluid.EnderFluidFilterMenu;
import com.enderio.base.common.filter.item.general.EnderItemFilterMenu;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.filter.fluid.FluidFilterSlot;
import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
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
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);

        if (!(slot instanceof FluidFilterSlot fluidFilterSlot)) {
            return;
        }

        var fluidStack = fluidFilterSlot.getResource();
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation still = props.getStillTexture(fluidStack);
        if (still != null) {
            AbstractTexture texture = minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
            if (texture instanceof TextureAtlas atlas) {
                TextureAtlasSprite sprite = atlas.getSprite(still);

                int color = props.getTintColor();
                RenderSystem.setShaderColor(FastColor.ARGB32.red(color) / 255.0F,
                        FastColor.ARGB32.green(color) / 255.0F, FastColor.ARGB32.blue(color) / 255.0F,
                        FastColor.ARGB32.alpha(color) / 255.0F);
                RenderSystem.enableBlend();

                int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
                int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
                guiGraphics.blit(TextureAtlas.LOCATION_BLOCKS, slot.x, slot.y, 16, 16, sprite.getU0() * atlasWidth,
                        sprite.getV0() * atlasHeight, sprite.contents().width(), sprite.contents().height(), atlasWidth,
                        atlasHeight);
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
        }
    }

    @Override
    protected boolean renderCustomTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof FluidFilterSlot fluidFilterSlot) {
            FluidStack value = fluidFilterSlot.getResource();
            if (!value.isEmpty()) {
                guiGraphics.renderTooltip(this.font, value.getHoverName(), x, y);
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
