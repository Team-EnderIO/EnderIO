package com.enderio.enderio.api.armory.upgrades.hooks;

import com.enderio.enderio.api.capacitor.CapacitorData;
import net.minecraft.world.item.ItemStack;

public interface DarkSteelHookContext {
    ItemStack itemStack();

    CapacitorData getCapacitorData();

    boolean consumeEnergy(int toConsume, boolean simulate);
}
