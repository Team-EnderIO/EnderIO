package com.enderio.enderio.compat.vanilla;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;
import net.neoforged.neoforge.transfer.access.ItemAccess;
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
        return false;
    }

    @Override
    public boolean isSoulValid(Soul soul) {
        return Soul.isSameEntity(boundSoul(), soul);
    }

    @Override
    public boolean bindSoul(Soul newSoul, TransactionContext transaction) {
        return false;
    }
}
