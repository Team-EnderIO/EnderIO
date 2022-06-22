package com.enderio.machines.client.gui.widget;

import com.enderio.base.client.gui.widgets.EIOWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.TooltipUtil;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.text.NumberFormat;
import java.util.function.Supplier;

public class EnergyWidget extends EIOWidget {

    // TODO: Will need some way of displaying no tooltip and instead asking for a capacitor on non-simple machines.

    private static final ResourceLocation WIDGETS = EIOMachines.loc("textures/gui/widgets.png");

    private final Screen displayOn;
    private final Supplier<IMachineEnergyStorage> storageSupplier;

    public EnergyWidget(Screen displayOn, Supplier<IMachineEnergyStorage> storageSupplier, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.displayOn = displayOn;
        this.storageSupplier = storageSupplier;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Don't bother if we have no energy capacity, protects from divide by zero's when there's no capacitor.
        IMachineEnergyStorage storage = storageSupplier.get();
        if (storage.getMaxEnergyStored() <= 0)
            return;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, WIDGETS);

        float filledVolume = storage.getEnergyStored() / (float) storage.getMaxEnergyStored();
        int renderableHeight = (int)(filledVolume * height);

        poseStack.pushPose();
        poseStack.translate(0, height-16, 0);
        for (int i = 0; i < Math.ceil(renderableHeight / 16f); i++) {
            int drawingHeight = Math.min(16, renderableHeight - 16*i);
            int notDrawingHeight = 16 - drawingHeight;
            blit(poseStack, x, y + notDrawingHeight, displayOn.getBlitOffset(), 0, 128 + notDrawingHeight, width, drawingHeight, 256, 256);
            poseStack.translate(0,-16, 0);
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        poseStack.popPose();

        renderToolTip(poseStack, mouseX, mouseY);
    }

    public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            IMachineEnergyStorage storage = storageSupplier.get();

            NumberFormat fmt = NumberFormat.getInstance();
            displayOn.renderTooltip(poseStack, TooltipUtil.withArgs(EIOLang.ENERGY_AMOUNT, fmt.format(storage.getEnergyStored()) + "/" + fmt.format(
                storage.getMaxEnergyStored())), mouseX, mouseY);
        }
    }
}

