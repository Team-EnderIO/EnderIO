package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.client.gui.widget.EIOCommonWidgets;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.common.menu.DrainMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DrainScreen extends MachineScreen<DrainMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/drain.png");
    private static final ResourceLocation PLUS = EnderIO.loc("buttons/plus_small");
    private static final ResourceLocation MINUS = EnderIO.loc("buttons/minus_small");
    private static final WidgetSprites PLUS_SPRITES = new WidgetSprites(PLUS, PLUS);
    private static final WidgetSprites MINUS_SPRITES = new WidgetSprites(MINUS, MINUS);

    public DrainScreen(DrainMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, getMenu().getBlockEntity()::isCapacitorInstalled, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableOnly(new FluidStackWidget(this, getMenu().getBlockEntity()::getFluidTank, 80 + leftPos, 21 + topPos, 16, 47));

        addRenderableWidget(EIOCommonWidgets.createRange(
            leftPos + imageWidth - 6 - 16,
            topPos + 2 * 16 + 2,
            EIOLang.HIDE_RANGE,
            EIOLang.SHOW_RANGE,
            () -> menu.getBlockEntity().isRangeVisible(),
            state -> menu.getBlockEntity().setRangeVisible(state)));

        addRenderableWidget(new ImageButton(leftPos + imageWidth - 2 * 16, topPos + 2 + 16 * 2, 8, 8, PLUS_SPRITES,
            (b) -> menu.getBlockEntity().increaseRange()));
        addRenderableWidget(new ImageButton(leftPos + imageWidth - 2 * 16, topPos + 2 + 16 * 2 + 8, 8, 8, MINUS_SPRITES,
            (b) -> menu.getBlockEntity().decreaseRange()));

        addRenderableWidget(new ActivityWidget(this, menu.getBlockEntity()::getMachineStates, leftPos + imageWidth - 6 - 16, topPos + 16 * 4));
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
        guiGraphics.drawString(font, EIOLang.RANGE, imageWidth - 6 - font.width(EIOLang.RANGE), 16 + 8, 4210752, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        guiGraphics.drawString(font, getMenu().getBlockEntity().getRange() + "", leftPos + imageWidth - 8 - 16 - font.width(getMenu().getBlockEntity().getRange() + "") - 10, topPos + 16*2 + 6, 0, false);
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
}
