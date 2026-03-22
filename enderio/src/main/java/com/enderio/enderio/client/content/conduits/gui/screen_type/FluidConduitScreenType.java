package com.enderio.enderio.client.content.conduits.gui.screen_type;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.screen.ConduitMenuDataAccess;
import com.enderio.enderio.api.conduits.screen.ConduitScreenHelper;
import com.enderio.enderio.api.conduits.screen.IOConduitScreenType;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.content.conduits.type.fluid.FluidConduit;
import com.enderio.enderio.content.conduits.type.fluid.FluidConduitConnectionConfig;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class FluidConduitScreenType extends IOConduitScreenType<FluidConduitConnectionConfig> {

    private static final Identifier ICON_ROUND_ROBIN_ENABLED = EnderIO.id("icon/round_robin_enabled");
    private static final Identifier ICON_ROUND_ROBIN_DISABLED = EnderIO.id("icon/round_robin_disabled");
    private static final Identifier ICON_SELF_FEED_ENABLED = EnderIO.id("icon/self_feed_enabled");
    private static final Identifier ICON_SELF_FEED_DISABLED = EnderIO.id("icon/self_feed_disabled");

    private static final Identifier ICON_INCREASE = EnderIO.id("icon/increase");
    private static final Identifier ICON_DECREASE = EnderIO.id("icon/decrease");

    @Override
    public void renderLabels(ConduitMenuDataAccess<FluidConduitConnectionConfig> dataAccess, GuiGraphicsExtractor graphics, int startX, int startY, Font font,
        int mouseX, int mouseY) {
        super.renderLabels(dataAccess, graphics, startX, startY, font, mouseX, mouseY);

        String priority = String.valueOf(dataAccess.getConnectionConfig().insertPriority());
        graphics.text(font, ConduitLang.PRIORITY, 22, 7 + 4 + 4 + 8 + 16 + 12, CommonColors.DARK_GRAY, false);
        graphics.text(font, priority, 90 - font.width(priority), 7 + 4 + 4 + 8 + 16 + 12, CommonColors.DARK_GRAY, false);
    }

    private int getIncrement(InputWithModifiers input) {
        if (input.hasControlDown()) {
            return 100;
        }

        if (input.hasShiftDown()) {
            return 10;
        }

        return 1;
    }

    @Override
    public void createLeftWidgets(ConduitScreenHelper screen, int startX, int startY, ConduitMenuDataAccess<FluidConduitConnectionConfig> dataAccess) {
        super.createLeftWidgets(screen, startX, startY, dataAccess);

        // Channel colors
        screen.addColorPicker(startX, startY + 20, ConduitLang.CHANNEL, () -> dataAccess.getConnectionConfig().insertChannel(),
            value -> dataAccess.updateConnectionConfig(config -> config.withInsertChannel(value)));

        screen.addFilterConfigureButton(startX + 1, startY + 82, FluidConduit.INSERT_FILTER_SLOT);

        // Priority up/down
        screen.addIconButton(startX + 70, startY + 38, 9, 9, Component.empty(), ICON_INCREASE,
            input -> dataAccess.updateConnectionConfig(config -> config.withPriority(config.insertPriority() + getIncrement(input))));
        screen.addIconButton(startX + 70, startY + 38 + 9, 9, 9, Component.empty(), ICON_DECREASE,
            input -> dataAccess.updateConnectionConfig(config -> config.withPriority(config.insertPriority() - getIncrement(input))));
    }

    @Override
    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY, ConduitMenuDataAccess<FluidConduitConnectionConfig> dataAccess) {
        super.createRightWidgets(screen, startX, startY, dataAccess);

        // Channel colors
        screen.addColorPicker(startX, startY + 20, ConduitLang.CHANNEL, () -> dataAccess.getConnectionConfig().extractChannel(),
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
        screen.addPreRenderAction(() -> redstoneChannelWidget.visible = dataAccess.getConnectionConfig().extractRedstoneControl().isRedstoneSensitive());

        screen.addRedstoneControlPicker(startX, startY + 40, EIOCommonLang.REDSTONE_MODE, () -> dataAccess.getConnectionConfig().extractRedstoneControl(),
            value -> dataAccess.updateConnectionConfig(config -> config.withExtractRedstoneControl(value)));

        screen.addFilterConfigureButton(startX + 1, startY + 82, FluidConduit.EXTRACT_FILTER_SLOT);

        // TODO: Show redstone signal indicators using the extra NBT payload.
    }

    @Override
    protected FluidConduitConnectionConfig setLeftEnabled(FluidConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsInsert(isEnabled);
    }

    @Override
    protected FluidConduitConnectionConfig setRightEnabled(FluidConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsExtract(isEnabled);
    }
}
