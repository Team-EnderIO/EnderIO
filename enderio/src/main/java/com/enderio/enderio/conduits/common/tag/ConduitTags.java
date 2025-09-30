package com.enderio.enderio.conduits.common.tag;

import com.enderio.EnderIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ConduitTags {

    public void init() {
        Items.init();
        Blocks.init();
    }

    public static class Items {

        private static void init() {}
    }

    public static class Blocks {

        private static void init() {}

        public static final TagKey<Block> REDSTONE_CONNECTABLE = BlockTags.create(EnderIO.rl("redstone_connectable"));
        public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "relocation_not_supported"));
    }
}
