package com.enderio.enderio.compat.vanilla;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;

public class SpawnEggSoulBindable implements SoulBindable {
    private final ItemStack spawnEgg;

    public SpawnEggSoulBindable(ItemStack spawnEgg) {
        this.spawnEgg = spawnEgg;
    }

    @Override
    public Soul getBoundSoul() {
        if (!(spawnEgg.getItem() instanceof SpawnEggItem)) {
            return Soul.EMPTY;
        }

        // Get custom entity data
        TypedEntityData<EntityType<?>> customEntityData = spawnEgg.get(DataComponents.ENTITY_DATA);
        if (customEntityData != null) {
            return new Soul(customEntityData.type(), customEntityData.copyTagWithoutId());
        }

        return Soul.EMPTY;
    }

    @Override
    public boolean canBind() {
        return false;
    }

    @Override
    public boolean isSoulValid(Soul soul) {
        return Soul.isSameEntity(getBoundSoul(), soul);
    }

    @Override
    public void bindSoul(Soul newSoul) {
        throw new UnsupportedOperationException("Cannot bind a spawn egg to a soul");
    }
}
