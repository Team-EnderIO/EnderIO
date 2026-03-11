package com.enderio.enderio.foundation.integrations;

import com.enderio.enderio.foundation.block.entity.Wrenchable;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.util.TriState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@Mod.EventBusSubscriber
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
