package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.core.client.gui.widgets.ToggleImageButton;
import com.enderio.core.common.util.Vector2i;
import com.enderio.machines.common.menu.VacuumChestMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VacuumChestScreen extends EIOScreen<VacuumChestMenu> {

    private static final ResourceLocation VACUMM_CHEST_BG = EnderIO.loc("textures/gui/vacuum_chest.png");
    private static final ResourceLocation BUTTONS = EnderIO.loc("textures/gui/icons/buttons.png");
    private static final ResourceLocation WIDGETS = EnderIO.loc("textures/gui/40/widgetsv2.png");

    public VacuumChestScreen(VacuumChestMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, true);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 14, topPos + 105 + 2, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
        addRenderableWidget(new ToggleImageButton<>(this, leftPos + imageWidth - 8 - 14 - 2 - 16, topPos + 105, 16, 16, 144, 176, 16, 0, WIDGETS,
            () -> menu.getBlockEntity().isShowingRange(), state -> menu.getBlockEntity().shouldShowRange(state),
            () -> menu.getBlockEntity().isShowingRange() ? EIOLang.HIDE_RANGE : EIOLang.SHOW_RANGE));
        addRenderableWidget(new ImageButton(leftPos + imageWidth - 8 - 8, topPos + 86, 8, 8, 8, 0, 16, BUTTONS, (b) -> menu.getBlockEntity().increaseRange()));
        addRenderableWidget(new ImageButton(leftPos + imageWidth - 8 - 8, topPos + 94, 8, 8, 8, 8, 16, BUTTONS, (b) -> menu.getBlockEntity().decreaseRange()));
    }

    @Override
    protected ResourceLocation getBackgroundImage() {
        return VACUMM_CHEST_BG;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 206);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.font.draw(pPoseStack, EIOLang.FILTER, 8, 74, 4210752);
        this.font.draw(pPoseStack, EIOLang.RANGE, this.imageWidth - 8 - this.font.width(EIOLang.RANGE), 74, 4210752);
        super.renderLabels(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
        font.draw(pPoseStack, this.getMenu().getBlockEntity().getRange() + "", leftPos + imageWidth - 8 - 8 - 10, topPos + 90, 0);
    }

}