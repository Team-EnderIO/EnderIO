package com.enderio.machines.client.gui.widget;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CapacitorEnergyWidget extends EnergyWidget {
    public static final ItemStack CAPACITOR = new ItemStack(EIOItems.BASIC_CAPACITOR.get());
    private final Supplier<Boolean> cap;

    public CapacitorEnergyWidget(int x, int y, int width, int height, Supplier<IMachineEnergyStorage> storageSupplier, Supplier<Boolean> cap) {
        super(x, y, width, height, storageSupplier);
        this.cap = cap;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!cap.get()) {
            renderCapacitor(guiGraphics);

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
        for (String s :split) {
            list.add(Component.literal(s.stripLeading().stripTrailing()));
        }

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(0,0,1);
        guiGraphics.renderComponentTooltip(minecraft.font, list, mouseX, mouseY);
        pose.popPose();
    }

    public void renderCapacitor(GuiGraphics guiGraphics) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        
        long tick = level.getGameTime() % 90;

        int heightModifier = (int) Math.round(Math.sin(level.getGameTime() * 0.05) * 12);
        guiGraphics.renderFakeItem(CAPACITOR, x - 4, y + height/2 - 8 + heightModifier);

        //noinspection IntegerDivisionInFloatingPointContext
        guiGraphics.blit(WIDGETS, x, y + height/2 + 6, 0, 160 + tick / 10 * 9, 128, width, height, 256, 256);
        RenderSystem.setShaderColor(1,1,1, 100/255f);
        RenderSystem.enableBlend();
        guiGraphics.renderFakeItem(CAPACITOR, x - 4, y + height/2 + 25);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1,1,1, 1);

    }
}
