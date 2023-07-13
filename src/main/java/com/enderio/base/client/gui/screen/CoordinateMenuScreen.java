package com.enderio.base.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.menu.CoordinateMenu;
import com.enderio.base.common.network.UpdateCoordinateSelectionNameMenuPacket;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.common.network.CoreNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CoordinateMenuScreen extends EIOScreen<CoordinateMenu> {

    private static final Vector2i BG_SIZE = new Vector2i(176,116);
    private static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/40/location_printout.png");

    public CoordinateMenuScreen(CoordinateMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        EditBox name = new EditBox(this.font, leftPos + 43 + 4, topPos + 20 + 4, 92 - 12, 18, Component.literal("name"));
        name.setCanLoseFocus(false);
        name.setTextColor(0xFFFFFFFF);
        name.setTextColorUneditable(0xFFFFFFFF);
        name.setBordered(false);
        name.setMaxLength(50);
        name.setResponder(this::onNameChanged);
        name.setValue(menu.getName());
        this.addRenderableWidget(name);
        this.setInitialFocus(name);
        name.setEditable(true);
        // TODO: Translation string
        this.addRenderableWidget(new Button.Builder(Component.literal("Ok"), mouseButton -> Minecraft.getInstance().player.closeContainer())
            .bounds(getGuiLeft() + imageWidth - 30, getGuiTop() + imageHeight - 30, 20, 20)
            .build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTicks, int pMouseX, int pMouseY) {
        super.renderBg(guiGraphics, pPartialTicks, pMouseX, pMouseY);

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
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return BG_SIZE;
    }


    private void onNameChanged(String name) {
        CoreNetwork.sendToServer(new UpdateCoordinateSelectionNameMenuPacket(getMenu().containerId, name));
    }
}
