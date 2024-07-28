package com.enderio.modconduits.mods.mekanism;

import com.enderio.EnderIOBase;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.mojang.blaze3d.systems.RenderSystem;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector2i;

public class ChemicalFilterScreen extends EIOScreen<ChemicalFilterMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(183,201);
    private static ResourceLocation BG_TEXTURE = EnderIOBase.loc("textures/gui/40/item_filter.png");
    private static final ResourceLocation BLACKLIST_TEXTURE = EnderIOBase.loc("textures/gui/icons/blacklist.png");
    private static final ResourceLocation NBT_TEXTURE = EnderIOBase.loc("textures/gui/icons/range_buttons.png");

    public ChemicalFilterScreen(ChemicalFilterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        BG_TEXTURE = switch (pMenu.getFilter().getEntries().size()) {
            case 5 -> EnderIOBase.loc("textures/gui/40/basic_item_filter.png");
            case 2 * 5 -> EnderIOBase.loc("textures/gui/40/advanced_item_filter.png");
            case 4 * 9 -> EnderIOBase.loc("textures/gui/40/big_item_filter.png");
            default -> throw new NotImplementedException();
        };
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new ToggleImageButton<>(this, getGuiLeft() + 110,getGuiTop() + 36 + 20, 16, 16, 0, 0, 16, 0, BLACKLIST_TEXTURE, getMenu().getFilter()::isInvert, getMenu()::setInverted, () -> getMenu().getFilter().isInvert() ? EIOLang.BLACKLIST_FILTER : EIOLang.WHITELIST_FILTER));

    }

    @Override
    public void renderSlot(GuiGraphics pGuiGraphics, Slot pSlot) {
        ChemicalFilterCapability filterCapability = getMenu().getFilter();
        if (pSlot.index >= filterCapability.getEntries().size()) {
            super.renderSlot(pGuiGraphics, pSlot);
            return;
        }

        BoxedChemicalStack stack = filterCapability.getEntries().get(pSlot.index);

        if (stack.isEmpty()) {
            return;
        }
        TextureAtlasSprite sprite = Minecraft.getInstance()
            .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
            .apply(stack.getChemicalStack().getChemical().getIcon());

        int color = stack.getChemicalStack().getChemicalTint();
        RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
            (color & 0xFF) / 255.0F, 1);
        RenderSystem.enableBlend();

        int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
        int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
        pGuiGraphics.blit(TextureAtlas.LOCATION_BLOCKS, pSlot.x, pSlot.y, 16, 16, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight, sprite.contents().width(), sprite.contents().height(),
            atlasWidth, atlasHeight);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            ItemStack itemstack = this.hoveredSlot.getItem();
            var capability = getMenu().getFilter();
            if (hoveredSlot.index < capability.getEntries().size()) {
                BoxedChemicalStack value = capability.getEntries().get(hoveredSlot.index);
                if (!value.isEmpty()) {
                    guiGraphics.renderTooltip(this.font, value.getTextComponent(), x, y);
                    return;
                }
            }
            if (itemstack.isEmpty()) {
                return;
            }
            guiGraphics.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, x, y);
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
