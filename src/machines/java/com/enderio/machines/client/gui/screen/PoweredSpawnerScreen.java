package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.core.common.util.Vector2i;
import com.enderio.machines.client.gui.widget.EnergyWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity;
import com.enderio.machines.common.menu.PoweredSpawnerMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class PoweredSpawnerScreen extends EIOScreen<PoweredSpawnerMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/powered_spawner_spawn.png");
    private static final ResourceLocation RANGE_BUTTON_TEXTURE = EnderIO.loc("textures/gui/icons/range_buttons.png");


    public PoweredSpawnerScreen(PoweredSpawnerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new ProgressWidget.BottomUp(this, () -> menu.getBlockEntity().getProgress(), getGuiLeft() + 82, getGuiTop() + 38, 14, 14, 176, 0));

        addRenderableOnly(new EnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableWidget(new ToggleImageButton<>(this, leftPos + imageWidth - 8 - 13, topPos + 20, 16, 16, 0, 0, 16, 0, RANGE_BUTTON_TEXTURE,
            () -> menu.getBlockEntity().isShowingRange(), state -> menu.getBlockEntity().shouldShowRange(state),
            () -> menu.getBlockEntity().isShowingRange() ? EIOLang.HIDE_RANGE : EIOLang.SHOW_RANGE));
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
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
        Optional<ResourceLocation> rl = getMenu().getBlockEntity().getEntityType();
        if (rl.isPresent()) {
            Optional<EntityType<?>> typeOptional = Registry.ENTITY_TYPE.getOptional(rl.get());
            if (typeOptional.isPresent()) {
                String name = typeOptional.get().getDescription().getString();
                font.draw(pPoseStack, name, leftPos + imageWidth/2f - font.width(name)/2f, topPos + 15, 0);
            }
        }
        if (getMenu().getBlockEntity().getReason() != PoweredSpawnerBlockEntity.SpawnerBlockedReason.NONE) {
            font.draw(pPoseStack, getMenu().getBlockEntity().getReason().getComponent().getString(), leftPos + imageWidth/2f - font.width(getMenu().getBlockEntity().getReason().getComponent().getString())/2f, topPos + 26, 0);
        }
    }
}
