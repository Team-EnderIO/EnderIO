package com.enderio.enderio.common.integrations;

import com.enderio.enderio.common.blockentity.Wrenchable;
import com.enderio.enderio.common.init.EIOItems;
import com.enderio.enderio.common.tag.EIOTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@EventBusSubscriber
public class WrenchCompat {
    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event) {
        ItemStack itemInHand = event.getEntity().getItemInHand(event.getHand());
        // @formatter:off
        if (itemInHand.is(EIOTags.Items.WRENCH)
            && !itemInHand.is(EIOItems.YETA_WRENCH.get())
            && event.getLevel().getBlockEntity(event.getPos()) instanceof Wrenchable) {
            // @formatter:on
            event.setUseBlock(TriState.TRUE);
            event.setUseItem(TriState.FALSE);
        }
    }
}
