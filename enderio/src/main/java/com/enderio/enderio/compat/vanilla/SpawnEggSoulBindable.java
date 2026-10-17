package com.enderio.enderio.compat.vanilla;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SpawnEggSoulBindable implements SoulBindable {
    private final ItemAccess itemAccess;

    public SpawnEggSoulBindable(ItemAccess itemAccess) {
        this.itemAccess = itemAccess;
    }

    @Override
    public Soul boundSoul() {
        if (!(itemAccess.getResource().getItem() instanceof SpawnEggItem)) {
            return Soul.EMPTY;
        }

        // Get custom entity data
        TypedEntityData<EntityType<?>> customEntityData = itemAccess.getResource().get(DataComponents.ENTITY_DATA);
        if (customEntityData != null) {
            return new Soul(customEntityData.type(), customEntityData.copyTagWithoutId());
        }

        return Soul.EMPTY;
    }

    @Override
    public boolean canBeBound() {
        return true;
    }

    @Override
    public boolean isSoulValid(Soul soul) {
        // Can only ever have a soul bound to it,
        if (soul.isEmpty() || soul.entityType() == null) {
            return false;
        }

        // Must have a spawn egg representation.
        return SpawnEggItem.byId(soul.entityType()).isPresent();
    }

    @Override
    public boolean bindSoul(Soul newSoul, TransactionContext transaction) {
        if (!canBeBound() || !isSoulValid(newSoul) || newSoul.entityType() == null) {
            return false;
        }

        ItemResource newEggResource = ItemResource.of(SpawnEggItem.byId(newSoul.entityType()).orElseThrow());
        return itemAccess.exchange(newEggResource, 1, transaction) == 1;
    }
}
