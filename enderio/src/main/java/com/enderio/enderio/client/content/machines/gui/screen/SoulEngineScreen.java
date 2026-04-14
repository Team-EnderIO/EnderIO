package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.CapacitorEnergyWidget;
import com.enderio.enderio.client.foundation.widgets.FluidStackWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.soul_engine.SoulEngineMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.foundation.souldata.EngineSoul;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;

public class SoulEngineScreen extends MachineScreen<SoulEngineMenu> {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    public static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/screen/soul_engine.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public SoulEngineScreen(SoulEngineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        addRenderableOnly(new FluidStackWidget(80 + leftPos, 21 + topPos, 16, 47, menu::getFluidTank));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        EntityType<?> entityType = getMenu().getBlockEntity().getEntityType();
        if (entityType != null) {
            String name = entityType.getDescription().getString();
            graphics.text(font, name, (int) (imageWidth / 2f - font.width(name) / 2f), 10, CommonColors.DARK_GRAY, false);

            EngineSoul.RELOAD_LISTENER.matches(entityType).ifPresent(data -> {
                double burnRate = menu.getBlockEntity().getBurnRate();
                float genRate = menu.getBlockEntity().getGenerationRate();
                graphics.text(font, FORMAT.format((int) (data.powerpermb() * genRate) * burnRate / data.tickpermb()) + " µI/t", (int) (imageWidth / 2f + 12), 40, 4210752,
                    false);
                graphics.text(font, FORMAT.format(data.tickpermb() / burnRate) + " t/mb", (int) (imageWidth / 2f + 12), 50, CommonColors.DARK_GRAY,
                        false);
                graphics.text(font, (int) (data.powerpermb() * genRate) + " µI/mb", (int) (imageWidth / 2f + 12), 60,
                    CommonColors.DARK_GRAY, false);
            });
        }

        super.extractLabels(graphics, mouseX, mouseY);
    }
}
