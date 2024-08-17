package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.machines.client.gui.widget.ActiveWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.common.menu.AversionObeliskMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AversionObeliskScreen extends MachineScreen<AversionObeliskMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/inhibitor.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    private static final ResourceLocation BUTTONS = EnderIO.loc("textures/gui/icons/buttons.png");
    private static final ResourceLocation RANGE_BUTTON_TEXTURE = EnderIO.loc("textures/gui/icons/range_buttons.png");

    public AversionObeliskScreen(AversionObeliskMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, getMenu().getBlockEntity()::isCapacitorInstalled, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));


        addRenderableWidget(new ToggleImageButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 2*16 + 2, 16, 16, 0, 0, 16, 0, RANGE_BUTTON_TEXTURE,
            () -> menu.getBlockEntity().isRangeVisible(), state -> menu.getBlockEntity().setIsRangeVisible(state),
            () -> menu.getBlockEntity().isRangeVisible() ? EIOLang.HIDE_RANGE : EIOLang.SHOW_RANGE));
        addRenderableWidget(new ImageButton(leftPos + imageWidth - 2*16, topPos + 2 + 16*2, 8, 8, 8, 0, 16, BUTTONS, (b) -> menu.getBlockEntity().increaseRange()));
        addRenderableWidget(new ImageButton(leftPos + imageWidth - 2*16, topPos + 2 + 16*2 + 8, 8, 8, 8, 8, 16, BUTTONS, (b) -> menu.getBlockEntity().decreaseRange()));

        addRenderableWidget(new ActiveWidget(this, menu.getBlockEntity()::getMachineStates, leftPos + imageWidth - 6 - 16, topPos + 16*4));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        guiGraphics.drawString(font, getMenu().getBlockEntity().getRange() + "", leftPos + imageWidth - 8 - 16 - font.width(getMenu().getBlockEntity().getRange() + "") - 10, topPos + 16*2 + 6, 0, false);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        guiGraphics.drawString(font, EIOLang.RANGE, imageWidth - 6 - font.width(EIOLang.RANGE), 16 + 8, 4210752, false);
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(WIDTH, HEIGHT);
    }
}
