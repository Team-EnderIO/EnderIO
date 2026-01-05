package com.enderio.enderio.client.content.tools;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.tools.coordinate_selector.CoordinateMenu;
import com.enderio.enderio.foundation.network.packets.ServerboundUpdateCoordinateSelectionNameMenuPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class CoordinateMenuScreen extends EnderContainerScreen<CoordinateMenu> {

    private static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/40/location_printout.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 116;

    private EditBox nameInput;

    public CoordinateMenuScreen(CoordinateMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        nameInput = new EditBox(this.font, leftPos + 43 + 4, topPos + 20 + 4, 92 - 12, 18, Component.literal("name"));
        nameInput.setCanLoseFocus(false);
        nameInput.setTextColor(0xFFFFFFFF);
        nameInput.setTextColorUneditable(0xFFFFFFFF);
        nameInput.setBordered(false);
        nameInput.setMaxLength(50);
        nameInput.setResponder(this::onNameChanged);
        nameInput.setValue(menu.getName());

        addRenderableWidget(nameInput);
        addRestorableState("name", nameInput);
        setInitialFocus(nameInput);
        nameInput.setEditable(true);

        this.addRenderableWidget(new Button.Builder(CommonComponents.GUI_OK, mouseButton -> Minecraft.getInstance().player.closeContainer())
            .bounds(getGuiLeft() + imageWidth - 30, getGuiTop() + imageHeight - 30, 20, 20)
            .build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight, 256, 256);

        int midX = this.width / 2;
        int y = topPos + 48;
        String txt = getMenu().getSelection().pos().toShortString();
        int x = midX - font.width(txt) / 2;
        guiGraphics.drawString(this.font, txt, x, y, CommonColors.DARK_GRAY, true);
        txt = getMenu().getSelection().getLevelName();
        y += font.lineHeight + 4;
        x = midX - font.width(txt) / 2;
        guiGraphics.drawString(this.font, txt, x, y, CommonColors.DARK_GRAY, true);
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

    private void onNameChanged(String name) {
        ClientPacketDistributor.sendToServer(new ServerboundUpdateCoordinateSelectionNameMenuPacket(getMenu().containerId, name));
    }
}
