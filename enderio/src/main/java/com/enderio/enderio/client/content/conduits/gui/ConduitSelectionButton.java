package com.enderio.enderio.client.content.conduits.gui;

import com.enderio.enderio.api.conduits.Conduit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConduitSelectionButton extends AbstractButton {
    private final int conduitButtonIndex;
    private final Supplier<Holder<Conduit<?, ?>>> currentConduit;
    private final Supplier<List<Holder<Conduit<?, ?>>>> conduitListGetter;
    private final Consumer<Integer> onPressed;

    public ConduitSelectionButton(int x, int y, int conduitButtonIndex,
            Supplier<Holder<Conduit<?, ?>>> currentConduit, Supplier<List<Holder<Conduit<?, ?>>>> conduitListGetter,
            Consumer<Integer> onPressed) {
        super(x, y, 21, 24, Component.empty());
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
    protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
        return super.isValidClickButton(buttonInfo) && getConduit() != currentConduit.get();
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        onPressed.accept(conduitButtonIndex);
    }

    @Override
    public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var conduit = getConduit();
        if (conduit == null) {
            return;
        }

        // TODO: 1.21.8: is this needed?
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.enableDepthTest();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ConduitScreen.TEXTURE, getX(), getY(), 227, 0, this.width, this.height, 256, 256);
        if (currentConduit.get() == conduit) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ConduitScreen.TEXTURE, getX() - 3, getY(), 224, 0, 3, this.height, 256, 256);
        }

        // TODO: This shouldn't be a hard-coded path.
        Identifier iconLocation = MissingTextureAtlasSprite.getLocation();
        Identifier conduitKey = conduit.unwrapKey().map(ResourceKey::identifier).orElse(null);
        if (conduitKey != null) {
            iconLocation = Identifier.fromNamespaceAndPath(conduitKey.getNamespace(),
                    "conduit_icon/" + conduitKey.getPath());
        }

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, iconLocation, getX() + 3, getY() + 6, 12, 12);

//        RenderSystem.disableDepthTest();
//        RenderSystem.disableBlend();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
