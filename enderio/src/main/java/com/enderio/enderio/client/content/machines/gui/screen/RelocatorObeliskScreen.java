package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.obelisks.relocator.RelocatorObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.relocator.RelocatorObeliskMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class RelocatorObeliskScreen extends ObeliskScreen<RelocatorObeliskBlockEntity, RelocatorObeliskMenu> {

    public static final Identifier BG_TEXTURE = EnderIO.rl("textures/gui/screen/relocator.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public RelocatorObeliskScreen(RelocatorObeliskMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, BG_TEXTURE, WIDTH, HEIGHT);
    }
}
