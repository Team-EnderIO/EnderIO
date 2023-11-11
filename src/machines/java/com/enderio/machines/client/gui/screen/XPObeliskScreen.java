package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.machines.client.gui.widget.ExperienceWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.XPObeliskMenu;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
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

        addRenderableOnly(new ExperienceWidget(this, getMenu().getBlockEntity()::getFluidTank, leftPos + (imageWidth / 2) - 55, topPos + 55, 110, 5));

        int size = 16;
        int padding = 16;
        int offset = size + padding;
        Vector2i midLeft = new Vector2i(leftPos + imageWidth / 2 - size / 2 - offset, topPos + 58);
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() - offset, size,  0, 0, 0, MachineLang.RETRIEVE_1));
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() + padding, size,  0, 16, 1, MachineLang.STORE_1));
        midLeft = midLeft.add(offset, 0);
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() - offset, size,  16, 0, 2, MachineLang.RETRIEVE_10));
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() + padding, size,  16, 16, 3, MachineLang.STORE_10));
        midLeft = midLeft.add(offset, 0);
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() - offset, size,  32, 0, 4, MachineLang.RETRIEVE_ALL));
        addRenderableWidget(makeButton(midLeft.x(), midLeft.y() + padding, size,  32, 16, 5, MachineLang.STORE_ALL));

        IOConfigButton.Inset insets = new IOConfigButton.Inset(0, 22, -26,0);
        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, menu, this::addRenderableWidget, font, insets));

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

    private ImageButton makeButton(int x, int y, int size,int xTexStart, int yTexStart, int id, Component tooltip){
        ImageButton button = new ImageButton(x , y, size, size, xTexStart, yTexStart, 0, XP_BTNS, 48,32, (press) -> handlePress(id));
        button.setTooltip(Tooltip.create(tooltip));
        return button;
    }

}
