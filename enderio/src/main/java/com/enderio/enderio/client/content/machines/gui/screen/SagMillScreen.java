package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.NewCapacitorEnergyWidget;
import com.enderio.enderio.client.foundation.widgets.NewProgressWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.sag_mill.SagMillMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nullable;
import java.util.List;

public class SagMillScreen extends MachineScreen<SagMillMenu> {
    public static final Identifier BG_TEXTURE = EnderIO.rl("textures/gui/screen/sag_mill.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 208;

    private static final Identifier PROGRESS_SPRITE = EnderIO.rl("screen/sag_mill/progress");
    private static final Identifier BALL_DURABILITY_SPRITE = EnderIOAPI.rl("screen/sag_mill/grinding_ball_durability");

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

        addRenderableOnly(NewProgressWidget.topDown(leftPos + 80, topPos + 47, 16, 24, PROGRESS_SPRITE, menu::getCraftingProgress, true));

        addRenderableOnly(new ActivityWidget(leftPos + 153, topPos + 89, menu::getMachineStates, true));

        addRenderableOnly(new NewCapacitorEnergyWidget(leftPos + 7, topPos + 27, menu::getEnergyStorage, menu::isCapacitorInstalled));

        addRenderableOnly(new GrindingBallWidget(142 + leftPos, 39 + topPos));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6 + 55, menu::getRedstoneControl, menu::setRedstoneControl,
            EIOCommonLang.REDSTONE_MODE));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 125, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 6 + 55 - 16 - 2, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
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
        public boolean mouseClicked(MouseButtonEvent p_447133_, boolean p_434606_) {
            return false;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }

        @Nullable private GrindingBallData tooltipDataCache;
        private float tooltipDuraCache;

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            float durability = menu.getGrindingBallDamage();
            var data = menu.getGrindingBallData();

            int yOffset = (int) Math.ceil(this.height * (1.0f - durability));

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BALL_DURABILITY_SPRITE, WIDTH, HEIGHT, 0, yOffset, getX(), getY() + yOffset, WIDTH, HEIGHT - yOffset);

            if (this.isHoveredOrFocused() && (tooltipDataCache != data || tooltipDuraCache != durability)) {
                tooltipDataCache = data;
                tooltipDuraCache = durability;

                MutableComponent tooltip = Component.empty().copy();

                if (durability > 0) {
                    // Build tooltip pieces
                    List<Component> tooltipComponents = List.of(
                        TooltipUtil.styledWithArgs(MachinesLang.SAG_MILL_GRINDING_BALL_REMAINING, (int) (durability * 100)),
                        MachinesLang.SAG_MILL_GRINDING_BALL_TITLE,
                        TooltipUtil.styledWithArgs(EIOCommonLang.GRINDINGBALL_MAIN_OUTPUT, (int) (data.outputMultiplier() * 100)),
                        TooltipUtil.styledWithArgs(EIOCommonLang.GRINDINGBALL_BONUS_OUTPUT, (int) (data.bonusMultiplier() * 100)),
                        TooltipUtil.styledWithArgs(EIOCommonLang.GRINDINGBALL_POWER_USE, (int) (data.powerUse() * 100)));

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
