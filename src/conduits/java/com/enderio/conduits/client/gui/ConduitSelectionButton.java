package com.enderio.conduits.client.gui;

import com.enderio.api.conduit.ConduitType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConduitSelectionButton extends AbstractButton {
    private final ConduitType<?, ?, ?> type;
    private final Supplier<ConduitType<?, ?, ?>> getter;
    private final Consumer<ConduitType<?, ?, ?>> setter;

    public ConduitSelectionButton(int pX, int pY, ConduitType<?, ?, ?> type, Supplier<ConduitType<?, ?, ?>> getter, Consumer<ConduitType<?, ?, ?>> setter) {
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
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        guiGraphics.blit(ConduitScreen.TEXTURE, getX(), getY(), 227, 0, this.width, this.height);
        if (getter.get() == type) {
            guiGraphics.blit(ConduitScreen.TEXTURE, getX() - 3, getY(), 224, 0, 3, this.height);
        }

        TextureAtlasSprite iconSprite = ConduitIconTextureManager.INSTANCE.get(type);
        guiGraphics.blit(getX() + 3, getY() + 6, 0, 12, 12, iconSprite);

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    public ConduitType<?, ?, ?> getType() {
        return type;
    }
}
