package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.CapacitorEnergyWidget;
import com.enderio.enderio.client.foundation.widgets.EIOCommonWidgets;
import com.enderio.enderio.client.foundation.widgets.FluidStackWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.niard.NiardMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix3x2fStack;

public class NiardScreen extends MachineScreen<NiardMenu> {


    public static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/screen/niard.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
            menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
            menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        addRenderableOnly(new FluidStackWidget(80 + leftPos, 21 + topPos, 16, 47, menu::getFluidTank));

        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 6 - 16, topPos + 2 * 16 + 2,
            MachinesLang.HIDE_RANGE, MachinesLang.SHOW_RANGE, menu::isRangeVisible,
            (ignore) -> handleButtonPress(NiardMenu.VISIBILITY_BUTTON_ID)));

        addRenderableWidget(EIOCommonWidgets.createRangeIncrease(leftPos + imageWidth - 2 * 16, topPos + 2 + 16 * 2,
            (b) -> handleButtonPress(NiardMenu.INCREASE_BUTTON_ID)));
        addRenderableWidget(EIOCommonWidgets.createRangeDecrease(leftPos + imageWidth - 2 * 16, topPos + 2 + 16 * 2 + 8,
            (b) -> handleButtonPress(NiardMenu.DECREASE_BUTTON_ID)));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));
    }

    public NiardScreen(NiardMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        // TODO: 1.21.4: Hardcoded 256x256
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        FluidStack fluidStack = menu.getFluidTank().contents();

        renderFluidBg(guiGraphics, fluidStack, leftPos + 112, topPos + 28, 39, 56);
    }

    private void renderFluidBg(GuiGraphics guiGraphics, FluidStack fluidStack, int x, int y, int width, int height) {
        if (fluidStack.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();

        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        Identifier still = props.getStillTexture(fluidStack);

        if (still != null) {
            AbstractTexture texture = minecraft.getTextureManager().getTexture(AtlasIds.BLOCKS);
            if (texture instanceof TextureAtlas atlas) {
                TextureAtlasSprite sprite = atlas.getSprite(still);

                int color = props.getTintColor();

                int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
                int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));

                float uOffset = sprite.getU0() * atlasWidth;
                float vOffset = sprite.getV0() * atlasHeight;
                int spriteWidth = (int)((sprite.getU1() - sprite.getU0()) * atlasWidth);
                int spriteHeight = (int)((sprite.getV1() - sprite.getV0()) * atlasHeight);

                Matrix3x2fStack poseStack = guiGraphics.pose();
                poseStack.pushMatrix();

                poseStack.translate(x, y);

                float scaleX = (float) width / (float) spriteWidth;
                float scaleY = (float) height / (float) spriteHeight;
                poseStack.scale(scaleX, scaleY);

                guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    TextureAtlas.LOCATION_BLOCKS,
                    0, 0,
                    uOffset, vOffset,
                    spriteWidth, spriteHeight,
                    atlasWidth, atlasHeight,
                    color
                );

                poseStack.popMatrix();

                // TODO: 1.21.4: Hardcoded 256x256
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, x, y, 200, 0, width, height, 256, 256);
            }
        }
    }
}
