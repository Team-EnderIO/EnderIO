package com.enderio.machines.client.gui.widget;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CapacitorEnergyWidget extends EnergyWidget{
    private final Supplier<Boolean> cap;

    public CapacitorEnergyWidget(Screen displayOn, Supplier<IMachineEnergyStorage> storageSupplier, Supplier<Boolean> cap, int x, int y, int width, int height) {
        super(displayOn, storageSupplier, x, y, width, height);
        this.cap = cap;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!cap.get()) {
            renderCapacitorTooltip(guiGraphics);
            return;
        }
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void renderCapacitorTooltip(GuiGraphics guiGraphics) {
        List<Component> list = new ArrayList<>();
        list.add(EIOLang.NOCAP_TITLE.withStyle(ChatFormatting.DARK_AQUA));
        String[] split = EIOLang.NOCAP_DESC.getString().split("\n");
        list.add(Component.literal(split[0].stripTrailing()));
        list.add(Component.literal(split[1].stripLeading()));
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(0,0,1);
        guiGraphics.renderComponentTooltip(displayOn.getMinecraft().font, list, x + 4, y + 26);
        pose.popPose();
    }
}
