package com.enderio.conduits.common.tag;

import com.enderio.EnderIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ConduitTags {

    public void init() {
        Items.init();
        Blocks.init();
    }

    public static class Items {

        private static void init() {}

        public static final TagKey<Item> COVERED_DENSE_CABLE = ItemTags.create(new ResourceLocation("ae2:covered_dense_cable"));
        public static final TagKey<Item> COVERED_CABLE = ItemTags.create(new ResourceLocation("ae2:covered_cable"));
        public static final TagKey<Item> GLASS_CABLE = ItemTags.create(new ResourceLocation("ae2:glass_cable"));
    }

    public static class Blocks {

        private static void init() {}

        public static final TagKey<Block> REDSTONE_CONNECTABLE = BlockTags.create(EnderIO.loc("redstone_connectable"));
    }
}
