package com.enderio.conduits.client.gui.screen.types;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.api.screen.ConduitMenuDataAccess;
import com.enderio.conduits.api.screen.ConduitScreenHelper;
import com.enderio.conduits.api.screen.ConduitScreenType;
import com.enderio.conduits.api.screen.IOConduitScreenType;
import com.enderio.conduits.common.conduit.type.item.ItemConduitConnectionConfig;
import com.enderio.conduits.common.init.ConduitLang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ItemConduitScreenType extends IOConduitScreenType<ItemConduitConnectionConfig> {

    private static final ResourceLocation ICON_ROUND_ROBIN_ENABLED = EnderIO.loc("icon/round_robin_enabled");
    private static final ResourceLocation ICON_ROUND_ROBIN_DISABLED = EnderIO.loc("icon/round_robin_disabled");
    private static final ResourceLocation ICON_SELF_FEED_ENABLED = EnderIO.loc("icon/self_feed_enabled");
    private static final ResourceLocation ICON_SELF_FEED_DISABLED = EnderIO.loc("icon/self_feed_disabled");

    @Override
    public void createLeftWidgets(ConduitScreenHelper screen, int startX, int startY, ConduitMenuDataAccess<ItemConduitConnectionConfig> dataAccess) {
        super.createLeftWidgets(screen, startX, startY, dataAccess);

        // Channel color
        screen.addColorPicker(startX, startY + 20, ConduitLang.CONDUIT_CHANNEL, () -> dataAccess.getConnectionConfig().sendColor(),
            value -> dataAccess.updateConnectionConfig(config -> config.withSendColor(value)));
    }

    @Override
    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY, ConduitMenuDataAccess<ItemConduitConnectionConfig> dataAccess) {
        super.createRightWidgets(screen, startX, startY, dataAccess);

        // Channel color
        screen.addColorPicker(startX, startY + 20, ConduitLang.CONDUIT_CHANNEL,
            () -> dataAccess.getConnectionConfig().receiveColor(),
            value -> dataAccess.updateConnectionConfig(config -> config.withReceiveColor(value)));

        // Round robin
        screen.addToggleButton(startX + 16 + 4, startY + 20, 16, 16, ConduitLang.ROUND_ROBIN_ENABLED,
            ConduitLang.ROUND_ROBIN_DISABLED, ICON_ROUND_ROBIN_ENABLED, ICON_ROUND_ROBIN_DISABLED,
            () -> dataAccess.getConnectionConfig().isRoundRobin(),
            value -> dataAccess.updateConnectionConfig(config -> config.withIsRoundRobin(value)));

        // Self feed
        screen.addToggleButton(startX + (16 + 4) * 2, startY + 20, 16, 16, ConduitLang.SELF_FEED_ENABLED,
            ConduitLang.SELF_FEED_DISABLED, ICON_SELF_FEED_ENABLED, ICON_SELF_FEED_DISABLED,
            () -> dataAccess.getConnectionConfig().isSelfFeed(),
            value -> dataAccess.updateConnectionConfig(config -> config.withIsSelfFeed(value)));

        // Redstone control
        var redstoneChannelWidget = screen.addColorPicker(startX + 16 + 4, startY + 40, ConduitLang.REDSTONE_CHANNEL,
            () -> dataAccess.getConnectionConfig().receiveRedstoneChannel(),
            value -> dataAccess.updateConnectionConfig(config -> config.withReceiveRedstoneChannel(value)));

        // Only show the redstone widget when redstone control is sensitive to signals.
        screen.addPreRenderAction(() -> redstoneChannelWidget.visible = dataAccess.getConnectionConfig()
            .receiveRedstoneControl()
            .isRedstoneSensitive());

        screen.addRedstoneControlPicker(startX, startY + 40, EIOLang.REDSTONE_MODE,
            () -> dataAccess.getConnectionConfig().receiveRedstoneControl(),
            value -> dataAccess.updateConnectionConfig(config -> config.withReceiveRedstoneControl(value)));

        // TODO: Show redstone signal indicators using the extra NBT payload.
    }

    @Override
    protected ItemConduitConnectionConfig setLeftEnabled(ItemConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsSend(isEnabled);
    }

    @Override
    protected ItemConduitConnectionConfig setRightEnabled(ItemConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsReceive(isEnabled);
    }
}
