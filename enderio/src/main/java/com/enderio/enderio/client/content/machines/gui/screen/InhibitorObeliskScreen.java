package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.obelisks.inhibitor.InhibitorObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.inhibitor.InhibitorObeliskMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class InhibitorObeliskScreen extends ObeliskScreen<InhibitorObeliskBlockEntity, InhibitorObeliskMenu> {

    public static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/screen/inhibitor.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public InhibitorObeliskScreen(InhibitorObeliskMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BG_TEXTURE, WIDTH, HEIGHT);
    }
}
