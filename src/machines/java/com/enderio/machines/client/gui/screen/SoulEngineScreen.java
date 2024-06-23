package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.client.gui.widget.RedstoneControlIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.menu.SoulEngineMenu;
import com.enderio.machines.common.souldata.EngineSoul;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class SoulEngineScreen extends MachineScreen<SoulEngineMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/soul_engine.png");

    public SoulEngineScreen(SoulEngineMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, getMenu().getBlockEntity()::isCapacitorInstalled, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new RedstoneControlIconWidget(leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableOnly(new FluidStackWidget(this, getMenu().getBlockEntity()::getFluidTank, 80 + leftPos, 21 + topPos, 16, 47));

        addRenderableWidget(new ActivityWidget(this, menu.getBlockEntity()::getMachineStates, leftPos + imageWidth - 6 - 16, topPos + 16 * 4));

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, menu, this::addRenderableWidget, font));

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
        Optional<ResourceLocation> rl = getMenu().getBlockEntity().getEntityType();
        if (rl.isPresent()) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(rl.get());
            if (BuiltInRegistries.ENTITY_TYPE.getKey(type).equals(rl.get())) { // check we don't get the default pig
                String name = type.getDescription().getString();
                guiGraphics.drawString(font, name, imageWidth / 2f - font.width(name) / 2f, 10, 4210752, false);
            } else {
                guiGraphics.drawString(font, rl.get().toString(), imageWidth / 2f - font.width(rl.get().toString()) / 2f, 10, 4210752, false);
            }
            EngineSoul.SoulData data = EngineSoul.ENGINE.map.get(rl.get());
            if (data != null) {
                double burnRate = menu.getBlockEntity().getBurnRate();
                float genRate = menu.getBlockEntity().getGenerationRate();
                guiGraphics.drawString(font, data.tickpermb()/ burnRate + " t/mb", imageWidth / 2f + 12 , 40, 4210752, false);
                guiGraphics.drawString(font, (int) (data.powerpermb() * genRate) + " µI/mb", imageWidth / 2f + 12 , 50, 4210752, false);

            }
        }


    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
}
