package com.enderio.armory.common.tag;

import com.enderio.EnderIOBase;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ArmoryTags {

    public static void register() {
        Blocks.init();
    }

    public static class Blocks {

        private static void init() {}

        public static final TagKey<Block> DARK_STEEL_TIER = BlockTags.create(EnderIOBase.loc("needs_dark_steel"));
        public static final TagKey<Block> INCORRECT_FOR_DARK_STEEL_TOOL = BlockTags.create(EnderIOBase.loc("incorrect_for_dark_steel_tool"));
        public static final TagKey<Block> DARK_STEEL_EXPLODABLE_DENY_LIST = BlockTags.create(EnderIOBase.loc("dark_steel_explodable_deny_list"));
        public static final TagKey<Block> DARK_STEEL_EXPLODABLE_ALLOW_LIST = BlockTags.create(EnderIOBase.loc("dark_steel_explodable_allow_list"));

    }
}
