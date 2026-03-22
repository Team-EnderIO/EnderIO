package com.enderio.enderio.client.foundation.widgets;

import com.enderio.core.client.gui.widgets.EIOWidget;
import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.function.Supplier;

public class FluidStackStaticWidget extends EIOWidget {

    private final Supplier<FluidStorageInfo> fluidStorageSupplier;

    public FluidStackStaticWidget(int x, int y, int width, int height, Supplier<FluidStorageInfo> fluidStorageSupplier) {
        super(x, y, width, height);
        this.fluidStorageSupplier = fluidStorageSupplier;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        //TODO blend depth pipeline
        FluidStorageInfo fluidTank = fluidStorageSupplier.get();
        if (!fluidTank.contents().isEmpty()) {
            FluidStack fluidStack = fluidTank.contents();

            FluidModel fluidModel = minecraft.getModelManager().getFluidStateModelSet().get(fluidStack.getFluid().defaultFluidState());
            TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();

            int color = 0xFFFFFFFF;
            if (fluidModel.fluidTintSource() != null) {
                fluidModel.fluidTintSource().colorAsStack(fluidStack);
            }

            int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
            int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
            // TODO: 1.21.4: Check this
            graphics.blit(RenderPipelines.GUI_TEXTURED, TextureAtlas.LOCATION_BLOCKS, x, y, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight, width,
                height, sprite.contents().width(), sprite.contents().height(), atlasWidth, atlasHeight, color);

            renderToolTip(graphics, mouseX, mouseY);
        }

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void renderToolTip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            Minecraft minecraft = Minecraft.getInstance();
            graphics.setTooltipForNextFrame(minecraft.font, Arrays.asList(fluidStorageSupplier.get().contents().getHoverName().getVisualOrderText(),
                Component.literal(fluidStorageSupplier.get().contents().getAmount() + "mB").getVisualOrderText()), mouseX, mouseY);
        }
    }
}
