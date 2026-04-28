package com.enderio.modded_conduits.client.modules.mekanism;

import com.enderio.core.client.gui.tooltip.AbstractClientSlotListTooltip;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilter;
import com.mojang.blaze3d.systems.RenderSystem;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

import java.util.List;

public class ClientEnderChemicalFilterTooltip extends AbstractClientSlotListTooltip {

    private final EnderChemicalFilter filter;

    public ClientEnderChemicalFilterTooltip(EnderChemicalFilter filter) {
        super();
        this.filter = filter;
    }

    protected List<ChemicalStack> chemicalStacksToDisplay() {
        return filter.matches().stream()
            .filter(fluidStack -> !fluidStack.isEmpty())
            .toList();
    }

    @Override
    protected int slotCount() {
        return chemicalStacksToDisplay().size();
    }

    @Override
    protected void renderSlotContent(int x, int y, int slotIndex, GuiGraphics guiGraphics, Font font) {
        var chemicalStack = chemicalStacksToDisplay().get(slotIndex);
        TextureAtlasSprite sprite = Minecraft.getInstance()
            .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
            .apply(chemicalStack.getChemical().getIcon());

        int color = chemicalStack.getChemicalTint();
        RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
            (color & 0xFF) / 255.0F, 1);
        RenderSystem.enableBlend();

        int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
        int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
        guiGraphics.blit(TextureAtlas.LOCATION_BLOCKS, x + 1, y + 1, 16, 16, sprite.getU0() * atlasWidth,
            sprite.getV0() * atlasHeight, sprite.contents().width(), sprite.contents().height(), atlasWidth,
            atlasHeight);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
}
