package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.foundation.energy.EnergyStorageInfo;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CapacitorEnergyWidget extends EnergyWidget {
    public static final ItemStack CAPACITOR = new ItemStack(EIOItems.BASIC_CAPACITOR.get());
    private final Supplier<Boolean> cap;

    public CapacitorEnergyWidget(int x, int y, int width, int height, Supplier<EnergyStorageInfo> storageSupplier, Supplier<Boolean> cap) {
        super(x, y, width, height, storageSupplier);
        this.cap = cap;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!cap.get()) {
            renderCapacitor(graphics);

            if (isHoveredOrFocused()) {
                renderCapacitorTooltip(graphics, mouseX, mouseY);
            }
            return;
        }
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
    }

    public void renderCapacitorTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        List<Component> list = new ArrayList<>();
        list.add(MachinesLang.NOCAP_TITLE.withStyle(ChatFormatting.DARK_AQUA));
        String[] split = MachinesLang.NOCAP_DESC.getString().split("\n");
        for (String s :split) {
            list.add(Component.literal(s.stripLeading().stripTrailing()));
        }

        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(0,0); //TODO Z push not possible
        graphics.setComponentTooltipForNextFrame(minecraft.font, list, mouseX, mouseY); //TODO does this render?
        pose.popMatrix();
    }

    public void renderCapacitor(GuiGraphicsExtractor graphics) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        
        long tick = level.getGameTime() % 90;

        int heightModifier = (int) Math.round(Math.sin(level.getGameTime() * 0.05) * 12);
        graphics.fakeItem(CAPACITOR, x - 4, y + height/2 - 8 + heightModifier);

        //TODO blend pipeline + ghost item
        //noinspection IntegerDivisionInFloatingPointContext
        graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, x, y + height/2 + 6, 160 + tick / 10 * 9, 128, width, height, 256, 256);
        graphics.fakeItem(CAPACITOR, x - 4, y + height/2 + 25);

    }
}
