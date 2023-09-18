package com.enderio.core.common.compat;

import com.almostreliable.unified.api.AlmostUnifiedLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class AlmostUnifiedCompat {

    private static boolean isLoaded() {
        return ModList.get().isLoaded("almostunified");
    }

    @Nullable
    public static Item getPreferredItemForTag(TagKey<Item> tagKey) {
        if (isLoaded()) {
            return Adapter.getPreferredItemForTag(tagKey);
        }

        return null;
    }

    private static class Adapter {
        private static Item getPreferredItemForTag(TagKey<Item> tagKey) {
            return AlmostUnifiedLookup.INSTANCE.getPreferredItemForTag(tagKey);
        }
    }
}
