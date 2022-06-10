package com.enderio.base.common.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.List;
import java.util.Optional;

public class TagUtil {
    private static final List<String> ENDER_MODIDS = List.of(
        "enderio",
        "enderio_machines",
        "enderio_conduits" // TODO: Add all modules here.
    );

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
        Optional<Item> enderItem = tag.stream().filter(item -> ENDER_MODIDS.contains(item.getRegistryName().getNamespace())).findAny();
        if (enderItem.isPresent())
            return enderItem;

        // TODO: Search based on config precedence

        // Return the first thing in the stream.
        return tag.stream().findFirst();
    }
}
