package com.enderio.core.client.gui.widgets;

import com.enderio.api.misc.Vector2i;
import com.enderio.core.EnderCore;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

enum MultiIconButtonType {
    PLUS_BUTTON,
    MINUS_BUTTON,
}

public class MultiIconButton extends AbstractButton {

    private final Supplier<Integer> getter;
    private final Consumer<Integer> setter;
    private static final ResourceLocation MULTI_ICON_TEXTURE = EnderCore.loc("textures/gui/multi_icon.png");
    private final ResourceLocation texture;
    private final MultiIconButtonType type;

    public MultiIconButton(ResourceLocation texture, Vector2i pos, Supplier<Integer> getter, Consumer<Integer> setter, MultiIconButtonType type) {
        this(texture, pos, 8, 8, getter, setter, Component.empty(), type);
    }

    public MultiIconButton(ResourceLocation texture, Vector2i pos, int width, int height, Supplier<Integer> getter, Consumer<Integer> setter, Component message, MultiIconButtonType type) {
        super(pos.x(), pos.y(), width, height, message);
        this.getter = getter;
        this.setter = setter;
        this.texture = texture;
        this.type = type;
    }

    @Override
    public void onPress() {
        int increment = 1;

        if (Screen.hasShiftDown()) {
            increment = 10;
        }
        if (Screen.hasControlDown()) {
            increment = 100;
        }
        if (type == MultiIconButtonType.PLUS_BUTTON) {
            setter.accept(getter.get() + increment);
        }
        else if (type == MultiIconButtonType.MINUS_BUTTON) {
            setter.accept(getter.get() - increment);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int textureX = 0;
        int textureY = 0;
        if (this.type == MultiIconButtonType.MINUS_BUTTON) {
            textureX = 16;
        }
        if (this.isHovered()) {
            textureY = 16;
        }
        guiGraphics.blit(texture, getX(), getY(), this.width, this.height, textureX, textureY, this.width * 2, this.height * 2, 256, 256);
    }

    public static MultiIconButton createAddButton(Vector2i pos, Supplier<Integer> getter, Consumer<Integer> setter) {
        return new MultiIconButton(MULTI_ICON_TEXTURE, pos, getter, setter, MultiIconButtonType.PLUS_BUTTON);
    }

    public static MultiIconButton createMinusButton(Vector2i pos, Supplier<Integer> getter, Consumer<Integer> setter) {
        return new MultiIconButton(MULTI_ICON_TEXTURE, pos, getter, setter, MultiIconButtonType.MINUS_BUTTON);
    }
}
