package com.enderio.machines.client.gui.screen;

import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.client.gui.screen.EIOScreen;
import com.enderio.base.client.gui.widgets.EnumIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.TooltipUtil;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.gui.widget.EnergyWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.SagMillBlockEntity;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.SagMillMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SagMillScreen extends EIOScreen<SagMillMenu> {
    public static final ResourceLocation BG_TEXTURE = EIOMachines.loc("textures/gui/sagmill.png");
    public static final ResourceLocation BG_TEXTURE_SIMPLE = EIOMachines.loc("textures/gui/simple_sagmill.png");

    public SagMillScreen(SagMillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new ProgressWidget(this, () -> menu.getBlockEntity().getProgress(), getGuiLeft() + 81, getGuiTop() + 31, 15, 23, 202, 0, ProgressWidget.Direction.TOP_DOWN));

        addRenderableOnly(new EnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableOnly(new GrindingBallWidget(142 + leftPos, 23 + topPos));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
    }

    @Override
    protected ResourceLocation getBackgroundImage() {
        if (getMenu().getBlockEntity().getTier() == MachineTier.SIMPLE) {
            return BG_TEXTURE_SIMPLE;
        }
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }

    private class GrindingBallWidget extends AbstractWidget {
        private static final int U = 186;
        private static final int V = 31;
        private static final int WIDTH = 4;
        private static final int HEIGHT = 16;

        public GrindingBallWidget(int x, int y) {
            super(x, y, WIDTH, HEIGHT, TextComponent.EMPTY);
        }

        // Stop the click noise

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {}

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SagMillScreen.BG_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            float durability = SagMillScreen.this.getMenu().getBlockEntity().getGrindingBallDamage();

            int yOffset = (int) Math.ceil(this.height * (1.0f - durability));
            int height = (int) Math.ceil(this.height * durability);

            poseStack.pushPose();
            blit(poseStack, x, y + yOffset, U, V + yOffset, width, height);

            if (this.isHoveredOrFocused()) {
                this.renderToolTip(poseStack, mouseX, mouseY);
            }

            poseStack.popPose();
        }

        @Override
        public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
            if (isHovered && isActive()) {
                SagMillBlockEntity be = SagMillScreen.this.getMenu().getBlockEntity();
                float durability = be.getGrindingBallDamage();
                IGrindingBallData dat = be.getGrindingBallData();
                SagMillScreen.this.renderTooltip(poseStack, TooltipUtil.withArgs(MachineLang.SAG_MILL_GRINDINGBALL_TOOLTIP,
                    (int) (durability * 100), (int) (dat.getOutputMultiplier() * 100), (int) (dat.getBonusMultiplier() * 100), (int) (dat.getPowerUse() * 100)), mouseX, mouseY);
            }
        }
    }
}
