package com.enderio.conduits.client.gui;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.common.integrations.mekanism.ChemicalFilterCapability;
import com.enderio.conduits.common.menu.ChemicalFilterMenu;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EIOImageButton;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.mojang.blaze3d.systems.RenderSystem;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ChemicalFilterScreen extends EIOScreen<ChemicalFilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183, 201);
    private static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/40/basic_item_filter.png");
    private static final ResourceLocation BLACKLIST_TEXTURE = EnderIO.loc("textures/gui/icons/blacklist.png");
    private static final ResourceLocation BACK_TEXTURE = EnderIO.loc("textures/gui/icons/back.png");

    public ChemicalFilterScreen(ChemicalFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new ToggleImageButton<>(this, getGuiLeft() + 110, getGuiTop() + 36 + 20, 16, 16, 0, 0, 16, 0, BLACKLIST_TEXTURE, getMenu().getFilter()::isInvert, getMenu()::setInverted, () -> getMenu().getFilter().isInvert() ? EIOLang.BLACKLIST_FILTER : EIOLang.WHITELIST_FILTER));
        addRenderableWidget(new EIOImageButton(this, getGuiLeft() + 3, getGuiTop() + 3, 16, 16, 0, 0, 0, BACK_TEXTURE, button -> closeContainer()));
    }

    @Override
    public void renderSlot(GuiGraphics pGuiGraphics, Slot pSlot) {
        ChemicalFilterCapability filterCapability = getMenu().getFilter();
        if (pSlot.index >= filterCapability.size()) {
            super.renderSlot(pGuiGraphics, pSlot);
            return;
        }

        BoxedChemicalStack chemicalStack = filterCapability.getEntry(pSlot.index);
        if (chemicalStack.isEmpty()) {
            return;
        }

        TextureAtlasSprite sprite = Minecraft.getInstance()
            .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
            .apply(chemicalStack.getChemicalStack().getType().getIcon());

        int color = chemicalStack.getChemicalStack().getType().getTint();
        RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
            (color & 0xFF) / 255.0F, 1);
        RenderSystem.enableBlend();

        int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
        int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
        pGuiGraphics.blit(TextureAtlas.LOCATION_BLOCKS, pSlot.x, pSlot.y, 16, 16, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight, sprite.contents().width(), sprite.contents().height(), atlasWidth, atlasHeight);
        RenderSystem.setShaderColor(1, 1, 1, 1);
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