package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.core.client.gui.widgets.EIOWidget;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.io.energy.ILargeMachineEnergyStorage;
import com.enderio.enderio.foundation.io.energy.IMachineEnergyStorage;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Supplier;

public class EnergyWidget extends EIOWidget {

    protected static final ResourceLocation WIDGETS = EnderIO.rl("textures/gui/widgets.png");

    private final Supplier<IMachineEnergyStorage> storageSupplier;

    public EnergyWidget(int x, int y, int width, int height, Supplier<IMachineEnergyStorage> storageSupplier) {
        super(x, y, width, height);
        this.storageSupplier = storageSupplier;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Don't bother if we have no energy capacity, protects from divide by zero's when there's no capacitor.
        IMachineEnergyStorage storage = storageSupplier.get();
        if (storage.getMaxEnergyStored() <= 0) {
            return;
        }

        //TODO blend depth pipeline
        float filledVolume = (float)(getEnergyStored(storage) / (double) getMaxEnergyStored(storage));
        int renderableHeight = (int)(filledVolume * height);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0, height-16);
        for (int i = 0; i < Math.ceil(renderableHeight / 16f); i++) {
            int drawingHeight = Math.min(16, renderableHeight - 16*i);
            int notDrawingHeight = 16 - drawingHeight;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, x, y + notDrawingHeight, 0, 0, 128 + notDrawingHeight, width, drawingHeight, 256, 256);
            guiGraphics.pose().translate(0,-16);
        }

        guiGraphics.pose().popMatrix();

        renderToolTip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            Minecraft minecraft = Minecraft.getInstance();

            IMachineEnergyStorage storage = storageSupplier.get();

            NumberFormat fmt = NumberFormat.getInstance(Locale.ENGLISH);
            guiGraphics.setTooltipForNextFrame(minecraft.font,
                TooltipUtil.withArgs(EIOCommonLang.ENERGY_AMOUNT, fmt.format(getEnergyStored(storage)) + "/" + fmt.format(
               getMaxEnergyStored(storage))), mouseX, mouseY);
        }
    }

    private static long getEnergyStored(IMachineEnergyStorage storage) {
        if (storage instanceof ILargeMachineEnergyStorage largeStorage) {
            return largeStorage.getLargeEnergyStored();
        }

        return storage.getEnergyStored();
    }

    private static long getMaxEnergyStored(IMachineEnergyStorage storage) {
        if (storage instanceof ILargeMachineEnergyStorage largeStorage) {
            return largeStorage.getLargeMaxEnergyStored();
        }

        return storage.getMaxEnergyStored();
    }
}

