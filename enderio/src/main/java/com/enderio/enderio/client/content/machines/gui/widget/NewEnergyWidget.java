package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.core.client.gui.widgets.EIOWidget;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.energy.EnergyStorageInfo;
import com.enderio.enderio.foundation.io.energy.ILargeMachineEnergyStorage;
import com.enderio.enderio.foundation.io.energy.IMachineEnergyStorage;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Supplier;

public class NewEnergyWidget extends EIOWidget {

    protected static final Identifier ENERGY_BAR_FILL_SPRITE = EnderIO.rl("widget/energy_bar_fill");

    private final Supplier<EnergyStorageInfo> storageSupplier;

    public NewEnergyWidget(int x, int y, Supplier<EnergyStorageInfo> storageSupplier) {
        super(x, y, 18, 52);
        this.storageSupplier = storageSupplier;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Don't bother if we have no energy capacity, protects from divide by zero's when there's no capacitor.
        EnergyStorageInfo storage = storageSupplier.get();
        if (storage.capacity() <= 0) {
            return;
        }

        //TODO blend depth pipeline
        float filledVolume = (float)(storage.energy() / (double) storage.capacity());
        int renderableHeight = (int)(filledVolume * height);
        int hiddenHeight = height - renderableHeight;

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENERGY_BAR_FILL_SPRITE, width, height, 0, hiddenHeight, x, y + hiddenHeight, width, renderableHeight);

        renderToolTip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            Minecraft minecraft = Minecraft.getInstance();

            EnergyStorageInfo storage = storageSupplier.get();

            NumberFormat fmt = NumberFormat.getInstance(Locale.ENGLISH);
            guiGraphics.setTooltipForNextFrame(minecraft.font,
                TooltipUtil.withArgs(EIOCommonLang.ENERGY_AMOUNT, fmt.format(storage.energy()) + "/" + fmt.format(
               storage.capacity())), mouseX, mouseY);
        }
    }
}

