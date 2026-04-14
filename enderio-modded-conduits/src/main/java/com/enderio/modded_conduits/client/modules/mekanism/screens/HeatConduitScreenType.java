//package com.enderio.modded_conduits.client.modules.mekanism.screens;
//
//import com.enderio.enderio.api.conduits.screen.ConduitMenuDataAccess;
//import com.enderio.enderio.api.conduits.screen.ConduitScreenHelper;
//import com.enderio.enderio.api.conduits.screen.IOConduitScreenType;
//import com.enderio.enderio.content.conduits.ConduitLang;
//import com.enderio.enderio.foundation.lang.EIOCommonLang;
//import com.enderio.modded_conduits.common.modules.mekanism.heat.HeatConduitConnectionConfig;
//
//public class HeatConduitScreenType extends IOConduitScreenType<HeatConduitConnectionConfig> {
//    @Override
//    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY,
//            ConduitMenuDataAccess<HeatConduitConnectionConfig> dataAccess) {
//        super.createRightWidgets(screen, startX, startY, dataAccess);
//
//        // Redstone control
//        var redstoneChannelWidget = screen.addColorPicker(startX + 16 + 4, startY + 22, ConduitLang.REDSTONE_CHANNEL,
//                () -> dataAccess.getConnectionConfig().extractRedstoneChannel(),
//                value -> dataAccess.updateConnectionConfig(config -> config.withExtractRedstoneChannel(value)));
//
//        // Only show the redstone widget when redstone control is sensitive to signals.
//        screen.addPreRenderAction(() -> redstoneChannelWidget.visible = dataAccess.getConnectionConfig()
//                .extractRedstoneControl()
//                .isRedstoneSensitive());
//
//        screen.addRedstoneControlPicker(startX, startY + 22, EIOCommonLang.REDSTONE_MODE,
//                () -> dataAccess.getConnectionConfig().extractRedstoneControl(),
//                value -> dataAccess.updateConnectionConfig(config -> config.withExtractRedstoneControl(value)));
//    }
//
//    @Override
//    protected HeatConduitConnectionConfig setLeftEnabled(HeatConduitConnectionConfig config, boolean isEnabled) {
//        return config.withIsInsert(isEnabled);
//    }
//
//    @Override
//    protected HeatConduitConnectionConfig setRightEnabled(HeatConduitConnectionConfig config, boolean isEnabled) {
//        return config.withIsExtract(isEnabled);
//    }
//}
