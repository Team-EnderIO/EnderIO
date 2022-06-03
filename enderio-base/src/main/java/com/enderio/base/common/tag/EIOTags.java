package com.enderio.base.common.tag;

import com.enderio.base.EnderIO;
import com.enderio.base.common.block.glass.GlassBlocks;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.block.glass.GlassIdentifier;
import com.enderio.base.common.block.glass.GlassLighting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.versions.forge.ForgeVersion;

import java.util.HashMap;
import java.util.Map;

public class EIOTags {

    public static void classload() {
        Items.init();
        Blocks.init();
        Fluids.init();
    }

    public static class Items {

        private static void init() {}
    
        public static final TagKey<Item> WRENCH = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "tools/wrench"));

        public static final TagKey<Item> DUSTS_LAPIS = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/lapis"));
        public static final TagKey<Item> DUSTS_COAL = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/coal"));
        public static final TagKey<Item> DUSTS_IRON = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/iron"));
        public static final TagKey<Item> DUSTS_GOLD = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/gold"));
        public static final TagKey<Item> DUSTS_COPPER = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/copper"));
        public static final TagKey<Item> DUSTS_TIN = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/tin"));
        public static final TagKey<Item> DUSTS_ENDER = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/ender"));
        public static final TagKey<Item> DUSTS_OBSIDIAN = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/obsidian"));
        public static final TagKey<Item> DUSTS_ARDITE = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/ardite"));
        public static final TagKey<Item> DUSTS_COBALT = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/cobalt"));
        public static final TagKey<Item> DUSTS_QUARTZ = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/quartz"));
        public static final TagKey<Item> SILICON = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "silicon"));
        public static final TagKey<Item> GEARS = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "gears"));
        public static final TagKey<Item> GEARS_WOOD = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "gears/wood"));
        public static final TagKey<Item> GEARS_STONE = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "gears/stone"));
        public static final TagKey<Item> GEARS_IRON = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "gears/stone"));
        public static final TagKey<Item> GEARS_ENERGIZED = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "gears/energized"));
        public static final TagKey<Item> GEARS_VIBRANT = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "gears/vibrant"));
        public static final TagKey<Item> GEARS_DARK_STEEL = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "gears/dark_steel"));

        public static final TagKey<Item> FUSED_QUARTZ = ItemTags.create(EnderIO.loc("fused_quartz"));
        public static final TagKey<Item> CLEAR_GLASS = ItemTags.create(EnderIO.loc("clear_glass"));

        public static final TagKey<Item> BROKEN_SPAWNER_BLACKLIST = ItemTags.create(EnderIO.loc("blacklists/broken_spawner"));
        public static final TagKey<Item> ELECTROMAGNET_BLACKLIST = ItemTags.create(EnderIO.loc("blacklists/electromagnet"));

        public static final Map<GlassIdentifier, TagKey<Item>> GLASS_TAGS = createGlassTags();

        public static Map<GlassIdentifier, TagKey<Item>> createGlassTags() {
            Map<GlassIdentifier, TagKey<Item>> map = new HashMap<>();
            for (GlassLighting lighting: GlassLighting.values()) {
                for (GlassCollisionPredicate collisionPredicate: GlassCollisionPredicate.values()) {
                    for (Boolean isFused: new boolean[]{false, true}) {
                        GlassIdentifier identifier = new GlassIdentifier(lighting, collisionPredicate, isFused);
                        map.put(identifier, ItemTags.create(EnderIO.loc(identifier.glassName())));
                    }
                }
            }
            return map;
        }
    }

    public static class Blocks {

        private static void init() {}

        public static final TagKey<Block> FUSED_QUARTZ = BlockTags.create(EnderIO.loc("fused_quartz"));
        public static final TagKey<Block> CLEAR_GLASS = BlockTags.create(EnderIO.loc("clear_glass"));
        public static final TagKey<Block> DARK_STEEL_TIER = BlockTags.create(EnderIO.loc("needs_dark_steel"));
        public static final TagKey<Block> DARK_STEEL_EXPLODABLE_DENY_LIST = BlockTags.create(EnderIO.loc("dark_steel_explodable_deny_list"));
        public static final TagKey<Block> DARK_STEEL_EXPLODABLE_ALLOW_LIST = BlockTags.create(EnderIO.loc("dark_steel_explodable_allow_list"));

    }
    
    public static class Fluids {
        private static void init() {}

        public static final TagKey<Fluid> COLD_FIRE_IGNITER_FUEL = FluidTags.create(EnderIO.loc("fluid_fuel/cold_fire_igniter"));
        public static final TagKey<Fluid> STAFF_OF_LEVITY_FUEL = FluidTags.create(EnderIO.loc("fluid_fuel/staff_of_levity"));

    }
}
