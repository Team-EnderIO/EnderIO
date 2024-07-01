package com.enderio.conduits.client.gui;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConduitSelectionButton extends AbstractButton {
    private final Holder<ConduitType<?, ?, ?>> type;
    private final Supplier<Holder<ConduitType<?, ?, ?>>> getter;
    private final Consumer<Holder<ConduitType<?, ?, ?>>> setter;

    public ConduitSelectionButton(int pX, int pY, Holder<ConduitType<?, ?, ?>> type, Supplier<Holder<ConduitType<?, ?, ?>>> getter, Consumer<Holder<ConduitType<?, ?, ?>>> setter) {
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

        ResourceLocation iconLocation = MissingTextureAtlasSprite.getLocation();
        ResourceLocation conduitTypeKey = type.unwrapKey().map(ResourceKey::location).orElse(null);
        if (conduitTypeKey != null) {
            iconLocation = ResourceLocation.fromNamespaceAndPath(conduitTypeKey.getNamespace(), "conduit_icon/" + conduitTypeKey.getPath());
        }

        guiGraphics.blitSprite(iconLocation, getX() + 3, getY() + 6, 12, 12);

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    public Holder<ConduitType<?, ?, ?>> getType() {
        return type;
    }
}
