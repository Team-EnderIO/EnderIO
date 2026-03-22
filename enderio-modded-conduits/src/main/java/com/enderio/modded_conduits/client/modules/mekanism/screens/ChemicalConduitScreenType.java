package com.enderio.modded_conduits.client.modules.mekanism.screens;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.screen.ConduitMenuDataAccess;
import com.enderio.enderio.api.conduits.screen.ConduitScreenHelper;
import com.enderio.enderio.api.conduits.screen.IOConduitScreenType;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.modded_conduits.common.modules.mekanism.chemical.ChemicalConduit;
import com.enderio.modded_conduits.common.modules.mekanism.chemical.ChemicalConduitConnectionConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class ChemicalConduitScreenType extends IOConduitScreenType<ChemicalConduitConnectionConfig> {
    private static final Identifier ICON_ROUND_ROBIN_ENABLED = EnderIO.rl("icon/round_robin_enabled");
    private static final Identifier ICON_ROUND_ROBIN_DISABLED = EnderIO.rl("icon/round_robin_disabled");
    private static final Identifier ICON_SELF_FEED_ENABLED = EnderIO.rl("icon/self_feed_enabled");
    private static final Identifier ICON_SELF_FEED_DISABLED = EnderIO.rl("icon/self_feed_disabled");

    @Override
    public void createLeftWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<ChemicalConduitConnectionConfig> dataAccess) {
        super.createLeftWidgets(screen, startX, startY, dataAccess);

        // Channel colors
        screen.addColorPicker(startX, startY + 20, ConduitLang.CHANNEL,
            () -> dataAccess.getConnectionConfig().insertChannel(),
            value -> dataAccess.updateConnectionConfig(config -> config.withInsertChannel(value)));

        screen.addFilterConfigureButton(startX + 1, startY + 82, ChemicalConduit.INSERT_FILTER_SLOT);
    }

    @Override
    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<ChemicalConduitConnectionConfig> dataAccess) {
        super.createRightWidgets(screen, startX, startY, dataAccess);

        // Channel colors
        screen.addColorPicker(startX, startY + 20, ConduitLang.CHANNEL,
            () -> dataAccess.getConnectionConfig().extractChannel(),
            value -> dataAccess.updateConnectionConfig(config -> config.withExtractChannel(value)));


        // TODO: Could be good fluid conduit features?
        /*
         * // Round robin screen.addToggleButton(90 + 16 + 4, 20, 16, 16,
         * ConduitLang.ROUND_ROBIN_ENABLED, ConduitLang.ROUND_ROBIN_DISABLED,
         * ICON_ROUND_ROBIN_ENABLED, ICON_ROUND_ROBIN_DISABLED, () ->
         * dataAccess.getConnectionConfig().isRoundRobin(), value ->
         * dataAccess.updateConnectionConfig(config -> config.withIsRoundRobin(value)));
         *
         * // Self feed screen.addToggleButton(90 + (16 + 4) * 2, 20, 16, 16,
         * ConduitLang.SELF_FEED_ENABLED, ConduitLang.SELF_FEED_DISABLED,
         * ICON_SELF_FEED_ENABLED, ICON_SELF_FEED_DISABLED, () ->
         * dataAccess.getConnectionConfig().isSelfFeed(), value ->
         * dataAccess.updateConnectionConfig(config -> config.withIsSelfFeed(value)));
         */

        // Redstone control
        var redstoneChannelWidget = screen.addColorPicker(startX + 16 + 4, startY + 40, ConduitLang.REDSTONE_CHANNEL,
                () -> dataAccess.getConnectionConfig().extractRedstoneChannel(),
                value -> dataAccess.updateConnectionConfig(config -> config.withExtractRedstoneChannel(value)));

        // Only show the redstone widget when redstone control is sensitive to signals.
        screen.addPreRenderAction(() -> redstoneChannelWidget.visible = dataAccess.getConnectionConfig()
                .extractRedstoneControl()
                .isRedstoneSensitive());

        screen.addRedstoneControlPicker(startX, startY + 40, EIOCommonLang.REDSTONE_MODE,
                () -> dataAccess.getConnectionConfig().extractRedstoneControl(),
                value -> dataAccess.updateConnectionConfig(config -> config.withExtractRedstoneControl(value)));

        screen.addFilterConfigureButton(startX + 1, startY + 82, ChemicalConduit.EXTRACT_FILTER_SLOT);

        // TODO: Show redstone signal indicators using the extra NBT payload.
    }

    @Override
    protected ChemicalConduitConnectionConfig setLeftEnabled(ChemicalConduitConnectionConfig config,
            boolean isEnabled) {
        return config.withIsInsert(isEnabled);
    }

    @Override
    protected ChemicalConduitConnectionConfig setRightEnabled(ChemicalConduitConnectionConfig config,
            boolean isEnabled) {
        return config.withIsExtract(isEnabled);
    }
}
