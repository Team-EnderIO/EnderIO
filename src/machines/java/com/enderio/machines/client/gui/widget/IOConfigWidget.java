package com.enderio.machines.client.gui.widget;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.common.util.Vector2i;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IOConfigWidget<U extends EIOScreen<?>> extends AbstractWidget {
    private final U addedOn;
    private static final int rendererHeight = 80;

    private final ResourceLocation resLoc = EnderIO.loc("textures/gui/40/widgetsv2.png");

    private boolean isRendererVisible = false;
    private final IOConfigRenderer<?> rendererWidget;
    private final Supplier<Boolean> playerSlotsHidden;
    private final Consumer<Boolean> shouldHidePlayerSlots;
    private final Rect2i bounds;

    // Rebase on ToggleButton if possible
    public IOConfigWidget(U addedOn, int x, int y, int width, int height, MachineBlockEntity block, Supplier<Boolean> playerSlotsHidden,
        Consumer<Boolean> shouldHidePlayerSlots) {
        super(x, y, width, height, Component.empty());
        this.addedOn = addedOn;
        this.playerSlotsHidden = playerSlotsHidden;
        isRendererVisible = playerSlotsHidden.get();
        this.shouldHidePlayerSlots = shouldHidePlayerSlots;
        this.bounds = calcRendererBounds(addedOn.getGuiLeft(), addedOn.getGuiTop(), addedOn.getXSize(), addedOn.getYSize());
        this.rendererWidget = new IOConfigRenderer<>(addedOn, bounds, block);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        if (isRendererVisible) {
            Minecraft mc = Minecraft.getInstance();
            Window window = mc.getWindow();

            int vpx = (int) ((addedOn.getGuiLeft() + 5) * window.getGuiScale());
            int vpy = (int) ((addedOn.getGuiTop() + 4) * window.getGuiScale());
            int w = (int) (bounds.getWidth() * window.getGuiScale());
            int h = (int) (bounds.getHeight() * window.getGuiScale());
            rendererWidget.render(pPoseStack, pMouseX, pMouseY, pPartialTick, new Rect2i(vpx, vpy, w, h));
        }
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Vector2i pos = new Vector2i(x, y);
        addedOn.renderSimpleArea(pPoseStack, pos, pos.add(new Vector2i(width, height)));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resLoc);
        RenderSystem.enableDepthTest();
        blit(pPoseStack, this.x, this.y, 80, 176, this.width, this.height, 256, 256);

        if (this.isHovered) {
            renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        shouldHidePlayerSlots.accept(!isRendererVisible);
        isRendererVisible = playerSlotsHidden.get();
    }

    private Rect2i calcRendererBounds(int leftPos, int topPos, int imageWidth, int imageHeight) {
        return new Rect2i(leftPos + 5, topPos + imageHeight - rendererHeight - 5, imageWidth - 10, rendererHeight);
    }

}
