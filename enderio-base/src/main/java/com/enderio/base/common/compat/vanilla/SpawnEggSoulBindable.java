package com.enderio.base.common.compat.vanilla;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.binding.ISoulBindable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;

public class SpawnEggSoulBindable implements ISoulBindable {
    private final ItemStack spawnEgg;

    public SpawnEggSoulBindable(ItemStack spawnEgg) {
        this.spawnEgg = spawnEgg;
    }

    @Override
    public Soul getBoundSoul() {
        if (!(spawnEgg.getItem() instanceof SpawnEggItem spawnEggItem)) {
            return Soul.EMPTY;
        }

        // Get custom entity data
        var customEntityData = spawnEgg.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
        if (!customEntityData.isEmpty()) {
            return new Soul(customEntityData.copyTag());
        }

        return Soul.of(spawnEggItem.getType(spawnEgg));
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
