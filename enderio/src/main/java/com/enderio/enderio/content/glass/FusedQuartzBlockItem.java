package com.enderio.enderio.content.glass;

import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class FusedQuartzBlockItem extends BlockItem {
    public FusedQuartzBlockItem(FusedQuartzBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    public FusedQuartzBlock getBlock() {
        return (FusedQuartzBlock) super.getBlock();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        GlassIdentifier glassIdentifier = getBlock().glassIdentifier();

        if (glassIdentifier.explosionResistance()) {
            tooltipAdder.accept(EIOCommonLang.BLOCK_BLAST_RESISTANT);
        }

        if (glassIdentifier.lighting() == GlassLighting.EMITTING) {
            tooltipAdder.accept(GlassLang.EMITS_LIGHT);
        }

        if (glassIdentifier.lighting() == GlassLighting.BLOCKING) {
            tooltipAdder.accept(GlassLang.BLOCKS_LIGHT);
        }

        Component collisionTooltip = glassIdentifier.collisionPredicate().getComponent();
        if (collisionTooltip != null) {
            tooltipAdder.accept(collisionTooltip);
        }
    }
}
