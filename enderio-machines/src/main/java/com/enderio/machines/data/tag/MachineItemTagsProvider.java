package com.enderio.machines.data.tag;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.tag.MachineTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//TODO remove when regilite support
public class MachineItemTagsProvider extends ItemTagsProvider {

    private static final Map<TagKey<Item>, List<Item>> itemTags = new HashMap<>();
    private static final Map<TagKey<Item>, List<TagKey<Item>>> tagTags = new HashMap<>();

    public MachineItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, EnderIO.NAMESPACE, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MachineTags.ItemTags.CROPS, Tags.Items.CROPS);
        tag(MachineTags.ItemTags.SEEDS, Tags.Items.SEEDS);
        tag(MachineTags.ItemTags.MEAT, ItemTags.MEAT);
        tag(MachineTags.ItemTags.EXPLOSIVES, Items.TNT, Items.FIREWORK_STAR, Items.FIREWORK_ROCKET, Items.FIRE_CHARGE);
        tag(MachineTags.ItemTags.NATURAL_LIGHTS, Items.GLOWSTONE_DUST, Items.GLOWSTONE, Items.SEA_LANTERN,
                Items.SEA_PICKLE, Items.GLOW_LICHEN, Items.GLOW_BERRIES, Items.GLOW_INK_SAC);
        tag(MachineTags.ItemTags.SUNFLOWER, Items.SUNFLOWER);
        tag(MachineTags.ItemTags.BLAZE_POWDER, Items.BLAZE_POWDER);
        tag(MachineTags.ItemTags.AMETHYST, Items.AMETHYST_SHARD);
        tag(MachineTags.ItemTags.PRISMARINE, Items.PRISMARINE_SHARD);
        tag(MachineTags.ItemTags.CLOUD_COLD, Items.SNOW, Items.SNOW_BLOCK, Items.SNOWBALL, Items.ICE, Items.PACKED_ICE,
                Items.BLUE_ICE);
        tag(MachineTags.ItemTags.LIGHTNING_ROD, Items.LIGHTNING_ROD);
        tag(MachineTags.ItemTags.WIND_CHARGES, Items.WIND_CHARGE);

        itemTags.forEach((key, list) -> {
            var holder = tag(key);
            list.forEach(holder::add);
        });

        tagTags.forEach((key, tagKeys) -> {
            var holder = tag(key);
            tagKeys.forEach(holder::addTag);
        });
    }

    // helpers for tags. since tags can be added by external files, this prevents
    // duplicates.
    public static void tag(TagKey<Item> tag, Item item) {
        var items = itemTags.computeIfAbsent(tag, t -> new ArrayList<>());
        if (!items.contains(item))
            items.add(item);
    }

    public static void tag(TagKey<Item> tag, Item... itemList) {
        var items = itemTags.computeIfAbsent(tag, t -> new ArrayList<>());
        for (Item item : itemList) {
            if (!items.contains(item))
                items.add(item);
        }
    }

    public static void tag(TagKey<Item> tag, TagKey<Item> childTag) {
        var tags = tagTags.computeIfAbsent(tag, t -> new ArrayList<>());
        if (!tags.contains(childTag))
            tags.add(childTag);
    }
}
