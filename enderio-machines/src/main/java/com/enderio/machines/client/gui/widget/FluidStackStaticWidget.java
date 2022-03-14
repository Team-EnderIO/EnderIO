package com.enderio.machines.client.gui.widget;

import java.util.Arrays;
import java.util.function.Supplier;

import com.enderio.base.client.gui.widgets.EIOWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidStackStaticWidget extends EIOWidget {

    private final Screen displayOn;
    private final Supplier<FluidTank> getFluid;

    public FluidStackStaticWidget(Screen displayOn, Supplier<FluidTank> getFluid, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.displayOn = displayOn;
        this.getFluid = getFluid;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        FluidTank fluidTank = getFluid.get();
        if (!fluidTank.isEmpty()) {
            FluidStack fluidStack = fluidTank.getFluid();
            ResourceLocation still = fluidStack.getFluid().getAttributes().getStillTexture(fluidStack);
            if (still != null) {
                AbstractTexture texture = minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
                if (texture instanceof TextureAtlas atlas) {
                    TextureAtlasSprite sprite = atlas.getSprite(still);
                    RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

                    int color = fluidStack.getFluid().getAttributes().getColor();
                    RenderSystem.setShaderColor(
                        FastColor.ARGB32.red(color) / 255.0F,
                        FastColor.ARGB32.green(color) / 255.0F,
                        FastColor.ARGB32.blue(color) / 255.0F,
                        FastColor.ARGB32.alpha(color) / 255.0F);
                    RenderSystem.enableBlend();

                    int atlasWidth = (int)(sprite.getWidth() / (sprite.getU1() - sprite.getU0()));
                    int atlasHeight = (int)(sprite.getHeight() / (sprite.getV1() - sprite.getV0()));
                    //blit(poseStack, x, y, displayOn.getBlitOffset(), sprite.getU0()*atlasWidth, sprite.getV0()*atlasHeight, sprite.getWidth(), sprite.getHeight(), atlasWidth, atlasHeight);
                    
                    blit(poseStack, x, y, width, height, sprite.getU0()*atlasWidth, sprite.getV0()*atlasHeight, sprite.getWidth(), sprite.getHeight(), atlasWidth, atlasHeight);
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                    
                }
            }
            renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            displayOn.renderTooltip(poseStack, Arrays.asList(getFluid.get().getFluid().getDisplayName().getVisualOrderText(), new TextComponent(getFluid.get().getFluidAmount() + "mB").getVisualOrderText()), mouseX, mouseY);
        }
    }
}
