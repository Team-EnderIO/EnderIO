package com.enderio.base.client.decorator;

import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.block.glass.FusedQuartzBlock;
import com.enderio.core.client.gui.screen.EnderScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class GlassIconDecorator implements IItemDecorator {
    public static final GlassIconDecorator INSTANCE = new GlassIconDecorator();

    private static final float COUNT_BLIT_HEIGHT = 200;

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof FusedQuartzBlock block) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(xOffset, yOffset, COUNT_BLIT_HEIGHT - 1);

                EnderScreen.renderIcon(guiGraphics, new Vector2i(0,0), block.getCollisionPredicate());
                EnderScreen.renderIcon(guiGraphics, new Vector2i(0,0), block.getGlassLighting());

                guiGraphics.pose().popPose();
                return true;
            }
        }

        return false;
    }
}
