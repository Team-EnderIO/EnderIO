package com.enderio.base.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.menu.FilterMenu;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.CheckBox;
import com.enderio.core.common.capability.FluidFilterCapability;
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

public class FluidFilterScreen extends EIOScreen<FilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183,201);
    private static ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/40/item_filter.png");

    public FluidFilterScreen(FilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        switch (pMenu.getFilter().getEntries().size()) {
            case 5 -> BG_TEXTURE = EnderIO.loc("textures/gui/40/basic_item_filter.png");
            case 2*5 -> BG_TEXTURE = EnderIO.loc("textures/gui/40/advanced_item_filter.png");
            case 4*9 -> BG_TEXTURE = EnderIO.loc("textures/gui/40/big_item_filter.png");
        }
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new CheckBox(new Vector2i(getGuiLeft() + 34,getGuiTop() + 34), getMenu().getFilter()::isNbt, getMenu()::setNbt));
        addRenderableWidget(new CheckBox(new Vector2i(getGuiLeft() + 34 + 20,getGuiTop() + 34), getMenu().getFilter()::isInvert, getMenu()::setInverted));

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);
    }

    @Override
    public void renderSlot(GuiGraphics pGuiGraphics, Slot pSlot) {
        if (getMenu().getFilter() instanceof FluidFilterCapability filterCapability) {
            if (pSlot.index >= filterCapability.getEntries().size()) {
                super.renderSlot(pGuiGraphics, pSlot);
                return;
            }
            FluidStack fluidStack = filterCapability.getEntries().get(pSlot.index);
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            ResourceLocation still = props.getStillTexture(fluidStack);
            if (still != null) {
                AbstractTexture texture = minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
                if (texture instanceof TextureAtlas atlas) {
                    TextureAtlasSprite sprite = atlas.getSprite(still);

                    int color = props.getTintColor();
                    RenderSystem.setShaderColor(FastColor.ARGB32.red(color) / 255.0F, FastColor.ARGB32.green(color) / 255.0F,
                        FastColor.ARGB32.blue(color) / 255.0F, FastColor.ARGB32.alpha(color) / 255.0F);
                    RenderSystem.enableBlend();

                    int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
                    int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
                    pGuiGraphics.blit(TextureAtlas.LOCATION_BLOCKS, pSlot.x, pSlot.y, 16, 16, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight, sprite.contents().width(), sprite.contents().height(),
                        atlasWidth, atlasHeight);
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                }
            }
        }
    }

    @Override
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        if (getMenu().getFilter() instanceof FluidFilterCapability filterCapability) {
            if (pSlot != null && pSlot.index < filterCapability.getEntries().size()) {
                if (!filterCapability.getEntries().get(pSlot.index).isEmpty()) {
                    filterCapability.getEntries().set(pSlotId, FluidStack.EMPTY);
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
