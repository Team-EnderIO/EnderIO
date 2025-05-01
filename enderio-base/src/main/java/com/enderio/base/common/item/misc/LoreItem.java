package com.enderio.base.common.item.misc;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class LoreItem extends Item {

    private final boolean hasGlint;
    private final Component loreTooltip;

    public LoreItem(Properties properties, boolean hasGlint, Component loreTooltip) {
        super(properties);
        this.hasGlint = hasGlint;
        this.loreTooltip = loreTooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(loreTooltip);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasGlint;
    }
}
