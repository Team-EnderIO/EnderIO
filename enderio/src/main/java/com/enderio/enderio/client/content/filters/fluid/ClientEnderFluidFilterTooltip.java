package com.enderio.enderio.client.content.filters.fluid;

import com.enderio.core.client.gui.tooltip.AbstractClientSlotListTooltip;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class ClientEnderFluidFilterTooltip extends AbstractClientSlotListTooltip {

    private final EnderFluidFilter filter;

    public ClientEnderFluidFilterTooltip(EnderFluidFilter filter) {
        super();
        this.filter = filter;
    }

    protected List<FluidStack> fluidStacksToDisplay() {
        return filter.matches().stream()
            .filter(fluidStack -> !fluidStack.isEmpty())
            .toList();
    }

    @Override
    protected int slotCount() {
        return fluidStacksToDisplay().size();
    }

    @Override
    protected void extractSlotContents(int x, int y, int slotIndex, GuiGraphicsExtractor guiGraphics, Font font) {
        var fluidStack = fluidStacksToDisplay().get(slotIndex);

        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidStack.getFluid().defaultFluidState());
        TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();

        int color = 0xFFFFFFFF;
        if (fluidModel.fluidTintSource() != null) {
            color = fluidModel.fluidTintSource().colorAsStack(fluidStack);
        }

        int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
        int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TextureAtlas.LOCATION_BLOCKS, x, y, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight,
            16, 16, sprite.contents().width(), sprite.contents().height(), atlasWidth, atlasHeight, color);
    }
}
