package com.enderio.enderio.client.content.glass;

import com.enderio.enderio.client.foundation.icon.EIOEnumIcons;
import com.enderio.enderio.content.glass.FusedQuartzBlock;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class GlassIconDecorator implements IItemDecorator {
    public static final GlassIconDecorator INSTANCE = new GlassIconDecorator();

    //TODO ensure Z is correct
    private static final float COUNT_BLIT_HEIGHT = 200;

    @Override
    public boolean render(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof FusedQuartzBlock block) {
                graphics.pose().pushMatrix();
                graphics.pose().translate(xOffset, yOffset);

                Identifier collisionSprite = EIOEnumIcons.GLASS_COLLISION_PREDICATE.get(block.getCollisionPredicate());
                if (collisionSprite != null) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, collisionSprite, 0, 0, 16, 16);
                }
                
                Identifier lightingSprite = EIOEnumIcons.GLASS_LIGHTING.get(block.getGlassLighting());
                if (lightingSprite != null) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, lightingSprite, 0, 0, 16, 16);
                }

                graphics.pose().popMatrix();
                return true;
            }
        }

        return false;
    }
}
