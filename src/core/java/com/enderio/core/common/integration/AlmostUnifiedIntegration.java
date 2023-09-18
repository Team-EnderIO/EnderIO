package com.enderio.core.common.integration;

import com.almostreliable.unified.api.AlmostUnifiedLookup;
import com.enderio.api.integration.Integration;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class AlmostUnifiedIntegration implements Integration {

    private boolean isLoaded() {
        return ModList.get().isLoaded("almostunified");
    }

    @Nullable
    public Item getPreferredItemForTag(TagKey<Item> tagKey) {
        if (isLoaded()) {
            return AlmostUnifiedLookup.INSTANCE.getPreferredItemForTag(tagKey);
        }

        return null;
    }
}
