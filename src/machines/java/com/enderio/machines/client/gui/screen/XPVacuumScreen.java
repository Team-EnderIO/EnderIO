package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.client.gui.widget.EIOCommonWidgets;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.machines.client.gui.widget.FluidStackStaticWidget;
import com.enderio.machines.common.menu.XPVacuumMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class XPVacuumScreen extends MachineScreen<XPVacuumMenu> {

    private static final ResourceLocation XP_VACUUM_BG = EnderIO.loc("textures/gui/xp_vacuum.png");
    private static final ResourceLocation PLUS = EnderIO.loc("buttons/plus_small");
    private static final ResourceLocation MINUS = EnderIO.loc("buttons/minus_small");
    private static final WidgetSprites PLUS_SPRITES = new WidgetSprites(PLUS, PLUS);
    private static final WidgetSprites MINUS_SPRITES = new WidgetSprites(MINUS, MINUS);

    public XPVacuumScreen(XPVacuumMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, true);
        this.inventoryLabelY = this.imageHeight - 106;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new FluidStackStaticWidget(this, getMenu().getBlockEntity()::getFluidTank, leftPos + 27, topPos + 22, 32, 32));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableWidget(EIOCommonWidgets.createRange(
            leftPos + imageWidth - 6 - 16,
            topPos + 34,
            EIOLang.HIDE_RANGE,
            EIOLang.SHOW_RANGE,
            () -> menu.getBlockEntity().isRangeVisible(),
            state -> menu.getBlockEntity().setRangeVisible(state)));

        addRenderableWidget(new ImageButton(leftPos + imageWidth - 6 - 8 - 2 - 16, topPos + 34, 8, 8, PLUS_SPRITES,
            (b) -> this.menu.getBlockEntity().increaseRange()));
        addRenderableWidget(new ImageButton(leftPos + imageWidth - 6 - 8 - 2 - 16, topPos + 42, 8, 8, MINUS_SPRITES,
            (b) -> this.menu.getBlockEntity().decreaseRange()));

    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return XP_VACUUM_BG;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        guiGraphics.drawString(font, EIOLang.RANGE, this.imageWidth - 6 - this.font.width(EIOLang.RANGE), 24, 4210752, false);
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        guiGraphics.drawString(font, this.getMenu().getBlockEntity().getRange() + "", leftPos + imageWidth - 6 - 16 - 2 - 8 - 10, topPos + 38, 0, false);
    }

}
