package com.enderio.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.enderio.machines.common.blocks.obelisks.attractor.AttractorObeliskBlockEntity;
import com.enderio.enderio.machines.common.blocks.obelisks.attractor.AttractorObeliskMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AttractorObeliskScreen extends ObeliskScreen<AttractorObeliskBlockEntity, AttractorObeliskMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/screen/attractor.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public AttractorObeliskScreen(AttractorObeliskMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, BG_TEXTURE, WIDTH, HEIGHT);
    }

}
