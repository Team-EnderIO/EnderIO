package com.enderio.enderio.compat.vanilla;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.BindableSoulSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SpawnEggBindableSoulSource implements BindableSoulSource {
    private final Item validItem;
    private final ItemAccess itemAccess;

    public SpawnEggBindableSoulSource(ItemAccess itemAccess) {
        this.itemAccess = itemAccess;
        this.validItem = itemAccess.getResource().getItem();
    }

    @Override
    public boolean isSoulAvailable() {
        return itemAccess.getResource().is(validItem);
    }

    @Override
    public Soul availableSoul() {
        ItemResource resource = itemAccess.getResource();
        if (!resource.is(validItem)) {
            return Soul.EMPTY;
        }

        if (!(resource.getItem() instanceof SpawnEggItem)) {
            return Soul.EMPTY;
        }

        // Get custom entity data
        TypedEntityData<EntityType<?>> customEntityData = resource.get(DataComponents.ENTITY_DATA);
        if (customEntityData != null) {
            return new Soul(customEntityData.type(), customEntityData.copyTagWithoutId());
        }

        return Soul.EMPTY;
    }

    @Override
    public Soul extractSoul(TransactionContext transaction) {
        Soul resultSoul = availableSoul().copy();
        if (itemAccess.exchange(ItemResource.of(Items.EGG), 1, transaction) == 1) {
            return resultSoul;
        }

        return Soul.EMPTY;
    }
}
