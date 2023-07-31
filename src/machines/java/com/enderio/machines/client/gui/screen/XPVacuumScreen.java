package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.machines.client.gui.widget.FluidStackStaticWidget;
import com.enderio.machines.common.menu.XPVacuumMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class XPVacuumScreen extends EIOScreen<XPVacuumMenu> {

    private static final ResourceLocation XP_VACUUM_BG = EnderIO.loc("textures/gui/xp_vacuum.png");
    private static final ResourceLocation BUTTONS = EnderIO.loc("textures/gui/icons/buttons.png");
    private static final ResourceLocation RANGE_BUTTON_TEXTURE = EnderIO.loc("textures/gui/icons/range_buttons.png");

    public XPVacuumScreen(XPVacuumMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, true);
        this.inventoryLabelY = this.imageHeight - 106;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new FluidStackStaticWidget(this, getMenu().getBlockEntity()::getFluidTank, leftPos + 27, topPos + 22, 32, 32));
        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 14, topPos + 52, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
        addRenderableWidget(new ToggleImageButton<>(this, leftPos + imageWidth - 8 - 16, topPos + 34, 16, 16, 0, 0, 16, 0, RANGE_BUTTON_TEXTURE,
            () -> menu.getBlockEntity().isRangeVisible(), state -> menu.getBlockEntity().setIsRangeVisible(state),
            () -> menu.getBlockEntity().isRangeVisible() ? EIOLang.HIDE_RANGE : EIOLang.SHOW_RANGE));
        addRenderableWidget(
            new ImageButton(leftPos + imageWidth - 8 - 8 - 2 - 16, topPos + 34, 8, 8, 8, 0, 16, BUTTONS, (b) -> this.menu.getBlockEntity().increaseRange()));
        addRenderableWidget(
            new ImageButton(leftPos + imageWidth - 8 - 8 - 2 - 16, topPos + 42, 8, 8, 8, 8, 16, BUTTONS, (b) -> this.menu.getBlockEntity().decreaseRange()));
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
        guiGraphics.drawString(font, EIOLang.RANGE, this.imageWidth - 8 - this.font.width(EIOLang.RANGE), 21, 4210752, false);
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        guiGraphics.drawString(font, this.getMenu().getBlockEntity().getRange() + "", leftPos + imageWidth - 8 - 16 - 2 - 8 - 10, topPos + 38, 0, false);
    }

}
