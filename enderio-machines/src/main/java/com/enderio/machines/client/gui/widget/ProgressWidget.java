package com.enderio.machines.client.gui.widget;

import com.enderio.base.common.util.TooltipUtil;
import com.enderio.machines.common.lang.MachineLang;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.function.Supplier;

// Will need adapting for arrows, will do that as part of SAG mill
public class ProgressWidget extends AbstractWidget {
    private Supplier<Float> getter;

    private final Screen screen;
    private final int u;
    private final int v;

    public ProgressWidget(Screen screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.screen = screen;
        this.getter = getter;
        this.u = u;
        this.v = v;
    }

    // Stop the click sound
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return false;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        poseStack.pushPose();

        float progress = getter.get();
        int yOffset = (int)(14 * (1.0f - progress));
        int height = (int) (14 * progress);

        blit(poseStack, x, y + yOffset, u, v + yOffset, width, height);

        poseStack.popPose();

        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
        if (isHovered && isActive()) {
            screen.renderTooltip(poseStack, TooltipUtil.withArgs(MachineLang.PROGRESS_TOOLTIP, (int) (getter.get() * 100)), mouseX, mouseY);
        }
    }
}
