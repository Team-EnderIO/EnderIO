package com.enderio.base.common.integrations;

import com.enderio.base.common.blockentity.IWrenchable;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EnderIOWrenchCompat {
    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event) {
        ItemStack itemInHand = event.getEntity().getItemInHand(event.getHand());
        if (itemInHand.is(EIOTags.Items.WRENCH) && !itemInHand.is(EIOItems.YETA_WRENCH.asItem()) && event
            .getLevel()
            .getBlockEntity(event.getPos()) instanceof IWrenchable) {
            event.setUseBlock(Event.Result.ALLOW);
            event.setUseItem(Event.Result.DENY);
        }
    }
}
