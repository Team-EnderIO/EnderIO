package com.enderio.machines.client.gui.widget.ioconfig;

import com.enderio.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.common.util.Vector2i;
import com.enderio.machines.common.menu.MachineMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.function.Supplier;

public class IOConfigButton<U extends EIOScreen<?>, T extends AbstractWidget> extends AbstractWidget {
    private static final int RENDERER_HEIGHT = 80;
    public static final ResourceLocation IOCONFIG = EnderIO.loc("textures/gui/icons/io_config.png");
    private final IOConfigWidget<U> configRenderer;
    private final ImageButton neighbourButton;
    private final Supplier<Boolean> playerInvVisible;
    private final Function<Boolean, Boolean> setPlayerInvVisible;
    private final U addedOn;

    public IOConfigButton(U addedOn, int x, int y, int width, int height, MachineMenu<?> menu, Function<AbstractWidget, T> addRenderableWidget, Font font) {
        super(x, y, width, height, Component.empty());
        this.addedOn = addedOn;
        this.playerInvVisible = menu::getPlayerInvVisible;
        this.setPlayerInvVisible = menu::setPlayerInvVisible;

        var show = !playerInvVisible.get();
        configRenderer = new IOConfigWidget<>(addedOn, addedOn.getGuiLeft() + 5, addedOn.getGuiTop() + addedOn.getYSize() - RENDERER_HEIGHT - 5,
            addedOn.getXSize() - 10, RENDERER_HEIGHT, menu.getBlockEntity().getBlockPos(), font);
        configRenderer.visible = show;
        addRenderableWidget.apply(configRenderer);

        neighbourButton = new ImageButton(addedOn.getGuiLeft() + addedOn.getXSize() - 5 - 16, addedOn.getGuiTop() + addedOn.getYSize() - 5 - 16, 16, 16, 16, 0,
            0, IOCONFIG, 48, 32, (k) -> configRenderer.toggleNeighbourVisibility(),
            (k, pPoseStack, pMouseX, pMouseY) -> addedOn.renderTooltip(pPoseStack, EIOLang.TOGGLE_NEIGHBOUR, pMouseX, pMouseY), Component.empty());
        neighbourButton.visible = show;
        addRenderableWidget.apply(neighbourButton);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Vector2i pos = new Vector2i(x, y);
        addedOn.renderSimpleArea(pPoseStack, pos, pos.add(new Vector2i(width, height)));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, IOCONFIG);
        RenderSystem.enableDepthTest();
        blit(pPoseStack, this.x, this.y, 0, 0, this.width, this.height, 48, 32);

        if (this.isHovered) {
            renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }

    @Override
    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        addedOn.renderTooltip(pPoseStack, EIOLang.IOCONFIG, pMouseX, pMouseY);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        var state = !setPlayerInvVisible.apply(!playerInvVisible.get()); // toggle the variable and set state to opposite of it
        configRenderer.visible = state;
        neighbourButton.visible = state;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

}
