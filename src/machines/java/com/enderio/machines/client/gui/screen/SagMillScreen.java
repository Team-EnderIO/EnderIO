package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.grindingball.GrindingBallData;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.blockentity.SagMillBlockEntity;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.SagMillMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nullable;
import java.util.List;

public class SagMillScreen extends MachineScreen<SagMillMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/screen/sag_mill.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public SagMillScreen(SagMillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new ProgressWidget.TopDown(BG_TEXTURE, menu::getCraftingProgress, getGuiLeft() + 81, getGuiTop() + 31, 15, 23, 202, 0));

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage, menu::isCapacitorInstalled));

        addRenderableOnly(new GrindingBallWidget(142 + leftPos, 23 + topPos));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6, menu::getRedstoneControl, menu::setRedstoneControl,
            EIOLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    private class GrindingBallWidget extends AbstractWidget {
        private static final int U = 186;
        private static final int V = 31;
        private static final int WIDTH = 4;
        private static final int HEIGHT = 16;

        GrindingBallWidget(int x, int y) {
            super(x, y, WIDTH, HEIGHT, Component.empty());
        }

        // Stop the click noise

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

        @Nullable
        private GrindingBallData tooltipDataCache;
        private float tooltipDuraCache;

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            SagMillBlockEntity be = SagMillScreen.this.getMenu().getBlockEntity();
            if (be == null) {
                return;
            }

            float durability = be.getGrindingBallDamage();
            var data = be.getGrindingBallData();

            int yOffset = (int) Math.ceil(this.height * (1.0f - durability));
            int height = (int) Math.ceil(this.height * durability);

            guiGraphics.blit(SagMillScreen.BG_TEXTURE, getX(), getY() + yOffset, U, V + yOffset, width, height);

            if (this.isHoveredOrFocused() && (tooltipDataCache != data || tooltipDuraCache != durability)) {
                tooltipDataCache = data;
                tooltipDuraCache = durability;

                // Gather all parts of the tooltip
                List<Component> tooltipComponents = List.of(
                    TooltipUtil.styledWithArgs(MachineLang.SAG_MILL_GRINDINGBALL_REMAINING, (int) (durability * 100)),
                    MachineLang.SAG_MILL_GRINDINGBALL_TITLE,
                    TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_MAIN_OUTPUT, (int) (data.outputMultiplier() * 100)),
                    TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_BONUS_OUTPUT, (int) (data.bonusMultiplier() * 100)),
                    TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_POWER_USE, (int) (data.powerUse() * 100))
                );

                // Build single component
                MutableComponent tooltip = Component.empty().copy();
                for (int i = 0; i < tooltipComponents.size(); i++) {
                    tooltip.append(tooltipComponents.get(i));

                    if (i + 1 < tooltipComponents.size()) {
                        tooltip.append("\n");
                    }
                }

                // Set for display
                setTooltip(Tooltip.create(tooltip));
            }
        }
    }
}
