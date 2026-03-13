package com.enderio.enderio.client.content.conduits.gui.screen_type;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.screen.ConduitMenuDataAccess;
import com.enderio.enderio.api.conduits.screen.ConduitScreenHelper;
import com.enderio.enderio.api.conduits.screen.IOConduitScreenType;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.content.conduits.type.energy.EnergyConduitConnectionConfig;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;

public class EnergyConduitScreenType extends IOConduitScreenType<EnergyConduitConnectionConfig> {

    private static final Identifier ICON_INCREASE = EnderIO.id("icon/increase");
    private static final Identifier ICON_DECREASE = EnderIO.id("icon/decrease");

    @Override
    public void renderLabels(ConduitMenuDataAccess<EnergyConduitConnectionConfig> dataAccess, GuiGraphicsExtractor graphics,
        int startX, int startY, Font font, int mouseX, int mouseY) {
        super.renderLabels(dataAccess, graphics, startX, startY, font, mouseX, mouseY);

        String priority = String.valueOf(dataAccess.getConnectionConfig().priority());
        graphics.text(font, ConduitLang.PRIORITY, 22, 7 + 4 + 4 + 8 + 16 + 12, CommonColors.DARK_GRAY, false);
        graphics.text(font, priority, 90 - font.width(priority), 7 + 4 + 4 + 8 + 16 + 12, CommonColors.DARK_GRAY, false);

        // TODO: Show redstone signal indicators using the extra NBT payload.
    }

    @Override
    public void createLeftWidgets(ConduitScreenHelper screen, int startX, int startY, ConduitMenuDataAccess<EnergyConduitConnectionConfig> dataAccess) {
        super.createLeftWidgets(screen, startX, startY, dataAccess);

        // Priority up/down
        screen.addIconButton(startX + 70, startY + 38, 9, 9, Component.empty(), ICON_INCREASE, input -> dataAccess
            .updateConnectionConfig(config -> config.withPriority(config.priority() + getIncrement(input))));
        screen.addIconButton(startX + 70, startY + 38 + 9, 9, 9, Component.empty(), ICON_DECREASE, input -> dataAccess
            .updateConnectionConfig(config -> config.withPriority(config.priority() - getIncrement(input))));
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

        screen.addRedstoneControlPicker(startX, startY + 20, EIOCommonLang.REDSTONE_MODE,
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
