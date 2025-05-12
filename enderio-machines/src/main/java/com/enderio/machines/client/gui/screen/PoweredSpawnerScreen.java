package com.enderio.machines.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.client.gui.widget.EIOCommonWidgets;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.NewCapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.NewProgressWidget;
import com.enderio.machines.client.gui.widget.PoweredSpawnerModeWidget;
import com.enderio.machines.common.blocks.powered_spawner.PoweredSpawnerMenu;
import com.enderio.machines.common.lang.MachineEnumLang;
import com.enderio.machines.common.lang.MachineLang;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;

public class PoweredSpawnerScreen extends MachineScreen<PoweredSpawnerMenu> {

    public static final ResourceLocation BG_TEXTURE_SPAWN = EnderIO
            .loc("textures/gui/screen/powered_spawner_spawn.png");
    public static final ResourceLocation BG_TEXTURE_CAPTURE = EnderIO
            .loc("textures/gui/screen/powered_spawner_capture.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 187;

    private static final ResourceLocation SPAWN_PROGRESS_SPRITE = EnderIO.loc("screen/powered_spawner/spawn_progress");
    private static final ResourceLocation CAPTURE_PROGRESS_SPRITE = EnderIO
            .loc("screen/powered_spawner/capture_progress");

    private NewProgressWidget spawnProgress;
    private NewProgressWidget captureProgress;

    public PoweredSpawnerScreen(PoweredSpawnerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = WIDTH;
        imageHeight = HEIGHT;

        shouldRenderLabels = true;

        titleLabelY = 6 + 2;
        inventoryLabelY = 94;
    }

    @Override
    protected void init() {
        super.init();
        centerAlignTitleLabelX();

        addRenderableOnly(new NewCapacitorEnergyWidget(7 + leftPos, 6 + topPos, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 6 - 16, topPos + 6 + (16 + 2),
                EIOLang.HIDE_RANGE, EIOLang.SHOW_RANGE, menu::isRangeVisible,
                (ignored) -> handleButtonPress(PoweredSpawnerMenu.VISIBILITY_BUTTON_ID)));

        addRenderableWidget(new PoweredSpawnerModeWidget(leftPos + imageWidth - 6 - 16 - 18, topPos + 6, menu::getMode,
                menu::setMode, MachineLang.POWERED_SPAWNER_MODE));

        addRenderableWidget(new ActivityWidget(152 + leftPos, 68 + topPos, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 93, 162, 87);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 6 + (16 + 2) * 2, overlay);

        spawnProgress = addRenderableOnly(NewProgressWidget.bottomUp(leftPos + 82, topPos + 38, 14, 14,
                SPAWN_PROGRESS_SPRITE, menu::getSpawnProgress, true));

        captureProgress = addRenderableOnly(NewProgressWidget.leftRight(leftPos + 75, topPos + 43, 24, 16,
                CAPTURE_PROGRESS_SPRITE, menu::getSpawnProgress, true));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        switch (menu.getMode()) {
        case SPAWN -> {
            spawnProgress.visible = true;
            captureProgress.visible = false;
        }
        case CAPTURE -> {
            spawnProgress.visible = false;
            captureProgress.visible = true;
        }
        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(getBackgroundTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    private ResourceLocation getBackgroundTexture() {
        return switch (menu.getMode()) {
        case SPAWN -> BG_TEXTURE_SPAWN;
        case CAPTURE -> BG_TEXTURE_CAPTURE;
        };
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        String modeLabel = Objects.requireNonNull(MachineEnumLang.POWERED_SPAWNER_MODE.get(menu.getMode())).getString();
        guiGraphics.drawString(font, modeLabel, imageWidth / 2f - font.width(modeLabel) / 2f, 25, 0xFFFFFFFF, true);

        var entityType = getMenu().getBlockEntity().getEntityType();
        if (entityType != null) {
            String name = entityType.getDescription().getString();
            guiGraphics.drawString(font, name, imageWidth / 2f - font.width(name) / 2f, 65, 0xFFFFFFFF, true);
        }

        super.renderLabels(guiGraphics, pMouseX, pMouseY);
    }
}
