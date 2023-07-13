package com.enderio.machines.client.gui.widget;

import com.enderio.core.client.gui.widgets.EIOWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Arrays;
import java.util.function.Supplier;

public class FluidStackStaticWidget extends EIOWidget {

    private final Screen displayOn;
    private final Supplier<FluidTank> getFluid;

    public FluidStackStaticWidget(Screen displayOn, Supplier<FluidTank> getFluid, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.displayOn = displayOn;
        this.getFluid = getFluid;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        FluidTank fluidTank = getFluid.get();
        if (!fluidTank.isEmpty()) {
            FluidStack fluidStack = fluidTank.getFluid();
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            ResourceLocation still = props.getStillTexture(fluidStack);
            if (still != null) {
                AbstractTexture texture = minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
                if (texture instanceof TextureAtlas atlas) {
                    TextureAtlasSprite sprite = atlas.getSprite(still);

                    int color = props.getTintColor();
                    RenderSystem.setShaderColor(FastColor.ARGB32.red(color) / 255.0F, FastColor.ARGB32.green(color) / 255.0F,
                        FastColor.ARGB32.blue(color) / 255.0F, FastColor.ARGB32.alpha(color) / 255.0F);
                    RenderSystem.enableBlend();

                    int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
                    int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));
                    guiGraphics.blit(TextureAtlas.LOCATION_BLOCKS, x, y, width, height, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight, sprite.contents().width(), sprite.contents().height(),
                        atlasWidth, atlasHeight);
                    RenderSystem.setShaderColor(1, 1, 1, 1);

                }
            }
            renderToolTip(guiGraphics, mouseX, mouseY);
        }

        RenderSystem.disableDepthTest();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            guiGraphics.renderTooltip(displayOn.getMinecraft().font, Arrays.asList(getFluid.get().getFluid().getDisplayName().getVisualOrderText(),
                Component.literal(getFluid.get().getFluidAmount() + "mB").getVisualOrderText()), mouseX, mouseY);
        }
    }
}
