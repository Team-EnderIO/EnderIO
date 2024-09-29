package com.enderio.core.common.integration;

import com.almostreliable.unified.api.AlmostUnified;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class AlmostUnifiedIntegration {
    public static boolean isLoaded() {
        return ModList.get().isLoaded("almostunified");
    }

    public static @Nullable Item getTagTargetItem(TagKey<Item> tag) {
        if (isLoaded()) {
            return Adapter.getTagTargetItem(tag);
        }

        return null;
    }

    private static class Adapter {
        private static @Nullable Item getTagTargetItem(TagKey<Item> tag) {
            return AlmostUnified.INSTANCE.getTagTargetItem(tag);
        }
    }
}
