package com.enderio.enderio.compat.vanilla;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Enables binding of an egg into a spawn egg.
 * TODO: Our own 'empty spawn egg' item to do this instead of a vanilla egg.
 */
public class EggSoulBindable implements SoulBindable {
    private final ItemAccess itemAccess;

    public EggSoulBindable(ItemAccess itemAccess) {
        this.itemAccess = itemAccess;
    }

    @Override
    public Soul boundSoul() {
        return Soul.EMPTY;
    }

    @Override
    public boolean canBeBound() {
        return itemAccess.getResource().is(Items.EGG);
    }

    @Override
    public boolean isSoulValid(Soul soul) {
        return !soul.isEmpty() && soul.entityType() != null && SpawnEggItem.byId(soul.entityType()).isPresent();
    }

    @Override
    public boolean bindSoul(Soul newSoul, TransactionContext transaction) {
        if (!canBeBound() || !isSoulValid(newSoul) || newSoul.entityType() == null) {
            return false;
        }

        return itemAccess.exchange(ItemResource.of(SpawnEggItem.byId(newSoul.entityType()).orElseThrow()), 1, transaction) == 1;
    }
}
