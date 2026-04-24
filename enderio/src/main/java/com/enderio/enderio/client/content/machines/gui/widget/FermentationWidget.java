package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.core.client.gui.widgets.EIOWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Supplier;

public class FermentationWidget extends EIOWidget {
    private final Supplier<Boolean> shouldShow;
    private final Supplier<FluidStack> first;
    private final Supplier<FluidStack> second;
    private final Supplier<Float> progress;

    public FermentationWidget(Supplier<Boolean> shouldShow, Supplier<FluidStack> first, Supplier<FluidStack> second, Supplier<Float> progress, int x, int y,
        int width, int height) {
        super(x, y, width, height);
        this.shouldShow = shouldShow;
        this.first = first;
        this.second = second;
        this.progress = progress;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (shouldShow.get() && !first.get().isEmpty()) {
            renderFluid(graphics, second.get(), progress.get());
            renderFluid(graphics, first.get(), 1 - progress.get());
        }

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    public void renderFluid(GuiGraphicsExtractor graphics, FluidStack fluid, float opacity) {
        if (fluid.isEmpty()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();

        FluidModel fluidModel = minecraft.getModelManager().getFluidStateModelSet().get(fluid.getFluid().defaultFluidState());
        TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();

        int color = 0xFFFFFFFF;
        if (fluidModel.fluidTintSource() != null) {
            color = fluidModel.fluidTintSource().colorAsStack(fluid);
            color = ARGB.color(opacity, color);
        }

        int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
        int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
        graphics.blit(RenderPipelines.GUI_TEXTURED, TextureAtlas.LOCATION_BLOCKS, x, y, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight, width,
            height, sprite.contents().width(), sprite.contents().height(), atlasWidth, atlasHeight, color);
    }
}
