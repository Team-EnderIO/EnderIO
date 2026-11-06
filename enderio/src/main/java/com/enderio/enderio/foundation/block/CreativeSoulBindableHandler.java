package com.enderio.enderio.foundation.block;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.BindableSoulSource;
import com.enderio.enderio.api.soul.binding.SoulBindable;
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
        ItemAccess usedItemAccess = ItemAccess.forPlayerInteraction(player, event.getHand());
        BindableSoulSource soulSource = usedItemAccess.getCapability(EnderIOCapabilities.BINDABLE_SOUL_SOURCE);
        if (soulSource == null) {
            return;
        }

        // See if the block can be soul bound
        Level level = event.getLevel();
        SoulBindable soulBindable = level.getCapability(EnderIOCapabilities.SOUL_BINDABLE_BLOCK, event.getPos());
        if (soulBindable == null) {
            return;
        }

        // Find a bindable soul (simulate)
        Soul bindableSoul;
        try (Transaction transaction = Transaction.openRoot()) {
            bindableSoul = soulSource.extractSoul(transaction);
        }

        if (bindableSoul.isEmpty()) {
            return;
        }

        // Attempt to bind the block using a soul in the storage.
        try (Transaction transaction = Transaction.openRoot()) {
            if (soulBindable.bindSoul(bindableSoul, transaction)) {
                transaction.commit();
                event.cancelWithResult(InteractionResult.SUCCESS);
            }
        }
    }
}
