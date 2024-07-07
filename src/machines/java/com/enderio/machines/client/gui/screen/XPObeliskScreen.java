package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ExperienceWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.XPObeliskMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class XPObeliskScreen extends MachineScreen<XPObeliskMenu> {
    private static final ResourceLocation BG = EnderIO.loc("textures/gui/screen/xp_obelisk.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 116;

    private static final ResourceLocation XP_ADD_ONE = EnderIO.loc("buttons/xp_add_one");
    private static final ResourceLocation XP_ADD_ALL = EnderIO.loc("buttons/xp_add_all");
    private static final ResourceLocation XP_ADD_MULTI = EnderIO.loc("buttons/xp_add_multi");
    private static final ResourceLocation XP_REMOVE_ONE = EnderIO.loc("buttons/xp_remove_one");
    private static final ResourceLocation XP_REMOVE_MULTI = EnderIO.loc("buttons/xp_remove_multi");
    private static final ResourceLocation XP_REMOVE_ALL = EnderIO.loc("buttons/xp_remove_all");

    private final List<ImageButton> xpButtons = new ArrayList<>();

    public XPObeliskScreen(XPObeliskMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 8 - 14, topPos + 6, menu::getRedstoneControl,
            menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableOnly(new ExperienceWidget(leftPos + (imageWidth / 2) - 55, topPos + 55, 110, 5, menu::getFluidTank));

        int size = 16;
        int padding = 16;
        int offset = size + padding;
        Vector2i midLeft = new Vector2i(leftPos + imageWidth / 2 - size / 2 - offset, topPos + 58);
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() - offset, size, 0, XP_ADD_ONE, MachineLang.RETRIEVE_1));
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() + padding, size, 1, XP_REMOVE_ONE, MachineLang.STORE_1));
        midLeft = midLeft.add(offset, 0);
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() - offset, size, 2, XP_ADD_MULTI, MachineLang.RETRIEVE_10));
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() + padding, size, 3, XP_REMOVE_MULTI, MachineLang.STORE_10));
        midLeft = midLeft.add(offset, 0);
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() - offset, size, 4, XP_ADD_ALL, MachineLang.RETRIEVE_ALL));
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() + padding, size, 5, XP_REMOVE_ALL, MachineLang.STORE_ALL));

        // TODO: IO Config
        //IOConfigButton.Inset insets = new IOConfigButton.Inset(0, 22, -26,0);
        //addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, menu, this::addRenderableWidget, font, insets,
        //    this::ioConfigCallback));

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
    }

    private void handlePress(int id) {
        this.getMinecraft().gameMode.handleInventoryButtonClick(getMenu().containerId, id);
    }

    private ImageButton makeButton(int x, int y, int size, int id, ResourceLocation SPRITE, Component tooltip) {
        ImageButton button = new ImageButton(x, y, size, size, new WidgetSprites(SPRITE, SPRITE), (press) -> handlePress(id));
        button.setTooltip(Tooltip.create(tooltip));
        xpButtons.add(button);
        return button;
    }

    private void ioConfigCallback(boolean ioconfigVisible) {
        xpButtons.forEach(button -> button.visible = !ioconfigVisible);
    }

}
