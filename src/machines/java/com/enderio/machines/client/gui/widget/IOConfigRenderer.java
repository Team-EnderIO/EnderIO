package com.enderio.machines.client.gui.widget;

import com.enderio.core.client.gui.screen.IEnderScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;

public class IOConfigRenderer<S extends Screen & IEnderScreen> {

    private final S addedOn;
    private final Rect2i bounds;

    public IOConfigRenderer(S addedOn, Rect2i bounds) {
        this.addedOn = addedOn;
        this.bounds = bounds;
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            GuiComponent.fill(pPoseStack, bounds.getX(), bounds.getY(), bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), 0xFF000000);
    }
}
