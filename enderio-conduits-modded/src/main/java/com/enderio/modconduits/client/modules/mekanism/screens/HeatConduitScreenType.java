package com.enderio.modconduits.client.modules.mekanism.screens;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.api.screen.ConduitMenuDataAccess;
import com.enderio.conduits.api.screen.ConduitScreenHelper;
import com.enderio.conduits.api.screen.IOConduitScreenType;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.modconduits.common.modules.mekanism.heat.HeatConduitConnectionConfig;

public class HeatConduitScreenType extends IOConduitScreenType<HeatConduitConnectionConfig> {
    @Override
    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<HeatConduitConnectionConfig> dataAccess) {
        super.createRightWidgets(screen, startX, startY, dataAccess);

        // Redstone control
        var redstoneChannelWidget = screen.addColorPicker(startX + 16 + 4, startY + 22, ConduitLang.REDSTONE_CHANNEL,
                () -> dataAccess.getConnectionConfig().receiveRedstoneChannel(),
                value -> dataAccess.updateConnectionConfig(config -> config.withReceiveRedstoneChannel(value)));

        // Only show the redstone widget when redstone control is sensitive to signals.
        screen.addPreRenderAction(() -> redstoneChannelWidget.visible = dataAccess.getConnectionConfig()
                .receiveRedstoneControl()
                .isRedstoneSensitive());

        screen.addRedstoneControlPicker(startX, startY + 22, EIOLang.REDSTONE_MODE,
                () -> dataAccess.getConnectionConfig().receiveRedstoneControl(),
                value -> dataAccess.updateConnectionConfig(config -> config.withReceiveRedstoneControl(value)));
    }

    @Override
    protected HeatConduitConnectionConfig setLeftEnabled(HeatConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsSend(isEnabled);
    }

    @Override
    protected HeatConduitConnectionConfig setRightEnabled(HeatConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsReceive(isEnabled);
    }
}
