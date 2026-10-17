package com.enderio.enderio.foundation.block;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.api.soul.storage.SoulHandler;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@EventBusSubscriber
public class CreativeSoulBindableHandler {
    @SubscribeEvent
    public static void onItemUse(UseItemOnBlockEvent event) {
        // Ensure the player is in creative mode.
        Player player = event.getPlayer();
        if (player == null || !player.getAbilities().instabuild) {
            return;
        }

        // See if the clicked item is a soul storage
//        ItemAccess usedItemAccess = ItemAccess.forPlayerInteraction(player, event.getHand());
        SoulHandler soulHandler = event.getItemStack().getCapability(EnderIOCapabilities.SOUL_HANDLER_ITEM);
        if (soulHandler == null) {
            return;
        }

        // See if the block can be soul bound
        Level level = event.getLevel();
        SoulBindable soulBindable = level.getCapability(EnderIOCapabilities.SOUL_BINDABLE_BLOCK, event.getPos());
        if (soulBindable == null) {
            return;
        }

        // Attempt to bind the block using a soul in the storage.
        try (Transaction transaction = Transaction.openRoot()) {
            for (int i = 0; i < soulHandler.getSlots(); i++) {
                Soul soul = soulHandler.getSoulInSlot(i);
                if (soul.isEmpty()) {
                    continue;
                }

                if (soulBindable.bindSoul(soul.copy(), transaction)) {
                    transaction.commit();
                    event.cancelWithResult(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
