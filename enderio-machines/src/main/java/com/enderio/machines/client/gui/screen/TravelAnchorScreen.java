package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIOBase;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.common.menu.TravelAnchorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TravelAnchorScreen extends MachineScreen<TravelAnchorMenu> {

    private static final ResourceLocation TRAVEL_ANCHOR_BG = EnderIOBase.loc("textures/gui/screen/travel_anchor.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 184;

    private EditBox nameInput;

    private static final ResourceLocation VISIBILITY_BTNS = EnderIOBase.loc("textures/gui/icons/visibility_buttons.png");

    public TravelAnchorScreen(TravelAnchorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        name.setEditable(true);

        // TODO: Redo toggle image button
        //addRenderableWidget(
        //    new ToggleImageButton<>(this, leftPos + 150, topPos + 10, 16, 16, 0, 0, 16, 0, VISIBILITY_BTNS, 32, 16, () -> menu.getBlockEntity().isVisible(),
        //        menu.getBlockEntity()::setIsVisible, () -> menu.getBlockEntity().isVisible() ? EIOLang.VISIBLE : EIOLang.NOT_VISIBLE));
        nameInput = new EditBox(this.font, leftPos + 25, topPos + 14, 87, 18, Component.literal("name"));
        nameInput.setCanLoseFocus(true);
        nameInput.setTextColor(0xFFFFFFFF);
        nameInput.setTextColorUneditable(0xFFFFFFFF);
        nameInput.setBordered(false);
        nameInput.setMaxLength(50);
        nameInput.setResponder(menu::setName);
        nameInput.setValue(menu.getName());
        this.addRenderableWidget(nameInput);
        this.setInitialFocus(nameInput);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TRAVEL_ANCHOR_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (nameInput.isFocused()) {
            if (nameInput.keyPressed(keyCode, scanCode, modifiers) || nameInput.canConsumeInput()) {
                return true;
            }
        }

        return super.onKeyPressed(keyCode, scanCode, modifiers);
    }
}
