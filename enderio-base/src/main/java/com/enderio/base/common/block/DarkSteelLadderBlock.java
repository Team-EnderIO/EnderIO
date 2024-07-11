package com.enderio.base.common.block;

import com.enderio.base.common.lang.EIOLang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.LadderBlock;

import java.util.List;

public class DarkSteelLadderBlock extends LadderBlock {

    public DarkSteelLadderBlock(Properties properties) {
        super(properties);
    }

    // TODO: 20.6: Could be cool for Regilite to have a addTooltip method that does this via events to cut down on useless classes :)
    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pContext, pTooltip, pFlag);
        pTooltip.add(EIOLang.DARK_STEEL_LADDER_FASTER);
    }
}
