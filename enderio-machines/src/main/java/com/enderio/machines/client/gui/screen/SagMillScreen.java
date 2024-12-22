package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIOBase;
import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.NewCapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.NewProgressWidget;
import com.enderio.machines.common.blocks.sag_mill.SagMillMenu;
import com.enderio.machines.common.lang.MachineLang;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SagMillScreen extends MachineScreen<SagMillMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIOBase.loc("textures/gui/screen/sag_mill.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 208;

    private static final ResourceLocation PROGRESS_SPRITE = EnderIOBase.loc("screen/sag_mill/progress");
    private static final ResourceLocation BALL_DURABILITY_SPRITE = EnderIOBase
            .loc("screen/sag_mill/grinding_ball_durability");

    public SagMillScreen(SagMillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
        shouldRenderLabels = true;

        titleLabelY = 6 + 2;
        inventoryLabelY = 115;
    }

    @Override
    protected void init() {
        super.init();
        centerAlignTitleLabelX();

        addRenderableOnly(NewProgressWidget.topDown(leftPos + 80, topPos + 47, 16, 24, PROGRESS_SPRITE,
                menu::getCraftingProgress, true));

        addRenderableOnly(new ActivityWidget(leftPos + 153, topPos + 89, menu::getMachineStates, true));

        addRenderableOnly(new NewCapacitorEnergyWidget(leftPos + 7, topPos + 27, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableOnly(new GrindingBallWidget(142 + leftPos, 39 + topPos));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6 + 55,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 125, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 6 + 55 - 16 - 2, overlay);
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
        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }

        @Nullable
        private GrindingBallData tooltipDataCache;
        private float tooltipDuraCache;

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            float durability = menu.getGrindingBallDamage();
            var data = menu.getGrindingBallData();

            int yOffset = (int) Math.ceil(this.height * (1.0f - durability));

            guiGraphics.blitSprite(BALL_DURABILITY_SPRITE, WIDTH, HEIGHT, 0, yOffset, getX(), getY() + yOffset, WIDTH,
                    HEIGHT - yOffset);

            if (this.isHoveredOrFocused() && (tooltipDataCache != data || tooltipDuraCache != durability)) {
                tooltipDataCache = data;
                tooltipDuraCache = durability;

                MutableComponent tooltip = Component.empty().copy();

                if (durability > 0) {
                    // Build tooltip pieces
                    List<Component> tooltipComponents = List.of(
                            TooltipUtil.styledWithArgs(
                                    MachineLang.SAG_MILL_GRINDINGBALL_REMAINING, (int) (durability * 100)),
                            MachineLang.SAG_MILL_GRINDINGBALL_TITLE,
                            TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_MAIN_OUTPUT,
                                    (int) (data.outputMultiplier() * 100)),
                            TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_BONUS_OUTPUT,
                                    (int) (data.bonusMultiplier() * 100)),
                            TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_POWER_USE, (int) (data.powerUse() * 100)));

                    // Combine together.
                    for (int i = 0; i < tooltipComponents.size(); i++) {
                        tooltip.append(tooltipComponents.get(i));

                        if (i + 1 < tooltipComponents.size()) {
                            tooltip.append("\n");
                        }
                    }
                }

                // Set for display
                setTooltip(Tooltip.create(tooltip));
            }
        }
    }
}
