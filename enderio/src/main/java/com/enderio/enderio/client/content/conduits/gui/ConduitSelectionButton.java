package com.enderio.enderio.client.content.conduits.gui;

import com.enderio.enderio.api.conduits.Conduit;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConduitSelectionButton extends AbstractButton {
    private final int conduitButtonIndex;
    private final Supplier<Holder<Conduit<?, ?>>> currentConduit;
    private final Supplier<List<Holder<Conduit<?, ?>>>> conduitListGetter;
    private final Consumer<Integer> onPressed;

    public ConduitSelectionButton(int pX, int pY, int conduitButtonIndex,
            Supplier<Holder<Conduit<?, ?>>> currentConduit, Supplier<List<Holder<Conduit<?, ?>>>> conduitListGetter,
            Consumer<Integer> onPressed) {
        super(pX, pY, 21, 24, Component.empty());
        this.conduitButtonIndex = conduitButtonIndex;
        this.currentConduit = currentConduit;
        this.conduitListGetter = conduitListGetter;
        this.onPressed = onPressed;
    }

    @Nullable
    private Holder<Conduit<?, ?>> getConduit() {
        var list = conduitListGetter.get();
        if (conduitButtonIndex >= 0 && conduitButtonIndex < list.size()) {
            return list.get(conduitButtonIndex);
        }

        return null;
    }

    @Override
    protected boolean isValidClickButton(int pButton) {
        var conduit = getConduit();
        return super.isValidClickButton(pButton) && conduit != null && conduit != currentConduit.get();
    }

    @Override
    public void onPress() {
        onPressed.accept(conduitButtonIndex);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        var conduit = getConduit();
        if (conduit == null) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        guiGraphics.blit(RenderType::guiTextured, ConduitScreen.TEXTURE, getX(), getY(), 227, 0, this.width, this.height, 256, 256);
        if (currentConduit.get() == conduit) {
            guiGraphics.blit(RenderType::guiTextured, ConduitScreen.TEXTURE, getX() - 3, getY(), 224, 0, 3, this.height, 256, 256);
        }

        // TODO: This shouldn't be a hard-coded path.
        ResourceLocation iconLocation = MissingTextureAtlasSprite.getLocation();
        ResourceLocation conduitKey = conduit.unwrapKey().map(ResourceKey::location).orElse(null);
        if (conduitKey != null) {
            iconLocation = ResourceLocation.fromNamespaceAndPath(conduitKey.getNamespace(),
                    "conduit_icon/" + conduitKey.getPath());
        }

        guiGraphics.blitSprite(RenderType::guiTextured, iconLocation, getX() + 3, getY() + 6, 12, 12);

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
