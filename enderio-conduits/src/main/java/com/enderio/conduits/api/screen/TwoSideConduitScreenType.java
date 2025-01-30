package com.enderio.conduits.api.screen;

import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.client.gui.screen.ConduitScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class TwoSideConduitScreenType<U extends ConnectionConfig> extends ConduitScreenType<U> {

    // Rows of 9, counts slot width and it's outline edge.
    // TODO: Better name.
    protected static final int RIGHT_START_X = PADDED_SLOT_SIZE * 5;

    // Titles default to perfect position to be alongside a checkbox.
    protected int leftTitleX = PADDED_SLOT_SIZE;
    protected int leftTitleY = 4;
    protected Component leftTitle;

    protected int rightTitleX = RIGHT_START_X + PADDED_SLOT_SIZE;
    protected int rightTitleY = 4;
    protected Component rightTitle;

    @Override
    protected void createWidgets(ConduitScreenHelper screen, int startX, int startY, ConduitMenuDataAccess<U> dataAccess) {
        createLeftWidgets(screen, startX, startY, dataAccess);
        createRightWidgets(screen, startX + RIGHT_START_X, startY, dataAccess);

        // TODO: *could* implement sanity checks to ensure widgets are not outside their bounds? Might be nice static-time check, even if we only do it in dev envs?
    }

    public abstract void createLeftWidgets(ConduitScreenHelper screen, int startX, int startY, ConduitMenuDataAccess<U> dataAccess);
    public abstract void createRightWidgets(ConduitScreenHelper screen, int startX, int startY, ConduitMenuDataAccess<U> dataAccess);

    @Override
    public void renderLabels(GuiGraphics guiGraphics, int startX, int startY, Font font, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, startX, startY, font, mouseX, mouseY);

        // TODO: This should be a sprite.
        guiGraphics.blit(ConduitScreen.TEXTURE, startX + (WIDTH / 2), startY, 255, 0, 1, 97);

        guiGraphics.drawString(font, leftTitle, startX + leftTitleX, startY + leftTitleY, 4210752, false);
        guiGraphics.drawString(font, rightTitle, startX + rightTitleX, startY + rightTitleY, 4210752, false);
    }
}
