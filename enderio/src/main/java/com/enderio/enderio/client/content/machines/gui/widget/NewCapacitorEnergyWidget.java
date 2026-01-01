package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.foundation.energy.EnergyStorageInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NewCapacitorEnergyWidget extends NewEnergyWidget {
    private static final Identifier ENERGY_BAR_ERROR_SPRITE = EnderIO.rl("widget/energy_bar_error");

    private final Supplier<Boolean> cap;

    public NewCapacitorEnergyWidget(int x, int y, Supplier<EnergyStorageInfo> storageSupplier,
            Supplier<Boolean> cap) {
        super(x, y, storageSupplier);
        this.cap = cap;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!cap.get()) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENERGY_BAR_ERROR_SPRITE, x, y, width, height);

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
        list.add(MachinesLang.NOCAP_TITLE.withStyle(ChatFormatting.DARK_AQUA));
        String[] split = MachinesLang.NOCAP_DESC.getString().split("\n");
        for (String s : split) {
            list.add(Component.literal(s.stripLeading().stripTrailing()));
        }

        Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(0, 0); //TODO can't push Z 1
        guiGraphics.setComponentTooltipForNextFrame(minecraft.font, list, mouseX, mouseY);
        pose.popMatrix();
    }
}
