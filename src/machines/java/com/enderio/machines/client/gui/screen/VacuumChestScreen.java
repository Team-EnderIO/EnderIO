package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.machines.common.menu.VacuumChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VacuumChestScreen extends EIOScreen<VacuumChestMenu> {

    private static final ResourceLocation VACUUM_CHEST_BG = EnderIO.loc("textures/gui/vacuum_chest.png");
    private static final ResourceLocation BUTTONS = EnderIO.loc("textures/gui/icons/buttons.png");
    private static final ResourceLocation RANGE_BUTTON_TEXTURE = EnderIO.loc("textures/gui/icons/range_buttons.png");

    public VacuumChestScreen(VacuumChestMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, true);
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 14, topPos + 105 + 2, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
        addRenderableWidget(new ToggleImageButton<>(this, leftPos + imageWidth - 8 - 14 - 2 - 16, topPos + 105, 16, 16, 0, 0, 16, 0, RANGE_BUTTON_TEXTURE,
            () -> menu.getBlockEntity().isRangeVisible(), state -> menu.getBlockEntity().setIsRangeVisible(state),
            () -> menu.getBlockEntity().isRangeVisible() ? EIOLang.HIDE_RANGE : EIOLang.SHOW_RANGE));
        addRenderableWidget(new ImageButton(leftPos + imageWidth - 8 - 8, topPos + 86, 8, 8, 8, 0, 16, BUTTONS, (b) -> menu.getBlockEntity().increaseRange()));
        addRenderableWidget(new ImageButton(leftPos + imageWidth - 8 - 8, topPos + 94, 8, 8, 8, 8, 16, BUTTONS, (b) -> menu.getBlockEntity().decreaseRange()));
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return VACUUM_CHEST_BG;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 206);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        guiGraphics.drawString(font, EIOLang.FILTER, 8, 74, 4210752, false);
        guiGraphics.drawString(font, EIOLang.RANGE, imageWidth - 8 - font.width(EIOLang.RANGE), 74, 4210752, false);
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        guiGraphics.drawString(font, getMenu().getBlockEntity().getRange() + "", leftPos + imageWidth - 8 - 8 - 10, topPos + 90, 0, false);
    }

}