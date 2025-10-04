package com.enderio.enderio.client.gui.screens.conduits.types;

import com.enderio.enderio.common.EnderIO;
import com.enderio.enderio.api.conduits.screen.ConduitMenuDataAccess;
import com.enderio.enderio.api.conduits.screen.ConduitScreenHelper;
import com.enderio.enderio.api.conduits.screen.IOConduitScreenType;
import com.enderio.enderio.common.conduits.type.energy.EnergyConduitConnectionConfig;
import com.enderio.enderio.common.lang.EIOLang;
import com.enderio.enderio.conduits.common.init.ConduitLang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnergyConduitScreenType extends IOConduitScreenType<EnergyConduitConnectionConfig> {

    private static final ResourceLocation ICON_INCREASE = EnderIO.rl("icon/increase");
    private static final ResourceLocation ICON_DECREASE = EnderIO.rl("icon/decrease");

    @Override
    public void renderLabels(ConduitMenuDataAccess<EnergyConduitConnectionConfig> dataAccess, GuiGraphics guiGraphics,
        int startX, int startY, Font font, int mouseX, int mouseY) {
        super.renderLabels(dataAccess, guiGraphics, startX, startY, font, mouseX, mouseY);

        String priority = String.valueOf(dataAccess.getConnectionConfig().priority());
        guiGraphics.drawString(font, ConduitLang.CONDUIT_PRIORITY, 22, 7 + 4 + 4 + 8 + 16 + 12, 0x000000, false);
        guiGraphics.drawString(font, priority, 90 - font.width(priority), 7 + 4 + 4 + 8 + 16 + 12, 0x000000, false);

        // TODO: Show redstone signal indicators using the extra NBT payload.
    }

    @Override
    public void createLeftWidgets(ConduitScreenHelper screen, int startX, int startY, ConduitMenuDataAccess<EnergyConduitConnectionConfig> dataAccess) {
        super.createLeftWidgets(screen, startX, startY, dataAccess);

        // Priority up/down
        screen.addIconButton(startX + 70, startY + 38, 9, 9, Component.empty(), ICON_INCREASE, () -> dataAccess
            .updateConnectionConfig(config -> config.withPriority(config.priority() + getIncrement())));
        screen.addIconButton(startX + 70, startY + 38 + 9, 9, 9, Component.empty(), ICON_DECREASE, () -> dataAccess
            .updateConnectionConfig(config -> config.withPriority(config.priority() - getIncrement())));
    }

    private int getIncrement() {
        if (Screen.hasControlDown()) {
            return 100;
        }

        if (Screen.hasShiftDown()) {
            return 10;
        }

        return 1;
    }

    @Override
    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<EnergyConduitConnectionConfig> dataAccess) {
        super.createRightWidgets(screen, startX, startY, dataAccess);

        // Redstone control
        var redstoneChannelWidget = screen.addColorPicker(startX + 16 + 4, startY + 20, ConduitLang.REDSTONE_CHANNEL,
                () -> dataAccess.getConnectionConfig().extractRedstoneChannel(),
                value -> dataAccess.updateConnectionConfig(config -> config.withExtractRedstoneChannel(value)));

        // Only show the redstone widget when redstone control is sensitive to signals.
        screen.addPreRenderAction(() -> redstoneChannelWidget.visible = dataAccess.getConnectionConfig()
                .extractRedstoneControl()
                .isRedstoneSensitive());

        screen.addRedstoneControlPicker(startX, startY + 20, EIOLang.REDSTONE_MODE,
                () -> dataAccess.getConnectionConfig().extractRedstoneControl(),
                value -> dataAccess.updateConnectionConfig(config -> config.withExtractRedstoneControl(value)));
    }

    @Override
    protected EnergyConduitConnectionConfig setLeftEnabled(EnergyConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsInsert(isEnabled);
    }

    @Override
    protected EnergyConduitConnectionConfig setRightEnabled(EnergyConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsExtract(isEnabled);
    }
}
