package com.enderio.base.common.block.glass;

import com.enderio.base.client.gui.screen.IEnderScreen;
import com.enderio.base.client.renderer.item.IItemOverlayRender;
import com.enderio.base.common.util.Vector2i;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FusedQuartzItem extends BlockItem implements IItemOverlayRender {
    public FusedQuartzItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void renderOverlay(ItemStack pStack, int pXPosition, int pYPosition, PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.translate(0,0,IItemOverlayRender.BLIT_HEIGHT_COUNT - 1);
        if (getBlock() instanceof FusedQuartzBlock block) {
            IEnderScreen.renderIcon(poseStack, new Vector2i(0,0), block.getCollisionPredicate());
            IEnderScreen.renderIcon(poseStack, new Vector2i(0,0), block.getGlassLighting());
        }
        poseStack.popPose();
    }
}
