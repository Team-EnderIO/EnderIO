package com.enderio.conduits.api.screen;

import com.enderio.conduits.api.connection.config.ConnectionConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public abstract class ConduitScreenType<U extends ConnectionConfig> {
    // Used for startX, startY
    private static final int USABLE_START_X = 22;
    private static final int USABLE_START_Y = 7;

    protected static final int SLOT_SIZE = 16;
    protected static final int PADDED_SLOT_SIZE = SLOT_SIZE + 2;

    // All of these are estimates and will change after the GUI rewrite.
    // With any luck, most UIs will just work if they rely upon these values.
    protected static final int WIDTH = 162;
    protected static final int HEIGHT = 100;

    @ApiStatus.Internal
    public void createScreenWidgets(ConduitScreenHelper screen, int guiLeft, int guiTop,
            ConduitMenuDataAccess<U> dataAccess) {
        createWidgets(screen, guiLeft + USABLE_START_X, guiTop + USABLE_START_Y, dataAccess);
    }

    @ApiStatus.Internal
    public void renderScreenLabels(ConduitMenuDataAccess<U> dataAccess, GuiGraphics guiGraphics, Font font, int mouseX,
            int mouseY) {
        renderLabels(dataAccess, guiGraphics, USABLE_START_X, USABLE_START_Y, font, mouseX, mouseY);
    }

    protected abstract void createWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<U> dataAccess);

    /**
     * Already projected into gui space (guiLeft & guiRight in Screen), so only local transformations required.
     * @param guiGraphics
     * @param font
     * @param mouseX
     * @param mouseY
     */
    protected void renderLabels(ConduitMenuDataAccess<U> dataAccess, GuiGraphics guiGraphics, int startX, int startY,
            Font font, int mouseX, int mouseY) {
    }
}
