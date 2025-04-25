package com.enderio.armory.common.tag;

import com.enderio.base.api.EnderIO;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ArmoryTags {

    public static void register() {
        Items.init();
        Blocks.init();
    }

    public static class Items {

        private static void init() {
        }

        public static final TagKey<Item> DARK_STEEL_UPGRADEABLE_PICKAXE = ItemTags
                .create(EnderIO.loc("dark_steel_upgradeable_pickaxe"));

        public static final TagKey<Item> DARK_STEEL_UPGRADEABLE_AXE = ItemTags
                .create(EnderIO.loc("dark_steel_upgradeable_axe"));

        public static final TagKey<Item> DARK_STEEL_UPGRADEABLE_SWORD = ItemTags
                .create(EnderIO.loc("dark_steel_upgradeable_sword"));

        public static final TagKey<Item> DARK_STEEL_UPGRADEABLE_CHESTPLATE = ItemTags
                .create(EnderIO.loc("dark_steel_upgradeable_chestplate"));

        public static final TagKey<Item> DARK_STEEL_UPGRADEABLE_LEGGINGS = ItemTags
                .create(EnderIO.loc("dark_steel_upgradeable_leggings"));

        public static final TagKey<Item> DARK_STEEL_UPGRADEABLE_HELMET = ItemTags
                .create(EnderIO.loc("dark_steel_upgradeable_helmet"));

        public static final TagKey<Item> DARK_STEEL_UPGRADEABLE_BOOTS = ItemTags
                .create(EnderIO.loc("dark_steel_upgradeable_boots"));

    }

    public static class Blocks {

        private static void init() {
        }

        public static final TagKey<Block> INCORRECT_FOR_DARK_STEEL_TOOL = BlockTags
                .create(EnderIO.loc("incorrect_for_dark_steel_tool"));

        public static final TagKey<Block> DARK_STEEL_EXPLODABLE_DENY_LIST = BlockTags
                .create(EnderIO.loc("dark_steel_explodable_deny_list"));

        public static final TagKey<Block> DARK_STEEL_EXPLODABLE_ALLOW_LIST = BlockTags
                .create(EnderIO.loc("dark_steel_explodable_allow_list"));

    }
}
