package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.client.gui.widget.RedstoneControlIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.menu.PoweredSpawnerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class PoweredSpawnerScreen extends MachineScreen<PoweredSpawnerMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/powered_spawner_spawn.png");
    private static final ResourceLocation RANGE_BUTTON_TEXTURE = EnderIO.loc("textures/gui/icons/range_buttons.png");

    public PoweredSpawnerScreen(PoweredSpawnerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, getMenu().getBlockEntity()::isCapacitorInstalled, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new RedstoneControlIconWidget(leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableWidget(new ToggleImageButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, 0, 0, 16, 0, RANGE_BUTTON_TEXTURE,
            () -> menu.getBlockEntity().isRangeVisible(), state -> menu.getBlockEntity().setRangeVisible(state),
            () -> menu.getBlockEntity().isRangeVisible() ? EIOLang.HIDE_RANGE : EIOLang.SHOW_RANGE));

        addRenderableWidget(new ActivityWidget(this, menu.getBlockEntity()::getMachineStates, leftPos + imageWidth - 6 - 16, topPos + 16 * 4));

        addRenderableOnly(new ProgressWidget.BottomUp(this, () -> menu.getBlockEntity().getSpawnProgress(), getGuiLeft() + 82, getGuiTop() + 38, 14, 14, 176, 0));

    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        Optional<ResourceLocation> rl = getMenu().getBlockEntity().getEntityType();
        if (rl.isPresent()) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(rl.get());
            if (BuiltInRegistries.ENTITY_TYPE.getKey(type).equals(rl.get())) { // check we don't get the default pig
                String name = type.getDescription().getString();
                guiGraphics.drawString(font, name, imageWidth / 2f - font.width(name) / 2f, 15, 4210752, false);
            } else {
                guiGraphics.drawString(font, rl.get().toString(), imageWidth / 2f - font.width(rl.get().toString()) / 2f, 15, 4210752, false);
            }
        }
    }
}
