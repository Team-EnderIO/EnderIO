package com.enderio.machines.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.common.blocks.soul_engine.SoulEngineMenu;
import com.enderio.machines.common.souldata.EngineSoul;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;
import java.util.Optional;

public class SoulEngineScreen extends MachineScreen<SoulEngineMenu> {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/screen/soul_engine.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public SoulEngineScreen(SoulEngineMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableOnly(new FluidStackWidget(80 + leftPos, 21 + topPos, 16, 47, menu::getFluidTank));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        EntityType<?> entityType = getMenu().getBlockEntity().getEntityType();
        if (entityType != null) {
            String name = entityType.getDescription().getString();
            guiGraphics.drawString(font, name, imageWidth / 2f - font.width(name) / 2f, 10, 4210752, false);

            EngineSoul.ENGINE.matches(entityType).ifPresent(data -> {
                double burnRate = menu.getBlockEntity().getBurnRate();
                float genRate = menu.getBlockEntity().getGenerationRate();
                guiGraphics.drawString(font, FORMAT.format((int) (data.powerpermb() * genRate) * burnRate / data.tickpermb()) + " µI/t", imageWidth / 2f + 12, 40, 4210752,
                    false);
                guiGraphics.drawString(font, FORMAT.format(data.tickpermb() / burnRate) + " t/mb", imageWidth / 2f + 12, 50, 4210752,
                        false);
                guiGraphics.drawString(font, (int) (data.powerpermb() * genRate) + " µI/mb", imageWidth / 2f + 12, 60,
                        4210752, false);
            });
        }

        super.renderLabels(guiGraphics, pMouseX, pMouseY);
    }
}
