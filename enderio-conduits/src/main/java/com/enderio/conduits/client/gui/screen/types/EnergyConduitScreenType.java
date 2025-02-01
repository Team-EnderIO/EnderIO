package com.enderio.conduits.client.gui.screen.types;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.api.screen.ConduitMenuDataAccess;
import com.enderio.conduits.api.screen.ConduitScreenHelper;
import com.enderio.conduits.api.screen.ConduitScreenType;
import com.enderio.conduits.api.screen.IOConduitScreenType;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitConnectionConfig;
import com.enderio.conduits.common.init.ConduitLang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class EnergyConduitScreenType extends IOConduitScreenType<EnergyConduitConnectionConfig> {

    @Override
    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY, ConduitMenuDataAccess<EnergyConduitConnectionConfig> dataAccess) {
        super.createRightWidgets(screen, startX, startY, dataAccess);

        // Redstone control
        var redstoneChannelWidget = screen.addColorPicker(startX + 16 + 4, startY + 20, ConduitLang.REDSTONE_CHANNEL,
            () -> dataAccess.getConnectionConfig().receiveRedstoneChannel(),
            value -> dataAccess.updateConnectionConfig(config -> config.withReceiveRedstoneChannel(value)));

        // Only show the redstone widget when redstone control is sensitive to signals.
        screen.addPreRenderAction(() -> redstoneChannelWidget.visible = dataAccess.getConnectionConfig()
            .receiveRedstoneControl()
            .isRedstoneSensitive());

        screen.addRedstoneControlPicker(startX, startY + 20, EIOLang.REDSTONE_MODE,
            () -> dataAccess.getConnectionConfig().receiveRedstoneControl(),
            value -> dataAccess.updateConnectionConfig(config -> config.withReceiveRedstoneControl(value)));

        // TODO: Show redstone signal indicators using the extra NBT payload.
    }

    @Override
    protected EnergyConduitConnectionConfig setLeftEnabled(EnergyConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsSend(isEnabled);
    }

    @Override
    protected EnergyConduitConnectionConfig setRightEnabled(EnergyConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsReceive(isEnabled);
    }
}
