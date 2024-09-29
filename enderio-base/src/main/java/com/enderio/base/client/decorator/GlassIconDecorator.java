package com.enderio.base.client.decorator;

import com.enderio.base.client.gui.icon.EIOEnumIcons;
import com.enderio.base.common.block.glass.FusedQuartzBlock;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
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

                ResourceLocation collisionSprite = EIOEnumIcons.GLASS_COLLISION_PREDICATE.get(block.getCollisionPredicate());
                if (collisionSprite != null) {
                    guiGraphics.blitSprite(collisionSprite, 0, 0, 16, 16);
                }
                
                ResourceLocation lightingSprite = EIOEnumIcons.GLASS_LIGHTING.get(block.getGlassLighting());
                if (lightingSprite != null) {
                    guiGraphics.blitSprite(lightingSprite, 0, 0, 16, 16);
                }

                guiGraphics.pose().popPose();
                return true;
            }
        }

        return false;
    }
}
