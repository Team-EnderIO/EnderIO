package com.enderio.conduits.client.gui;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.IEnderScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConduitSelectionButton extends AbstractButton {
    private final IConduitType type;
    private final Supplier<IConduitType> getter;
    private final Consumer<IConduitType> setter;
    public ConduitSelectionButton(int pX, int pY, IConduitType type, Supplier<IConduitType> getter, Consumer<IConduitType> setter) {
        super(pX, pY, 21, 24, Component.empty());
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    protected boolean isValidClickButton(int pButton) {
        return super.isValidClickButton(pButton) && getter.get() != type;
    }

    @Override
    public void onPress() {
        setter.accept(type);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ConduitScreen.TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(pPoseStack, x, y, 227, 0, this.width, this.height);
        if (getter.get() == type) {
            blit(pPoseStack, x-3, y, 224, 0, 3, this.height);
        }
        IEnderScreen.renderIcon(pPoseStack, new Vector2i(x, y).add(3, 6), type);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    public IConduitType getType() {
        return type;
    }
}
