package com.enderio.machines.client;

import com.enderio.base.EnderIO;
import com.enderio.base.client.screen.EIOScreen;
import com.enderio.base.client.screen.EnumIconWidget;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnchanterScreen extends EIOScreen<EnchanterMenu> {
    private static final ResourceLocation BG_TEXTURE = EIOMachines.loc("textures/gui/enchanter.png");

    public EnchanterScreen(EnchanterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    
    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(), control -> menu.getBlockEntity().setRedstoneControl(control)));
    }
    
    @Override
    protected ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
    
    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
        if (this.getMenu().getCurrentCost() < 0) return;
        int colour = 8453920; //green
        TranslatableComponent component = new TranslatableComponent("container.repair.cost", this.getMenu().getCurrentCost());
        if (Minecraft.getInstance().player.experienceLevel < this.getMenu().getCurrentCost() && !Minecraft.getInstance().player.isCreative()) {
            colour = 16736352; //red
        }
        drawCenteredString(pPoseStack, this.font, component, (width-getXSize())/2 + getXSize()/2, (height-getYSize())/2 + 57, colour);
    }
}
