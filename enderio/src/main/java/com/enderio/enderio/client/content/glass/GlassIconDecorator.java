package com.enderio.enderio.client.content.glass;

import com.enderio.enderio.client.foundation.icon.EIOEnumIcons;
import com.enderio.enderio.content.glass.FusedQuartzBlock;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class GlassIconDecorator implements IItemDecorator {
    public static final GlassIconDecorator INSTANCE = new GlassIconDecorator();

    //TODO ensure Z is correct
    private static final float COUNT_BLIT_HEIGHT = 200;

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof FusedQuartzBlock block) {
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(xOffset, yOffset);

                ResourceLocation collisionSprite = EIOEnumIcons.GLASS_COLLISION_PREDICATE.get(block.getCollisionPredicate());
                if (collisionSprite != null) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, collisionSprite, 0, 0, 16, 16);
                }
                
                ResourceLocation lightingSprite = EIOEnumIcons.GLASS_LIGHTING.get(block.getGlassLighting());
                if (lightingSprite != null) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, lightingSprite, 0, 0, 16, 16);
                }

                guiGraphics.pose().popMatrix();
                return true;
            }
        }

        return false;
    }
}
