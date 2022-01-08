package com.enderio.machines.client.gui.screen;

import com.enderio.base.client.gui.screen.EIOScreen;
import com.enderio.base.client.gui.widgets.EnumIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.gui.widget.EnergyWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.blockentity.AlloySmelterMode;
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
        addRenderableOnly(new ProgressWidget(this, () -> menu.getBlockEntity().getProgress(), getGuiLeft() + 56, getGuiTop() + 36, 14, 14, 176, 0));
        addRenderableOnly(new ProgressWidget(this, () -> menu.getBlockEntity().getProgress(), getGuiLeft() + 104, getGuiTop() + 36, 14, 14, 176, 0));
        addRenderableOnly(new EnergyWidget(this, getMenu().getBlockEntity()::getGuiEnergy, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        if (getMenu().getBlockEntity().getTier() != MachineTier.Simple) {
            addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6 + 16 * 3, () -> menu.getBlockEntity().getMode(), mode -> menu.getBlockEntity().setMode(mode), MachineLang.ALLOY_SMELTER_MODE));
        }
    }

    @Override
    protected ResourceLocation getBackgroundImage() {
        AlloySmelterBlockEntity be = getMenu().getBlockEntity();
        return switch (be.getTier()) {
            case Simple -> be.getMode() == AlloySmelterMode.Alloys ? BG_TEXTURE_SIMPLE_ALLOY : BG_TEXTURE_SIMPLE_FURNACE;
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
