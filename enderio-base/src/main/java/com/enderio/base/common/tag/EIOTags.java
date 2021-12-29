package com.enderio.base.common.tag;

import com.enderio.base.EnderIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.versions.forge.ForgeVersion;

public class EIOTags {

    public static void init() {
        Items.init();
        Blocks.init();
        Fluids.init();
    }

    public static class Items {

        private static void init() {}
    
        public static final IOptionalNamedTag<Item> WRENCH = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "tools/wrench"));

        public static final IOptionalNamedTag<Item> DUSTS_LAPIS = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/lapis"));
        public static final IOptionalNamedTag<Item> DUSTS_COAL = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/coal"));
        public static final IOptionalNamedTag<Item> DUSTS_IRON = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/iron"));
        public static final IOptionalNamedTag<Item> DUSTS_GOLD = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/gold"));
        public static final IOptionalNamedTag<Item> DUSTS_COPPER = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/copper"));
        public static final IOptionalNamedTag<Item> DUSTS_TIN = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/tin"));
        public static final IOptionalNamedTag<Item> DUSTS_ENDER = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/ender"));
        public static final IOptionalNamedTag<Item> DUSTS_OBSIDIAN = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/obsidian"));
        public static final IOptionalNamedTag<Item> DUSTS_ARDITE = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/ardite"));
        public static final IOptionalNamedTag<Item> DUSTS_COBALT = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/cobalt"));
        public static final IOptionalNamedTag<Item> DUSTS_QUARTZ = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/quartz"));
        public static final IOptionalNamedTag<Item> SILICON = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "silicon"));
        public static final IOptionalNamedTag<Item> GEARS = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "gears"));
        public static final IOptionalNamedTag<Item> GEARS_WOOD = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "gears/wood"));
        public static final IOptionalNamedTag<Item> GEARS_STONE = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "gears/stone"));
        public static final IOptionalNamedTag<Item> GEARS_IRON = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "gears/stone"));
        public static final IOptionalNamedTag<Item> GEARS_ENERGIZED = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "gears/energized"));
        public static final IOptionalNamedTag<Item> GEARS_VIBRANT = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "gears/vibrant"));
        public static final IOptionalNamedTag<Item> GEARS_DARK_STEEL = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "gears/dark_steel"));

        public static final IOptionalNamedTag<Item> FUSED_QUARTZ = ItemTags.createOptional(EnderIO.loc("fused_quartz"));
        public static final IOptionalNamedTag<Item> CLEAR_GLASS = ItemTags.createOptional(EnderIO.loc("clear_glass"));

        public static final IOptionalNamedTag<Item> BROKEN_SPAWNER_BLACKLIST = ItemTags.createOptional(EnderIO.loc("blacklists/broken_spawner"));
        public static final IOptionalNamedTag<Item> ELECTROMAGNET_BLACKLIST = ItemTags.createOptional(EnderIO.loc("blacklists/electromagnet"));

    }

    public static class Blocks {

        private static void init() {}

        public static final IOptionalNamedTag<Block> FUSED_QUARTZ = BlockTags.createOptional(EnderIO.loc("fused_quartz"));
        public static final IOptionalNamedTag<Block> CLEAR_GLASS = BlockTags.createOptional(EnderIO.loc("clear_glass"));
        public static final IOptionalNamedTag<Block> DARK_STEEL_TIER = BlockTags.createOptional(EnderIO.loc("needs_dark_steel"));
        public static final IOptionalNamedTag<Block> DARK_STEEL_EXPLODABLE_DENY_LIST = BlockTags.createOptional(EnderIO.loc("dark_steel_explodable_deny_list"));
        public static final IOptionalNamedTag<Block> DARK_STEEL_EXPLODABLE_ALLOW_LIST = BlockTags.createOptional(EnderIO.loc("dark_steel_explodable_allow_list"));

    }
    
    public static class Fluids {
        private static void init() {}

        public static final IOptionalNamedTag<Fluid> COLD_FIRE_IGNITER_FUEL = FluidTags.createOptional(EnderIO.loc("cold_fire_igniter_fuel"));

    }
}
