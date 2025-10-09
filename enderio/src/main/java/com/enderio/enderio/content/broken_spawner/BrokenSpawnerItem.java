package com.enderio.enderio.content.broken_spawner;

import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.foundation.util.EntityCaptureUtils;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BrokenSpawnerItem extends Item implements ICustomCreativeTabEntries {
    public BrokenSpawnerItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack forSoul(Soul soul) {
        ItemStack brokenSpawner = new ItemStack(EIOItems.BROKEN_SPAWNER.get());
        brokenSpawner.set(EIODataComponents.SOUL, soul);
        return brokenSpawner;
    }

    public static List<ItemStack> getPossibleStacks() {
        // Register for every mob that can be captured.
        List<ItemStack> items = new ArrayList<>();
        for (var entity : EntityCaptureUtils.getCapturableEntityTypes()) {
            items.add(forSoul(Soul.of(entity)));
        }
        return items;
    }

    // Hide default unbound variant from creative tab.
    @Override
    public boolean shouldAddDefaultItem() {
        return false;
    }
}
