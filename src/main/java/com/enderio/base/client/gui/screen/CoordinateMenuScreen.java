package com.enderio.base.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.menu.CoordinateMenu;
import com.enderio.base.common.network.UpdateCoordinateSelectionNameMenuPacket;
import com.enderio.core.client.gui.screen.EnderContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class CoordinateMenuScreen extends EnderContainerScreen<CoordinateMenu> {

    private static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/40/location_printout.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 116;

    private EditBox nameInput;

    public CoordinateMenuScreen(CoordinateMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
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

        this.addRenderableWidget(nameInput);
        this.setInitialFocus(nameInput);
        nameInput.setEditable(true);

        this.addRenderableWidget(new Button.Builder(EIOLang.OK, mouseButton -> Minecraft.getInstance().player.closeContainer())
            .bounds(getGuiLeft() + imageWidth - 30, getGuiTop() + imageHeight - 30, 20, 20)
            .build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTicks, int pMouseX, int pMouseY) {
        guiGraphics.blit(BG_TEXTURE, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight);

        int midX = this.width / 2;
        int y = topPos + 48;
        String txt = getMenu().getSelection().pos().toShortString();
        int x = midX - font.width(txt) / 2;
        guiGraphics.drawString(this.font, txt, x, y, 0xFFFFFF, true);
        txt = getMenu().getSelection().getLevelName();
        y += font.lineHeight + 4;
        x = midX - font.width(txt) / 2;
        guiGraphics.drawString(this.font, txt, x, y, 0xFFFFFF, true);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        String name = nameInput.getValue();
        super.resize(pMinecraft, pWidth, pHeight);
        nameInput.setValue(name);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (preKeyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }

        if (nameInput.isFocused()) {
            if (nameInput.keyPressed(pKeyCode, pScanCode, pModifiers) || nameInput.canConsumeInput()) {
                return true;
            }
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void onNameChanged(String name) {
        PacketDistributor.sendToServer(new UpdateCoordinateSelectionNameMenuPacket(getMenu().containerId, name));
    }
}
