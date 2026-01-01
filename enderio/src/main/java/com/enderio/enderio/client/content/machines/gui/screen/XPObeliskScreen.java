package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.foundation.widgets.ExperienceWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.obelisks.xp.XPObeliskMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class XPObeliskScreen extends MachineScreen<XPObeliskMenu> {
    private static final Identifier BG = EnderIO.id("textures/gui/screen/xp_obelisk.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 116;

    private static final Identifier XP_ADD_ONE = EnderIO.id("buttons/xp_add_one");
    private static final Identifier XP_ADD_ALL = EnderIO.id("buttons/xp_add_all");
    private static final Identifier XP_ADD_MULTI = EnderIO.id("buttons/xp_add_multi");
    private static final Identifier XP_REMOVE_ONE = EnderIO.id("buttons/xp_remove_one");
    private static final Identifier XP_REMOVE_MULTI = EnderIO.id("buttons/xp_remove_multi");
    private static final Identifier XP_REMOVE_ALL = EnderIO.id("buttons/xp_remove_all");

    private final List<ImageButton> xpButtons = new ArrayList<>();

    public XPObeliskScreen(XPObeliskMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 8 - 14, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        addRenderableOnly(new ExperienceWidget(leftPos + (imageWidth / 2) - 55, topPos + 55, 110, 5, menu::getFluid));

        int size = 16;
        int padding = 16;
        int offset = size + padding;
        Vector2i midLeft = new Vector2i(leftPos + imageWidth / 2 - size / 2 - offset, topPos + 58);
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() - offset, size, XPObeliskMenu.ADD_1_LEVEL_BUTTON_ID,
                XP_ADD_ONE, MachinesLang.XP_RETRIEVE_1));
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() + padding, size, XPObeliskMenu.REMOVE_1_LEVEL_BUTTON_ID,
                XP_REMOVE_ONE, MachinesLang.XP_STORE_1));
        midLeft = midLeft.add(offset, 0);
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() - offset, size, XPObeliskMenu.ADD_10_LEVELS_BUTTON_ID,
                XP_ADD_MULTI, MachinesLang.XP_RETRIEVE_10));
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() + padding, size,
                XPObeliskMenu.REMOVE_10_LEVELS_BUTTON_ID, XP_REMOVE_MULTI, MachinesLang.XP_STORE_10));
        midLeft = midLeft.add(offset, 0);
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() - offset, size, XPObeliskMenu.ADD_ALL_XP_BUTTON_ID,
                XP_ADD_ALL, MachinesLang.XP_RETRIEVE_ALL));
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() + padding, size, XPObeliskMenu.REMOVE_ALL_XP_BUTTON_ID,
                XP_REMOVE_ALL, MachinesLang.XP_STORE_ALL));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 7, 136, 102);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    private ImageButton makeButton(int x, int y, int size, int id, Identifier SPRITE, Component tooltip) {
        ImageButton button = new ImageButton(x, y, size, size, new WidgetSprites(SPRITE, SPRITE),
                (press) -> handleButtonPress(id));
        button.setTooltip(Tooltip.create(tooltip));
        xpButtons.add(button);
        return button;
    }

    private void ioConfigCallback(boolean ioconfigVisible) {
        xpButtons.forEach(button -> button.visible = !ioconfigVisible);
    }

}
