package com.enderio.core.client.gui.widgets;

import com.enderio.api.misc.Vector2i;
import com.enderio.core.EnderCore;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CheckBox extends AbstractButton {

    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;
    private final ResourceLocation texture;
    private static final ResourceLocation TEXTURE = EnderCore.loc("textures/gui/checkbox.png");

    public CheckBox(Vector2i pos, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this(TEXTURE, pos, getter, setter);
    }

    public CheckBox(ResourceLocation texture, Vector2i pos, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(pos.x(), pos.y(), 14, 14, Component.empty());
        this.getter = getter;
        this.setter = setter;
        this.texture = texture;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int textureX = 0;
        if (getter.get()) {
            textureX = 14;
        }
        if (isMouseOver(pMouseX, pMouseY)) {
            textureX += 28;
        }
        blit(pPoseStack, x, y, textureX, 0, this.width, this.height);
        if (getter.get()) {
            blit(pPoseStack, x, y, this.width, this.height, 0, 14, width*2, height*2, 256, 256);
        } else {
            blit(pPoseStack, x, y, this.width, this.height, 28, 14, width*2, height*2,256, 256);
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    @Override
    public void onPress() {
        setter.accept(!getter.get());
    }
}
