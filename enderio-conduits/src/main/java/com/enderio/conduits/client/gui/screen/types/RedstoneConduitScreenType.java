package com.enderio.conduits.client.gui.screen.types;

import com.enderio.conduits.api.screen.ConduitMenuDataAccess;
import com.enderio.conduits.api.screen.ConduitScreenHelper;
import com.enderio.conduits.api.screen.IOConduitScreenType;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitConnectionConfig;
import com.enderio.conduits.common.init.ConduitLang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class RedstoneConduitScreenType extends IOConduitScreenType<RedstoneConduitConnectionConfig> {

    public RedstoneConduitScreenType() {
        // TODO: Should be ctor params.
        leftTitle = ConduitLang.CONDUIT_INPUT;
        rightTitle = ConduitLang.CONDUIT_OUTPUT;
    }

    @Override
    public void createLeftWidgets(ConduitScreenHelper screen, int startX, int startY,
        ConduitMenuDataAccess<RedstoneConduitConnectionConfig> dataAccess) {
        super.createLeftWidgets(screen, startX, startY, dataAccess);

        // Send channel
        screen.addColorPicker(startX, startY + 20, ConduitLang.CONDUIT_CHANNEL, () -> dataAccess.getConnectionConfig().receiveColor(),
            value -> dataAccess.updateConnectionConfig(config -> config.withReceiveColor(value)));
    }

    @Override
    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY,
        ConduitMenuDataAccess<RedstoneConduitConnectionConfig> dataAccess) {
        super.createRightWidgets(screen, startX, startY, dataAccess);

        // Send channel
        screen.addColorPicker(startX, startY + 20, ConduitLang.CONDUIT_CHANNEL, () -> dataAccess.getConnectionConfig().sendColor(),
            value -> dataAccess.updateConnectionConfig(config -> config.withSendColor(value)));

        // Strong signal
        screen.addCheckbox(startX, startY + 40, () -> dataAccess.getConnectionConfig().isStrongOutputSignal(),
            value -> dataAccess.updateConnectionConfig(config -> config.withIsStrongOutputSignal(value)));
    }

    @Override
    public boolean getLeftEnabled(RedstoneConduitConnectionConfig config) {
        return config.isReceive();
    }

    @Override
    public boolean getRightEnabled(RedstoneConduitConnectionConfig config) {
        return config.isSend();
    }

    @Override
    protected RedstoneConduitConnectionConfig setLeftEnabled(RedstoneConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsReceive(isEnabled);
    }

    @Override
    protected RedstoneConduitConnectionConfig setRightEnabled(RedstoneConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsSend(isEnabled);
    }

    @Override
    public void renderLabels(GuiGraphics guiGraphics, int startX, int startY, Font font, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, startX, startY, font, mouseX, mouseY);

        guiGraphics.drawString(font, ConduitLang.CONDUIT_REDSTONE_SIGNAL_COLOR, startX + PADDED_SLOT_SIZE, startY + 20 + 4, 4210752, false);
        guiGraphics.drawString(font, ConduitLang.CONDUIT_REDSTONE_SIGNAL_COLOR, startX + RIGHT_START_X + PADDED_SLOT_SIZE, startY + 20 + 4, 4210752, false);

        guiGraphics.drawString(font, ConduitLang.CONDUIT_REDSTONE_STRONG_SIGNAL, startX + RIGHT_START_X + PADDED_SLOT_SIZE, startY + 40 + 4, 4210752, false);
    }
}
