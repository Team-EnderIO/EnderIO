package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.content.travel.travel_anchor.TravelAnchorMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class TravelAnchorScreen extends MachineScreen<TravelAnchorMenu> {

    private static final Identifier TRAVEL_ANCHOR_BG = EnderIO.id("textures/gui/screen/travel_anchor.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 184;

    private EditBox nameInput;

    private static final Identifier VISIBILITY_BTNS = EnderIOAPI
            .rl("textures/gui/icons/visibility_buttons.png");

    private static final Identifier VISIBLE_BUTTON = EnderIO.id("screen/travel_anchor/visible");
    private static final Identifier NOT_VISIBLE_BUTTON = EnderIO.id("screen/travel_anchor/not_visible");

    public TravelAnchorScreen(TravelAnchorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);
    }

    @Override
    protected void init() {
        super.init();

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
        nameInput.setEditable(true);

        addRenderableWidget(ToggleIconButton.of(leftPos + 150, topPos + 10, 16, 16, VISIBLE_BUTTON, NOT_VISIBLE_BUTTON,
                EIOCommonLang.VISIBLE, EIOCommonLang.NOT_VISIBLE, menu::isVisible, menu::setVisible));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        nameInput.setValue(menu.getName()); //TODO stupid but it works
        menu.setVisible(menu.isVisible()); //TODO stupid but it works
        graphics.blit(RenderPipelines.GUI_TEXTURED, TRAVEL_ANCHOR_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public boolean onKeyPressed(KeyEvent event) {
        if (nameInput.isFocused()) {
            if (nameInput.keyPressed(event) || nameInput.canConsumeInput()) {
                return true;
            }
        }

        return super.onKeyPressed(event);
    }
}
