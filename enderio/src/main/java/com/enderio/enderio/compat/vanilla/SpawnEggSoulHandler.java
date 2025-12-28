package com.enderio.enderio.compat.vanilla;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.storage.SoulHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TypedEntityData;

public class SpawnEggSoulHandler implements SoulHandler {
    private final ItemStack spawnEgg;

    public SpawnEggSoulHandler(ItemStack spawnEgg) {
        this.spawnEgg = spawnEgg;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public Soul getSoulInSlot(int slot) {
        if (slot > 0) {
            return Soul.EMPTY;
        }

        if (!(spawnEgg.getItem() instanceof SpawnEggItem spawnEggItem)) {
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
    public boolean tryInsertSoul(Soul soul, boolean isSimulate) {
        return false;
    }

    @Override
    public Soul tryExtractSoul(boolean isSimulate) {
        return getSoulInSlot(0);
    }

    @Override
    public boolean isSoulValid(int slot, Soul soul) {
        return false;
    }
}
