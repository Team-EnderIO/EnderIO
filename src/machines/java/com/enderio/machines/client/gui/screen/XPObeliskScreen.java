package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.machines.client.gui.widget.ExperienceWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.menu.XPObeliskMenu;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class XPObeliskScreen extends EIOScreen<XPObeliskMenu> {
    private static final ResourceLocation BG = EnderIO.loc("textures/gui/xp_obelisk.png");
    private static final ResourceLocation XP_BTNS = EnderIO.loc("textures/gui/icons/xp_obelisk.png");

    public XPObeliskScreen(XPObeliskMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 14, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
        //TODO: add proper io config offsets
        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, menu, this::addRenderableWidget, font));

        addRenderableOnly(new ExperienceWidget(this, getMenu().getBlockEntity()::getFluidTank, leftPos + (imageWidth / 2) - 55, topPos + 55, 110, 5));

        int size = 16;
        int padding = 16;
        int offset = size + padding;
        Vector2i midLeft = new Vector2i(leftPos + imageWidth / 2 - size / 2 - offset, topPos + 58);
        addRenderableWidget(new ImageButton(midLeft.x(), midLeft.y() - offset, size, size, 0, 0, 0, XP_BTNS, 48, 32, (press) -> handlePress(0)));
        addRenderableWidget(new ImageButton(midLeft.x(), midLeft.y() + padding, size, size, 0, 16, 0, XP_BTNS, 48, 32, (press) -> handlePress(1)));
        midLeft = midLeft.add(offset, 0);
        addRenderableWidget(new ImageButton(midLeft.x(), midLeft.y() - offset, size, size, 16, 0, 0, XP_BTNS, 48, 32, (press) -> handlePress(2)));
        addRenderableWidget(new ImageButton(midLeft.x(), midLeft.y() + padding, size, size, 16, 16, 0, XP_BTNS, 48, 32, (press) -> handlePress(3)));
        midLeft = midLeft.add(offset, 0);
        addRenderableWidget(new ImageButton(midLeft.x(), midLeft.y() - offset, size, size, 32, 0, 0, XP_BTNS, 48, 32, (press) -> handlePress(4)));
        addRenderableWidget(new ImageButton(midLeft.x(), midLeft.y() + padding, size, size, 32, 16, 0, XP_BTNS, 48, 32, (press) -> handlePress(5)));

    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 116);
    }
    private void handlePress(int id) {
        this.getMinecraft().gameMode.handleInventoryButtonClick(getMenu().containerId, id);
    }

}
