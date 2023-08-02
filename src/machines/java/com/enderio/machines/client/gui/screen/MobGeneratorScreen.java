package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.menu.MobGeneratorMenu;
import com.enderio.machines.common.network.MobGeneratorSoulPacket;
import com.enderio.machines.common.souldata.GeneratorSoul;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class MobGeneratorScreen extends EIOScreen<MobGeneratorMenu>{

    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/mob_generator.png");

    public MobGeneratorScreen(MobGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, getMenu().getBlockEntity()::isCapacitorInstalled, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableOnly(new FluidStackWidget(this, getMenu().getBlockEntity()::getFluidTank, 80 + leftPos, 21 + topPos, 16, 47));

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 22, 16, 16, menu, this::addRenderableWidget, font));

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
        Optional<ResourceLocation> rl = getMenu().getBlockEntity().getEntityType();
        if (rl.isPresent()) {
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(rl.get());
            if (type != null && ForgeRegistries.ENTITY_TYPES.getKey(type).equals(rl.get())) { // check we don't get the default pig
                String name = type.getDescription().getString();
                guiGraphics.drawString(font, name, imageWidth / 2f - font.width(name) / 2f, 10, 4210752, false);
            } else {
                guiGraphics.drawString(font, rl.get().toString(), imageWidth / 2f - font.width(rl.get().toString()) / 2f, 10, 4210752, false);
            }
            GeneratorSoul.SoulData data = MobGeneratorSoulPacket.SYNCED_DATA.get(rl.get());
            if (data != null) {
                guiGraphics.drawString(font, data.tickpermb() + " t/mb", imageWidth / 2f + 20 - font.width(data.tickpermb() + "") / 2f, 40, 4210752, false);
                guiGraphics.drawString(font, data.powerpermb() + " µI/mb", imageWidth / 2f + 20 - font.width(data.powerpermb() + "") / 2f, 50, 4210752, false);

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
