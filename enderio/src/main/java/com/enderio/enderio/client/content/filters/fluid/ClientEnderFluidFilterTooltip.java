package com.enderio.enderio.client.content.filters.fluid;

import com.enderio.core.client.gui.tooltip.AbstractClientSlotListTooltip;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilter;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
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
    protected void renderSlotContent(int x, int y, int slotIndex, GuiGraphics guiGraphics, Font font) {
        var fluidStack = fluidStacksToDisplay().get(slotIndex);
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation still = props.getStillTexture(fluidStack);
        if (still != null) {
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
            if (texture instanceof TextureAtlas atlas) {
                TextureAtlasSprite sprite = atlas.getSprite(still);

                int color = props.getTintColor();
                RenderSystem.setShaderColor(FastColor.ARGB32.red(color) / 255.0F,
                    FastColor.ARGB32.green(color) / 255.0F, FastColor.ARGB32.blue(color) / 255.0F,
                    FastColor.ARGB32.alpha(color) / 255.0F);
                RenderSystem.enableBlend();

                int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
                int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
                guiGraphics.blit(TextureAtlas.LOCATION_BLOCKS, x + 1, y + 1, 16, 16, sprite.getU0() * atlasWidth,
                    sprite.getV0() * atlasHeight, sprite.contents().width(), sprite.contents().height(), atlasWidth,
                    atlasHeight);
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
        }
    }
}
