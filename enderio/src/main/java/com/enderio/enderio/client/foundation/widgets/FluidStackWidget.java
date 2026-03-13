package com.enderio.enderio.client.foundation.widgets;

import com.enderio.core.client.gui.widgets.EIOWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluidStackWidget extends EIOWidget {

    private final Supplier<FluidStorageInfo> fluidStorageSupplier;

    public FluidStackWidget(int x, int y, int width, int height, Supplier<FluidStorageInfo> fluidStorageSupplier) {
        super(x, y, width, height);
        this.fluidStorageSupplier = fluidStorageSupplier;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Minecraft minecraft = Minecraft.getInstance();
        //TODO Blend + depth pipeline?
        FluidStorageInfo fluidTank = fluidStorageSupplier.get();
        if (!fluidTank.contents().isEmpty()) {
            FluidStack fluidStack = fluidTank.contents();
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            Identifier still = props.getStillTexture(fluidStack);
            if (still != null) {
                AbstractTexture texture = minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
                if (texture instanceof TextureAtlas atlas) {
                    TextureAtlasSprite sprite = atlas.getSprite(still);

                    int color = props.getTintColor();

                    int stored = fluidStack.getAmount();
                    float capacity = fluidTank.capacity();
                    // Avoid growing beyond 100%, see GH-1106.
                    float filledVolume = Math.min(1.0f, stored / capacity);
                    int renderableHeight = (int) (filledVolume * height);

                    int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
                    int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));

                    graphics.pose().pushMatrix();
                    graphics.pose().translate(0, height - 16);
                    for (int i = 0; i < Math.ceil(renderableHeight / 16f); i++) {
                        int drawingHeight = Math.min(16, renderableHeight - 16 * i);
                        int notDrawingHeight = 16 - drawingHeight;
                        graphics.blit(RenderPipelines.GUI_TEXTURED, TextureAtlas.LOCATION_BLOCKS, x, y + notDrawingHeight,
                                sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight + notDrawingHeight, width,
                                drawingHeight, atlasWidth, atlasHeight, color);
                        graphics.pose().translate(0, -16);
                    }

                    graphics.pose().popMatrix();
                }
            }
        }

        renderToolTip(graphics, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void renderToolTip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            Minecraft minecraft = Minecraft.getInstance();

            var storage = fluidStorageSupplier.get();

            if (storage.contents().isEmpty()) {
                graphics.setComponentTooltipForNextFrame(minecraft.font, List.of(MachinesLang.GUI_NO_FLUID), mouseX,
                        mouseY);
            } else {
                graphics.setTooltipForNextFrame(minecraft.font,
                        Arrays.asList(storage.contents().getHoverName().getVisualOrderText(),
                                Component.literal(storage.contents().getAmount() + "mB").getVisualOrderText()),
                        mouseX, mouseY);
            }

        }
    }
}
