package com.enderio.conduits.client.gui;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.menu.ConduitFilterMenu;
import com.enderio.conduits.common.network.ConduitItemFilterPacket;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.common.network.CoreNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ConduitFilterScreen extends EIOScreen<ConduitFilterMenu> {
    private static final ResourceLocation TEXTURE = EnderIO.loc("textures/gui/conduit.png");
    private final int ContentHeight;

    public ConduitFilterScreen(ConduitFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, true);
        // 3 padding, 9 fontheight, 3 padding
        // height based on slotcount
        // 3 padding, 9 fontheight, (already included in px, 7 high)
        ContentHeight = 3 + 9 + 3 + (Math.abs(menu.filterSlots.size()/9) * 18) + 3 + 9;

        imageWidth = 206;
        imageHeight = ContentHeight + 90;

        titleLabelX = 14;
        titleLabelY = 6;

        inventoryLabelX = 22;
        inventoryLabelY = ContentHeight-3;
    }

    @Override
    protected void init() {
        super.init();

        var cap = menu.inventory.getFilterCap();
        // TODO BUG: button is wacky
        addRenderableWidget(Button.builder(Component.literal("I"), ev ->
                CoreNetwork.sendToServer(new ConduitItemFilterPacket(menu.inventory.filter, !cap.getIgnoreMode(), cap.getStrictMode())))
            .size(16, 16)
            .pos(leftPos + (imageWidth - 29), topPos + 16)
            .tooltip(Tooltip.create(Component.literal("Toggle Ignore Mode")))
            .build());
        addRenderableWidget(Button.builder(Component.literal("S"), ev ->
                CoreNetwork.sendToServer(new ConduitItemFilterPacket(menu.inventory.filter, cap.getIgnoreMode(), !cap.getStrictMode())))
            .size(16, 16)
            .pos(leftPos + (imageWidth - 29), topPos + 34)
            .tooltip(Tooltip.create(Component.literal("Toggle Strict Mode")))
            .build());
        // todo image buttone
        // new ResourceLocation("textures/gui/checkmark.png") vanilla mc checkmark
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blitNineSliced(getBackgroundImage(), getGuiLeft(), getGuiTop(), imageWidth, ContentHeight, 9, 206, 107, 0, 0);
        guiGraphics.blit(getBackgroundImage(), getGuiLeft(), getGuiTop()+ContentHeight, 0, 105, imageWidth, 90);

        for (var slot : menu.filterSlots) {
            guiGraphics.blit(TEXTURE, leftPos + slot.x() - 1, topPos + slot.y() - 1, 206, 0, 18, 18);
        }
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(imageWidth, imageHeight);
    }
}
