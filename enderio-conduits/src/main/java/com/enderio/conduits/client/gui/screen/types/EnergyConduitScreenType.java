package com.enderio.conduits.client.gui.screen.types;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.api.screen.ConduitMenuDataAccess;
import com.enderio.conduits.api.screen.ConduitScreenHelper;
import com.enderio.conduits.api.screen.ConduitScreenType;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitConnectionConfig;
import com.enderio.conduits.common.init.ConduitLang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class EnergyConduitScreenType extends ConduitScreenType<EnergyConduitConnectionConfig> {

    @Override
    public void createWidgets(ConduitScreenHelper screen, int guiLeft, int guiTop,
            ConduitMenuDataAccess<EnergyConduitConnectionConfig> dataAccess) {
        // Add insert/extract checkboxes.
        screen.addCheckbox(guiLeft + 0, guiTop + 0, () -> dataAccess.getConnectionConfig().isSend(),
                value -> dataAccess.updateConnectionConfig(config -> config.withIsSend(value)));

        screen.addCheckbox(guiLeft + 90, guiTop + 0, () -> dataAccess.getConnectionConfig().isReceive(),
                value -> dataAccess.updateConnectionConfig(config -> config.withIsReceive(value)));

        // Redstone control
        var redstoneChannelWidget = screen.addColorPicker(guiLeft + 90 + 16 + 4, guiTop + 20, ConduitLang.REDSTONE_CHANNEL,
                () -> dataAccess.getConnectionConfig().receiveRedstoneChannel(),
                value -> dataAccess.updateConnectionConfig(config -> config.withReceiveRedstoneChannel(value)));

        // Only show the redstone widget when redstone control is sensitive to signals.
        screen.addPreRenderAction(() -> redstoneChannelWidget.visible = dataAccess.getConnectionConfig()
                .receiveRedstoneControl()
                .isRedstoneSensitive());

        screen.addRedstoneControlPicker(guiLeft + 90, guiTop + 20, EIOLang.REDSTONE_MODE,
                () -> dataAccess.getConnectionConfig().receiveRedstoneControl(),
                value -> dataAccess.updateConnectionConfig(config -> config.withReceiveRedstoneControl(value)));

        // TODO: Show redstone signal indicators using the extra NBT payload.
    }

    @Override
    public void renderLabels(GuiGraphics guiGraphics, int startX, int startY, Font font, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, startX, startY, font, mouseX, mouseY);

        guiGraphics.drawString(font, ConduitLang.CONDUIT_INSERT, startX + 16 + 2, startY + 4, 4210752, false);
        guiGraphics.drawString(font, ConduitLang.CONDUIT_EXTRACT, startY + 90 + 16 + 2, startY + 4, 4210752, false);
    }
}
