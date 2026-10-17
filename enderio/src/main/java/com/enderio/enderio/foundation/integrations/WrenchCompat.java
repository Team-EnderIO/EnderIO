package com.enderio.enderio.foundation.integrations;

import com.enderio.enderio.api.block.WrenchableBlockEntity;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.util.TriState;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@EventBusSubscriber
public class WrenchCompat {
    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event) {
        ItemStack itemInHand = event.getEntity().getItemInHand(event.getHand());
        // @formatter:off
        if (itemInHand.is(EIOTags.Items.WRENCH)
            && !itemInHand.is(EIOItems.YETA_WRENCH.get())
            && event.getLevel().getBlockEntity(event.getPos()) instanceof WrenchableBlockEntity) {
            // @formatter:on
            event.setUseBlock(TriState.TRUE);
            event.setUseItem(TriState.FALSE);
        }
    }
}
