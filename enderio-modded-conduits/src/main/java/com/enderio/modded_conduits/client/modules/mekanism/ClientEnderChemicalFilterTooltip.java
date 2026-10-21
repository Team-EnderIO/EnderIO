package com.enderio.modded_conduits.client.modules.mekanism;

import com.enderio.core.client.gui.tooltip.AbstractClientSlotListTooltip;
import com.enderio.modded_conduits.common.modules.mekanism.chemical_filter.EnderChemicalFilter;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.List;

public class ClientEnderChemicalFilterTooltip extends AbstractClientSlotListTooltip {

    private final EnderChemicalFilter filter;

    public ClientEnderChemicalFilterTooltip(EnderChemicalFilter filter) {
        super();
        this.filter = filter;
    }

    protected List<ChemicalStack> chemicalStacksToDisplay() {
        return filter.matches().stream()
            .filter(chemicalStack -> !chemicalStack.isEmpty())
            .toList();
    }

    @Override
    protected int slotCount() {
        return chemicalStacksToDisplay().size();
    }

    @Override
    protected void extractSlotContents(int x, int y, int slotIndex, GuiGraphicsExtractor guiGraphics, Font font) {
        var chemicalStack = chemicalStacksToDisplay().get(slotIndex);

        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager()
            .getAtlasOrThrow(net.minecraft.data.AtlasIds.BLOCKS).getSprite(chemicalStack.getChemical().icon());
        int color = chemicalStack.getChemical().tint();

        int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
        int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TextureAtlas.LOCATION_BLOCKS, x, y, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight,
            16, 16, sprite.contents().width(), sprite.contents().height(), atlasWidth, atlasHeight, color);
    }
}
