package com.enderio.enderio.foundation.soul;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

public class SoulUtility {
    public static ItemStack getStackForDisplay(Soul soul) {
        // Show spawn egg if available - this is easier to identify visually.
        ItemStack renderItem;
        var spawnEgg = SpawnEggItem.byId(soul.entityType());
        if (spawnEgg != null) {
            renderItem = spawnEgg.getDefaultInstance();
        } else {
            renderItem = SoulVialItem.forSoul(soul);
        }

        return renderItem;
    }
}
