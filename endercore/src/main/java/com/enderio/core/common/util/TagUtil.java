package com.enderio.core.common.util;

import com.enderio.core.EnderCore;
import com.enderio.core.common.integration.AlmostUnifiedIntegration;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TagUtil {
    /**
     * Get an optional item from a tag.
     * An optional item means the item may not actually be present, and if it isn't it is handled gracefully.
     * It will search tags in the following order:
     * - If Almost Unified is present, it will get the priority item from the tag.
     * - Pulls an enderio item first, if present
     * - Then goes down the tag looking in the order of modid precedence (from EnderIO config).
     * - If we found nothing in our specified lists, we will pick the first present item.
     */
    // TODO: Uses hardcoded enderio modid, clearly needs to live in base, not core.
    public static Optional<Item> getOptionalItem(TagKey<Item> tagKey) {
        Item preferredItem = AlmostUnifiedIntegration.getTagTargetItem(tagKey);
        if (preferredItem != null) {
            return Optional.of(preferredItem);
        }

        Optional<HolderSet.Named<Item>> tag = BuiltInRegistries.ITEM.getTag(tagKey);

        if (tag.isEmpty()) {
            return Optional.empty();
        }

        // Search for an EnderIO item
        Optional<Item> enderItem = tag.get()
                .stream()
                .filter(itemHolder -> BuiltInRegistries.ITEM.getKey(itemHolder.value())
                        .getNamespace()
                        .equals("enderio"))
                .map(Holder::value)
                .findFirst();

        if (enderItem.isPresent()) {
            return enderItem;
        }

        // TODO: Search based on config precedence

        // Return the first thing in the stream.
        return tag.get().stream().map(Holder::value).findFirst();
    }
}
