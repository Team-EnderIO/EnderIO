package com.enderio.machines.client.screen;

import com.enderio.base.client.screen.EIOScreen;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.widget.EnergyWidget;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AlloySmelterScreen extends EIOScreen<AlloySmelterMenu> {
    private static final ResourceLocation BG_TEXTURE_SIMPLE_ALLOY = EIOMachines.loc("textures/gui/simple_alloy_smelter.png");
    private static final ResourceLocation BG_TEXTURE_SIMPLE_FURNACE = EIOMachines.loc("textures/gui/simple_furnace.png");
    private static final ResourceLocation BG_TEXTURE_ALLOY = EIOMachines.loc("textures/gui/alloy_smelter_alloy.png");
    private static final ResourceLocation BG_TEXTURE_AUTO = EIOMachines.loc("textures/gui/alloy_smelter_auto.png");
    private static final ResourceLocation BG_TEXTURE_FURNACE = EIOMachines.loc("textures/gui/alloy_smelter_furnace.png");

    public AlloySmelterScreen(AlloySmelterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new EnergyWidget(this, getMenu().getBlockEntity()::guiGetEnergy, 16 + leftPos, 14 + topPos, 9, 42));
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);

        // TODO: Get machine progress and blit the flame :)
    }

    @Override
    protected ResourceLocation getBackgroundImage() {
        AlloySmelterBlockEntity be = getMenu().getBlockEntity();
        return switch (be.getTier()) {
            case Simple -> be.getMode() == AlloySmelterBlockEntity.SmeltingMode.Alloys ? BG_TEXTURE_SIMPLE_ALLOY : BG_TEXTURE_SIMPLE_FURNACE;
            case Standard, Enhanced -> switch (be.getMode()) {
                case All -> BG_TEXTURE_AUTO;
                case Alloys -> BG_TEXTURE_ALLOY;
                case Furnace -> BG_TEXTURE_FURNACE;
            };
        };
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
}
