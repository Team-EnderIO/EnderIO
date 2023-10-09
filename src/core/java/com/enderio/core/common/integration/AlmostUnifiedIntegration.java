package com.enderio.core.common.integration;

import com.almostreliable.unified.api.AlmostUnifiedLookup;
import com.enderio.api.integration.Integration;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class AlmostUnifiedIntegration implements Integration {
    @Nullable
    public Item getPreferredItemForTag(TagKey<Item> tagKey) {
        return AlmostUnifiedLookup.INSTANCE.getPreferredItemForTag(tagKey);
    }
}
