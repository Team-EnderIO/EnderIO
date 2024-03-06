package com.enderio.base.common.tag;

import com.enderio.EnderIO;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.block.glass.GlassIdentifier;
import com.enderio.base.common.block.glass.GlassLighting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

public class EIOTags {

    public static final String COMMON = "forge";

    public static void register() {
        Items.init();
        Blocks.init();
        Fluids.init();
        EntityTypes.init();
    }

    public static class Items {

        private static void init() {}
    
        public static final TagKey<Item> WRENCH = ItemTags.create(new ResourceLocation(COMMON, "tools/wrench"));
        public static final TagKey<Item> GLIDER = ItemTags.create(EnderIO.loc("tools/glider"));

        public static final TagKey<Item> DUSTS_LAPIS = ItemTags.create(new ResourceLocation(COMMON, "dusts/lapis"));
        public static final TagKey<Item> DUSTS_COAL = ItemTags.create(new ResourceLocation(COMMON, "dusts/coal"));
        public static final TagKey<Item> DUSTS_IRON = ItemTags.create(new ResourceLocation(COMMON, "dusts/iron"));
        public static final TagKey<Item> DUSTS_GOLD = ItemTags.create(new ResourceLocation(COMMON, "dusts/gold"));
        public static final TagKey<Item> DUSTS_COPPER = ItemTags.create(new ResourceLocation(COMMON, "dusts/copper"));
        public static final TagKey<Item> DUSTS_TIN = ItemTags.create(new ResourceLocation(COMMON, "dusts/tin"));
        public static final TagKey<Item> DUSTS_ENDER = ItemTags.create(new ResourceLocation(COMMON, "dusts/ender"));
        public static final TagKey<Item> DUSTS_OBSIDIAN = ItemTags.create(new ResourceLocation(COMMON, "dusts/obsidian"));
        public static final TagKey<Item> DUSTS_COBALT = ItemTags.create(new ResourceLocation(COMMON, "dusts/cobalt"));
        public static final TagKey<Item> DUSTS_QUARTZ = ItemTags.create(new ResourceLocation(COMMON, "dusts/quartz"));
        public static final TagKey<Item> DUSTS_SULFUR = ItemTags.create(new ResourceLocation(COMMON, "dusts/sulfur"));

        public static final TagKey<Item> DUSTS_GRAINS_OF_INFINITY = ItemTags.create(new ResourceLocation(COMMON, "dusts/grains_of_infinity"));
        public static final TagKey<Item> DUSTS_GRAINS_OF_PRESCIENCE = ItemTags.create(new ResourceLocation(COMMON, "dusts/grains_of_prescience"));
        public static final TagKey<Item> DUSTS_GRAINS_OF_VIBRANCY = ItemTags.create(new ResourceLocation(COMMON, "dusts/grains_of_vibrancy"));
        public static final TagKey<Item> DUSTS_GRAINS_OF_PIZEALLITY = ItemTags.create(new ResourceLocation(COMMON, "dusts/grains_of_pizeallity"));
        public static final TagKey<Item> DUSTS_GRAINS_OF_THE_END = ItemTags.create(new ResourceLocation(COMMON, "dusts/grains_of_the_end"));

        public static final TagKey<Item> GEMS_PULSATING_CRYSTAL = ItemTags.create(new ResourceLocation(COMMON, "gems/pulsating_crystal"));
        public static final TagKey<Item> GEMS_VIBRANT_CRYSTAL = ItemTags.create(new ResourceLocation(COMMON, "gems/vibrant_crystal"));
        public static final TagKey<Item> GEMS_ENDER_CRYSTAL = ItemTags.create(new ResourceLocation(COMMON, "gems/ender_crystal"));
        public static final TagKey<Item> GEMS_ENTICING_CRYSTAL = ItemTags.create(new ResourceLocation(COMMON, "gems/enticing_crystal"));
        public static final TagKey<Item> GEMS_WEATHER_CRYSTAL = ItemTags.create(new ResourceLocation(COMMON, "gems/weather_crystal"));
        public static final TagKey<Item> GEMS_PRESCIENT_CRYSTAL = ItemTags.create(new ResourceLocation(COMMON, "gems/prescient_crystal"));

        public static final TagKey<Item> INGOTS_CONDUCTIVE_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "ingots/conductive_alloy"));
        public static final TagKey<Item> INGOTS_COPPER_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "ingots/copper_alloy"));
        public static final TagKey<Item> INGOTS_DARK_STEEL = ItemTags.create(new ResourceLocation(COMMON, "ingots/dark_steel"));
        public static final TagKey<Item> INGOTS_END_STEEL = ItemTags.create(new ResourceLocation(COMMON, "ingots/end_steel"));
        public static final TagKey<Item> INGOTS_ENERGETIC_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "ingots/energetic_alloy"));
        public static final TagKey<Item> INGOTS_PULSATING_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "ingots/pulsating_alloy"));
        public static final TagKey<Item> INGOTS_REDSTONE_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "ingots/redstone_alloy"));
        public static final TagKey<Item> INGOTS_SOULARIUM = ItemTags.create(new ResourceLocation(COMMON, "ingots/soularium"));
        public static final TagKey<Item> INGOTS_VIBRANT_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "ingots/vibrant_alloy"));

        public static final TagKey<Item> NUGGETS_CONDUCTIVE_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "nuggets/conductive_alloy"));
        public static final TagKey<Item> NUGGETS_COPPER_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "nuggets/copper_alloy"));
        public static final TagKey<Item> NUGGETS_DARK_STEEL = ItemTags.create(new ResourceLocation(COMMON, "nuggets/dark_steel"));
        public static final TagKey<Item> NUGGETS_END_STEEL = ItemTags.create(new ResourceLocation(COMMON, "nuggets/end_steel"));
        public static final TagKey<Item> NUGGETS_ENERGETIC_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "nuggets/energetic_alloy"));
        public static final TagKey<Item> NUGGETS_PULSATING_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "nuggets/pulsating_alloy"));
        public static final TagKey<Item> NUGGETS_REDSTONE_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "nuggets/redstone_alloy"));
        public static final TagKey<Item> NUGGETS_SOULARIUM = ItemTags.create(new ResourceLocation(COMMON, "nuggets/soularium"));
        public static final TagKey<Item> NUGGETS_VIBRANT_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "nuggets/vibrant_alloy"));

        public static final TagKey<Item> INSULATION_METAL = ItemTags.create(EnderIO.loc("insulation_metals"));
        
        public static final TagKey<Item> SILICON = ItemTags.create(new ResourceLocation(COMMON, "silicon"));
        public static final TagKey<Item> GEARS = ItemTags.create(new ResourceLocation(COMMON, "gears"));
        public static final TagKey<Item> GEARS_WOOD = ItemTags.create(new ResourceLocation(COMMON, "gears/wood"));
        public static final TagKey<Item> GEARS_STONE = ItemTags.create(new ResourceLocation(COMMON, "gears/stone"));
        public static final TagKey<Item> GEARS_IRON = ItemTags.create(new ResourceLocation(COMMON, "gears/iron"));
        public static final TagKey<Item> GEARS_ENERGIZED = ItemTags.create(new ResourceLocation(COMMON, "gears/energized"));
        public static final TagKey<Item> GEARS_VIBRANT = ItemTags.create(new ResourceLocation(COMMON, "gears/vibrant"));
        public static final TagKey<Item> GEARS_DARK_STEEL = ItemTags.create(new ResourceLocation(COMMON, "gears/dark_steel"));

        public static final TagKey<Item> FUSED_QUARTZ = ItemTags.create(EnderIO.loc("fused_quartz"));
        public static final TagKey<Item> ENLIGHTENED_FUSED_QUARTZ = ItemTags.create(EnderIO.loc("enlighted_fused_quartz"));
        public static final TagKey<Item> DARK_FUSED_QUARTZ = ItemTags.create(EnderIO.loc("dark_fused_quartz"));
        public static final TagKey<Item> CLEAR_GLASS = ItemTags.create(EnderIO.loc("clear_glass"));

        public static final TagKey<Item> BROKEN_SPAWNER_BLACKLIST = ItemTags.create(EnderIO.loc("blacklists/broken_spawner"));
        public static final TagKey<Item> ELECTROMAGNET_BLACKLIST = ItemTags.create(EnderIO.loc("blacklists/electromagnet"));

        public static final TagKey<Item> BLOCKS_CONDUCTIVE_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "storage_blocks/conductive_alloy"));
        public static final TagKey<Item> BLOCKS_COPPER_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "storage_blocks/copper_alloy"));
        public static final TagKey<Item> BLOCKS_DARK_STEEL = ItemTags.create(new ResourceLocation(COMMON, "storage_blocks/dark_steel"));
        public static final TagKey<Item> BLOCKS_END_STEEL = ItemTags.create(new ResourceLocation(COMMON, "storage_blocks/end_steel"));
        public static final TagKey<Item> BLOCKS_ENERGETIC_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "storage_blocks/energetic_alloy"));
        public static final TagKey<Item> BLOCKS_PULSATING_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "storage_blocks/pulsating_alloy"));
        public static final TagKey<Item> BLOCKS_REDSTONE_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "storage_blocks/redstone_alloy"));
        public static final TagKey<Item> BLOCKS_SOULARIUM = ItemTags.create(new ResourceLocation(COMMON, "storage_blocks/soularium"));
        public static final TagKey<Item> BLOCKS_VIBRANT_ALLOY = ItemTags.create(new ResourceLocation(COMMON, "storage_blocks/vibrant_alloy"));

        public static final TagKey<Item> ENTITY_STORAGE = ItemTags.create(EnderIO.loc("entity_storage"));

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
        public static final TagKey<Block> BLOCKS_CONDUCTIVE_ALLOY = BlockTags.create(new ResourceLocation(COMMON, "storage_blocks/conductive_alloy"));
        public static final TagKey<Block> BLOCKS_COPPER_ALLOY = BlockTags.create(new ResourceLocation(COMMON, "storage_blocks/copper_alloy"));
        public static final TagKey<Block> BLOCKS_DARK_STEEL = BlockTags.create(new ResourceLocation(COMMON, "storage_blocks/dark_steel"));
        public static final TagKey<Block> BLOCKS_END_STEEL = BlockTags.create(new ResourceLocation(COMMON, "storage_blocks/end_steel"));
        public static final TagKey<Block> BLOCKS_ENERGETIC_ALLOY = BlockTags.create(new ResourceLocation(COMMON, "storage_blocks/energetic_alloy"));
        public static final TagKey<Block> BLOCKS_PULSATING_ALLOY = BlockTags.create(new ResourceLocation(COMMON, "storage_blocks/pulsating_alloy"));
        public static final TagKey<Block> BLOCKS_REDSTONE_ALLOY = BlockTags.create(new ResourceLocation(COMMON, "storage_blocks/redstone_alloy"));
        public static final TagKey<Block> BLOCKS_SOULARIUM = BlockTags.create(new ResourceLocation(COMMON, "storage_blocks/soularium"));
        public static final TagKey<Block> BLOCKS_VIBRANT_ALLOY = BlockTags.create(new ResourceLocation(COMMON, "storage_blocks/vibrant_alloy"));

    }
    
    public static class Fluids {
        private static void init() {}

        public static final TagKey<Fluid> COLD_FIRE_IGNITER_FUEL = FluidTags.create(EnderIO.loc("fluid_fuel/cold_fire_igniter"));
        public static final TagKey<Fluid> STAFF_OF_LEVITY_FUEL = FluidTags.create(EnderIO.loc("fluid_fuel/staff_of_levity"));
        public static final TagKey<Fluid> EXPERIENCE = FluidTags.create(new ResourceLocation(COMMON, "experience"));
    }

    public static class EntityTypes {
        private static void init() {}

        public static TagKey<EntityType<?>> SOUL_VIAL_BLACKLIST = create("soul_vial_blacklist");

        private static TagKey<EntityType<?>> create(String pName) {
            return TagKey.create(Registries.ENTITY_TYPE, EnderIO.loc(pName));
        }

    }
}
