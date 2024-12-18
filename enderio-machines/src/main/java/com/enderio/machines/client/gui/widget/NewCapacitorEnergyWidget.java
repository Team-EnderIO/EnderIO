package com.enderio.machines.client.gui.widget;

import com.enderio.EnderIOBase;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NewCapacitorEnergyWidget extends NewEnergyWidget {
    private static final ResourceLocation ENERGY_BAR_ERROR_SPRITE = EnderIOBase.loc("widget/energy_bar_error");

    private final Supplier<Boolean> cap;

    public NewCapacitorEnergyWidget(int x, int y, Supplier<IMachineEnergyStorage> storageSupplier,
            Supplier<Boolean> cap) {
        super(x, y, storageSupplier);
        this.cap = cap;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!cap.get()) {
            guiGraphics.blitSprite(ENERGY_BAR_ERROR_SPRITE, x, y, width, height);

            if (isHoveredOrFocused()) {
                renderCapacitorTooltip(guiGraphics, mouseX, mouseY);
            }

            return;
        }

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void renderCapacitorTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        List<Component> list = new ArrayList<>();
        list.add(EIOLang.NOCAP_TITLE.withStyle(ChatFormatting.DARK_AQUA));
        String[] split = EIOLang.NOCAP_DESC.getString().split("\n");
        for (String s : split) {
            list.add(Component.literal(s.stripLeading().stripTrailing()));
        }

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 1);
        guiGraphics.renderComponentTooltip(minecraft.font, list, mouseX, mouseY);
        pose.popPose();
    }
}
