package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.obelisks.relocator.RelocatorObeliskBlockEntity;
import com.enderio.enderio.content.machines.obelisks.relocator.RelocatorObeliskMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RelocatorObeliskScreen extends ObeliskScreen<RelocatorObeliskBlockEntity, RelocatorObeliskMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/screen/relocator.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public RelocatorObeliskScreen(RelocatorObeliskMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BG_TEXTURE, WIDTH, HEIGHT);
    }
}
