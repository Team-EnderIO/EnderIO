package com.enderio.machines.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.client.gui.widget.EIOCommonWidgets;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.NewProgressWidget;
import com.enderio.machines.common.blocks.powered_spawner.PoweredSpawnerMenu;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;

public class PoweredSpawnerScreen extends MachineScreen<PoweredSpawnerMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/screen/powered_spawner_spawn.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    private static final ResourceLocation PROGRESS_SPRITE = EnderIO.loc("screen/powered_spawner/progress");

    public PoweredSpawnerScreen(PoweredSpawnerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 6 - 16, topPos + 24, EIOLang.HIDE_RANGE,
                EIOLang.SHOW_RANGE, menu::isRangeVisible,
                (ignored) -> handleButtonPress(PoweredSpawnerMenu.VISIBILITY_BUTTON_ID)));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        addRenderableOnly(NewProgressWidget.bottomUp(leftPos + 82, topPos + 38, 14, 14, PROGRESS_SPRITE,
                menu::getSpawnProgress, true));
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
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
                guiGraphics.drawString(font, rl.get().toString(),
                        imageWidth / 2f - font.width(rl.get().toString()) / 2f, 15, 4210752, false);
            }
        }

        super.renderLabels(guiGraphics, pMouseX, pMouseY);
    }
}
