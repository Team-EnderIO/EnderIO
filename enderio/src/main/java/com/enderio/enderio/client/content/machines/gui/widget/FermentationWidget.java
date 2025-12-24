package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.core.client.gui.widgets.EIOWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Supplier;

public class FermentationWidget extends EIOWidget {
    private final Supplier<Boolean> shouldShow;
    private final Supplier<FluidStack> first;
    private final Supplier<FluidStack> second;
    private final Supplier<Float> progress;

    public FermentationWidget(Supplier<Boolean> shouldShow, Supplier<FluidStack> first, Supplier<FluidStack> second, Supplier<Float> progress, int x, int y,
        int width,
        int height) {
        super(x, y, width, height);
        this.shouldShow = shouldShow;
        this.first = first;
        this.second = second;
        this.progress = progress;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        //TODO depth pipeline

        if (shouldShow.get() && !first.get().isEmpty()) {
            renderFluid(guiGraphics, first.get(), 1 - progress.get());
            renderFluid(guiGraphics, second.get(), progress.get());
        }

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

    public void renderFluid(GuiGraphics guiGraphics, FluidStack fluid, float opacity) {
        if (fluid.isEmpty()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation loc = props.getStillTexture();

        AbstractTexture texture = minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
        if (texture instanceof TextureAtlas atlas) {
            TextureAtlasSprite sprite = atlas.getSprite(loc);

            int color = props.getTintColor();

            int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
            int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TextureAtlas.LOCATION_BLOCKS, x, y, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight,
                width, height, sprite.contents().width(), sprite.contents().height(), atlasWidth, atlasHeight, color);
        }
    }
}
