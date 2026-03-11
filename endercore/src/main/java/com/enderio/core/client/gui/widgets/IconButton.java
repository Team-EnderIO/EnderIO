package com.enderio.core.client.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class IconButton extends EnderButton {

    private final ResourceLocation texture;
    private final int texU;
    private final int texV;
    private final int texW;
    private final int texH;

    private final Runnable onPress;

    public IconButton(int x, int y, int width, int height, ResourceLocation texture, int texU, int texV, int texW, int texH, @Nullable Component tooltip,
            Runnable onPress) {
        super(x, y, width, height, Component.empty());
        this.texture = texture;
        this.texU = texU;
        this.texV = texV;
        this.texW = texW;
        this.texH = texH;

        this.onPress = onPress;

        if (tooltip != null) {
            setTooltip(Tooltip.create(tooltip));
        }
    }

    @Override
    public void onPress() {
        this.onPress.run();
    }

    @Override
    public void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blit(texture, getX(), getY(), texU, texV, this.width, this.height, texW, texH);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
