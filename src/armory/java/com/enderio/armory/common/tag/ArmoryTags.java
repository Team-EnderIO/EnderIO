package com.enderio.armory.common.tag;

import com.enderio.EnderIO;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ArmoryTags {

    public static void register() {
        Blocks.init();
    }

    public static class Blocks {

        private static void init() {}

        public static final TagKey<Block> DARK_STEEL_TIER = BlockTags.create(EnderIO.loc("needs_dark_steel"));
        public static final TagKey<Block> DARK_STEEL_EXPLODABLE_DENY_LIST = BlockTags.create(EnderIO.loc("dark_steel_explodable_deny_list"));
        public static final TagKey<Block> DARK_STEEL_EXPLODABLE_ALLOW_LIST = BlockTags.create(EnderIO.loc("dark_steel_explodable_allow_list"));

    }
}
