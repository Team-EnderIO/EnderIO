package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.obelisks.attractor.AttractorObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.attractor.AttractorObeliskMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class AttractorObeliskScreen extends ObeliskScreen<AttractorObeliskBlockEntity, AttractorObeliskMenu> {

    public static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/screen/attractor.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public AttractorObeliskScreen(AttractorObeliskMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, BG_TEXTURE, WIDTH, HEIGHT);
    }

}
