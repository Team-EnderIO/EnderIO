package com.enderio.machines.client.gui.widget.ioconfig;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.common.util.Vector2i;
import com.enderio.machines.common.menu.MachineMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IOConfigButton<U extends EIOScreen<?>, T extends Widget & GuiEventListener & NarratableEntry> extends AbstractWidget {
    private static final int RENDERER_HEIGHT = 80;
    private final ResourceLocation resLoc = EnderIO.loc("textures/gui/40/widgetsv2.png");
    private final IOConfigWidget<U> configRenderer;
    private final Supplier<Boolean> playerSlotsHidden;
    private final Consumer<Boolean> shouldHidePlayerSlots;
    private final U addedOn;

    public IOConfigButton(U addedOn, int x, int y, int width, int height, MachineMenu<?> menu, Function<T, T> addRenderableWidget, Font font) {
        super(x, y, width, height, Component.empty());
        this.addedOn = addedOn;
        this.playerSlotsHidden = menu::arePlayerSlotsHidden;
        this.shouldHidePlayerSlots = menu::hidePlayerSlots;
        configRenderer = new IOConfigWidget<>(addedOn, addedOn.getGuiLeft() + 5, addedOn.getGuiTop() + addedOn.getYSize() - RENDERER_HEIGHT - 5,
            addedOn.getXSize() - 10, RENDERER_HEIGHT, menu.getBlockEntity().getBlockPos(), font);
        configRenderer.visible = this.playerSlotsHidden.get();
        addRenderableWidget.apply((T) configRenderer);
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
    public void onClick(double pMouseX, double pMouseY) {
        boolean toggle = !playerSlotsHidden.get();
        shouldHidePlayerSlots.accept(toggle);
        configRenderer.visible = playerSlotsHidden.get();
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

}
