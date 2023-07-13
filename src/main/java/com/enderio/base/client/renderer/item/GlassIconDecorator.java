package com.enderio.base.client.renderer.item;

import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.block.glass.FusedQuartzBlock;
import com.enderio.core.client.gui.screen.IEnderScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

public class GlassIconDecorator implements IItemDecorator {
    public static final GlassIconDecorator INSTANCE = new GlassIconDecorator();

    private static final float COUNT_BLIT_HEIGHT = 200;

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof FusedQuartzBlock block) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(xOffset, yOffset, COUNT_BLIT_HEIGHT - 1);

                IEnderScreen.renderIcon(guiGraphics, new Vector2i(0,0), block.getCollisionPredicate());
                IEnderScreen.renderIcon(guiGraphics, new Vector2i(0,0), block.getGlassLighting());

                guiGraphics.pose().popPose();
                return true;
            }
        }
        return false;
    }
}
