package com.enderio.enderio.foundation.block;

import com.enderio.enderio.foundation.block.entity.Wrenchable;
import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.UseItemOnBlockEvent;

@Mod.EventBusSubscriber
public class WrenchableBlockHandler {
    @SubscribeEvent
    public static void onItemUse(UseItemOnBlockEvent event) {
        var level = event.getLevel();

        if (!event.getItemStack().is(EIOTags.Items.WRENCH)) {
            return;
        }

        if (level.getBlockEntity(event.getPos()) instanceof Wrenchable blockEntity) {
            var result = blockEntity.onWrenched(event.getUseOnContext());
            if (result != InteractionResult.PASS) {
                event.cancelWithResult(result);
            }
        }
    }
}
