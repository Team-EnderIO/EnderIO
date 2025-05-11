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
        screen.addColorPicker(startX, startY + 20, ConduitLang.CONDUIT_CHANNEL,
                () -> dataAccess.getConnectionConfig().extractChannel(),
                value -> dataAccess.updateConnectionConfig(config -> config.withExtractChannel(value)));
    }

    @Override
    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<RedstoneConduitConnectionConfig> dataAccess) {
        super.createRightWidgets(screen, startX, startY, dataAccess);

        // Send channel
        screen.addColorPicker(startX, startY + 20, ConduitLang.CONDUIT_CHANNEL,
                () -> dataAccess.getConnectionConfig().insertChannel(),
                value -> dataAccess.updateConnectionConfig(config -> config.withInsertChannel(value)));

        // Strong signal
        screen.addCheckbox(startX, startY + 40, () -> dataAccess.getConnectionConfig().isStrongOutputSignal(),
                value -> dataAccess.updateConnectionConfig(config -> config.withIsStrongOutputSignal(value)));
    }

    @Override
    public boolean getLeftEnabled(RedstoneConduitConnectionConfig config) {
        return config.isExtract();
    }

    @Override
    public boolean getRightEnabled(RedstoneConduitConnectionConfig config) {
        return config.isInsert();
    }

    @Override
    protected RedstoneConduitConnectionConfig setLeftEnabled(RedstoneConduitConnectionConfig config,
            boolean isEnabled) {
        return config.withIsExtract(isEnabled);
    }

    @Override
    protected RedstoneConduitConnectionConfig setRightEnabled(RedstoneConduitConnectionConfig config,
            boolean isEnabled) {
        return config.withIsInsert(isEnabled);
    }

    @Override
    public void renderLabels(ConduitMenuDataAccess<RedstoneConduitConnectionConfig> dataAccess, GuiGraphics guiGraphics,
            int startX, int startY, Font font, int mouseX, int mouseY) {
        super.renderLabels(dataAccess, guiGraphics, startX, startY, font, mouseX, mouseY);

        guiGraphics.drawString(font, ConduitLang.CONDUIT_REDSTONE_SIGNAL_COLOR, startX + PADDED_SLOT_SIZE,
                startY + 20 + 4, 4210752, false);
        guiGraphics.drawString(font, ConduitLang.CONDUIT_REDSTONE_SIGNAL_COLOR,
                startX + RIGHT_START_X + PADDED_SLOT_SIZE, startY + 20 + 4, 4210752, false);

        guiGraphics.drawString(font, ConduitLang.CONDUIT_REDSTONE_STRONG_SIGNAL,
                startX + RIGHT_START_X + PADDED_SLOT_SIZE, startY + 40 + 4, 4210752, false);
    }
}
