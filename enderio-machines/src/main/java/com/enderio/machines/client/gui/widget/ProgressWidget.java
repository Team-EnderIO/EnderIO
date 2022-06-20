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
    public enum Direction {
        BOTTOM_UP,
        TOP_DOWN,
        LEFT_RIGHT
    }

    private Supplier<Float> getter;

    private final Screen screen;
    private final int u;
    private final int v;
    private final Direction direction;

    public ProgressWidget(Screen screen, Supplier<Float> getter, int x, int y, int width, int height, int u, int v, Direction direction) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.screen = screen;
        this.getter = getter;
        this.u = u;
        this.v = v;
        this.direction = direction;
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
        float progress = getter.get();

        int yOffset = 0;
        int xOffset = 0;
        int width = this.width;
        int height = this.height;

        switch (direction) {
        case BOTTOM_UP -> {
            yOffset = (int)(this.height * (1.0f - progress));
            height = (int) (this.height * progress);
        }
        case TOP_DOWN -> {
            height = (int) (this.height * progress);
        }
        case LEFT_RIGHT -> {
            width = (int) (this.width * progress);
        }
        }

        poseStack.pushPose();
        blit(poseStack, x + xOffset, y + yOffset, u, v + yOffset, width, height);
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
