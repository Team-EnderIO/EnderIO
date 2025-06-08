package com.enderio.base.common.compat.vanilla;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.storage.ISoulHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;

public class SpawnEggSoulHandler implements ISoulHandler {
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
        var customEntityData = spawnEgg.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
        if (!customEntityData.isEmpty()) {
            return new Soul(customEntityData.copyTag());
        }

        return Soul.of(spawnEggItem.getType(spawnEgg));
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
