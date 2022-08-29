package com.enderio.core.common.util;

import com.enderio.core.EnderCore;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.Optional;

public class TagUtil {
    /**
     * Get an optional item from a tag.
     * An optional item means the item may not actually be present, and if it isn't it is handled gracefully.
     * It will search tags in the following order:
     * - Pulls an enderio item first, if present
     * - Then goes down the tag looking in the order of modid precedence (from EnderIO config).
     * - If we found nothing in our specified lists, we will pick the first present item.
     */
    public static Optional<Item> getOptionalItem(TagKey<Item> tagKey) {
        ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(tagKey);

        // Search for an EnderIO item
        Optional<Item> enderItem = tag.stream().filter(item -> ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(EnderCore.MODID)).findFirst();
        if (enderItem.isPresent())
            return enderItem;

        // TODO: Search based on config precedence

        // Return the first thing in the stream.
        return tag.stream().findFirst();
    }
}
