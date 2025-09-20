package com.enderio.conduits.api.screen;

import com.enderio.conduits.api.connection.config.IOConnectionConfig;
import com.enderio.conduits.client.gui.screen.ConduitScreen;
import com.enderio.conduits.common.init.ConduitLang;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

/**
 * Default implementation of a conduit screen type for IO connections.
 * Adds checkboxes and sided titles.
 */
@ApiStatus.Experimental
public abstract class IOConduitScreenType<U extends IOConnectionConfig> extends ConduitScreenType<U> {

    // Rows of 9, counts slot width and it's outline edge.
    // TODO: Better name.
    protected static final int RIGHT_START_X = PADDED_SLOT_SIZE * 5;

    // Titles default to perfect position to be alongside a checkbox.
    protected final int leftTitleX = PADDED_SLOT_SIZE;
    protected final int leftTitleY = 4;
    protected Component leftTitle = ConduitLang.CONDUIT_INSERT;

    protected final int rightTitleX = RIGHT_START_X + PADDED_SLOT_SIZE;
    protected final int rightTitleY = 4;
    protected Component rightTitle = ConduitLang.CONDUIT_EXTRACT;

    @Override
    protected void createWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<U> dataAccess) {
        createLeftWidgets(screen, startX, startY, dataAccess);
        createRightWidgets(screen, startX + RIGHT_START_X, startY, dataAccess);

        // TODO: *could* implement sanity checks to ensure widgets are not outside their
        // bounds? Might be nice static-time check, even if we only do it in dev envs?
    }

    public void createLeftWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<U> dataAccess) {
        screen.addCheckbox(startX, startY, () -> getLeftEnabled(dataAccess.getConnectionConfig()),
                value -> dataAccess.updateConnectionConfig(config -> setLeftEnabled(config, value)));
    }

    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<U> dataAccess) {
        screen.addCheckbox(startX, startY, () -> getRightEnabled(dataAccess.getConnectionConfig()),
                value -> dataAccess.updateConnectionConfig(config -> setRightEnabled(config, value)));
    }

    public boolean getLeftEnabled(U config) {
        return config.isInsert();
    }

    public boolean getRightEnabled(U config) {
        return config.isExtract();
    }

    /**
     * Unless you changed the order of the titles, this should edit isSend.
     */
    protected abstract U setLeftEnabled(U config, boolean isEnabled);

    /**
     * Unless you changed the order of the titles, this should edit isReceive.
     */
    protected abstract U setRightEnabled(U config, boolean isEnabled);

    @Override
    public void renderLabels(ConduitMenuDataAccess<U> dataAccess, GuiGraphics guiGraphics, int startX, int startY,
            Font font, int mouseX, int mouseY) {
        super.renderLabels(dataAccess, guiGraphics, startX, startY, font, mouseX, mouseY);

        // TODO: This should be a sprite.
        guiGraphics.blit(ConduitScreen.TEXTURE, startX + (WIDTH / 2), startY, 255, 0, 1, 97);

        guiGraphics.drawString(font, leftTitle, startX + leftTitleX, startY + leftTitleY, 4210752, false);
        guiGraphics.drawString(font, rightTitle, startX + rightTitleX, startY + rightTitleY, 4210752, false);
    }
}
