package com.enderio.machines.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.client.gui.widget.EIOCommonWidgets;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.common.blocks.niard.NiardMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class NiardScreen extends MachineScreen<NiardMenu> {


    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/screen/niard.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
            menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
            menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableOnly(new FluidStackWidget(80 + leftPos, 21 + topPos, 16, 47, menu::getFluidTank));

        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 6 - 16, topPos + 2 * 16 + 2,
            EIOLang.HIDE_RANGE, EIOLang.SHOW_RANGE, menu::isRangeVisible,
            (ignore) -> handleButtonPress(NiardMenu.VISIBILITY_BUTTON_ID)));

        addRenderableWidget(EIOCommonWidgets.createRangeIncrease(leftPos + imageWidth - 2 * 16, topPos + 2 + 16 * 2,
            (b) -> handleButtonPress(NiardMenu.INCREASE_BUTTON_ID)));
        addRenderableWidget(EIOCommonWidgets.createRangeDecrease(leftPos + imageWidth - 2 * 16, topPos + 2 + 16 * 2 + 8,
            (b) -> handleButtonPress(NiardMenu.DECREASE_BUTTON_ID)));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));
    }

    public NiardScreen(NiardMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        FluidStack fluidStack = menu.getFluidTank().contents();

        renderFluidBg(guiGraphics, fluidStack, leftPos + 112, topPos + 28, 39, 56);
    }

    private void renderFluidBg(GuiGraphics guiGraphics, FluidStack fluidStack, int x, int y, int width, int height) {
        if (fluidStack.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();

        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation still = props.getStillTexture(fluidStack);

        if (still != null) {
            AbstractTexture texture = minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
            if (texture instanceof TextureAtlas atlas) {
                TextureAtlasSprite sprite = atlas.getSprite(still);

                int color = props.getTintColor();
                RenderSystem.setShaderColor(
                    FastColor.ARGB32.red(color) / 255.0F,
                    FastColor.ARGB32.green(color) / 255.0F,
                    FastColor.ARGB32.blue(color) / 255.0F,
                    FastColor.ARGB32.alpha(color) / 255.0F
                );

                RenderSystem.enableBlend();

                int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
                int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));

                float uOffset = sprite.getU0() * atlasWidth;
                float vOffset = sprite.getV0() * atlasHeight;
                int spriteWidth = (int)((sprite.getU1() - sprite.getU0()) * atlasWidth);
                int spriteHeight = (int)((sprite.getV1() - sprite.getV0()) * atlasHeight);

                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();

                poseStack.translate(x, y, 0);

                float scaleX = (float) width / (float) spriteWidth;
                float scaleY = (float) height / (float) spriteHeight;
                poseStack.scale(scaleX, scaleY, 1.0F);

                guiGraphics.blit(
                    TextureAtlas.LOCATION_BLOCKS,
                    0, 0,
                    0,
                    uOffset, vOffset,
                    spriteWidth, spriteHeight,
                    atlasWidth, atlasHeight
                );

                poseStack.popPose();

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableBlend();

                guiGraphics.blit(BG_TEXTURE, x, y, 200, 0, width, height);
            }
        }
    }
}
