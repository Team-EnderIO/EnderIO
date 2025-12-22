package com.enderio.enderio.foundation.block;

import com.enderio.enderio.foundation.block.entity.Wrenchable;
import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@EventBusSubscriber
public class WrenchableBlockHandler {
    @SubscribeEvent
    public static void onItemUse(UseItemOnBlockEvent event) {
        var level = event.getLevel();

        if (!event.getItemStack().is(EIOTags.Items.WRENCH)) {
            return;
        }

        if (level.getBlockEntity(event.getPos()) instanceof Wrenchable blockEntity) {
            var result = blockEntity.onWrenched(event.getUseOnContext());
            if (result != InteractionResult.TRY_WITH_EMPTY_HAND) {
                event.cancelWithResult(result);
            }
        }
    }
}
