package com.enderio.enderio.foundation.soul;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

public class SoulUtility {
    public static ItemStack getStackForDisplay(Soul soul) {
        // Show spawn egg if available - this is easier to identify visually.
        return SpawnEggItem.byId(soul.entityType())
            .map(ItemStack::new)
            .orElse(SoulVialItem.forSoul(soul));
    }
}
