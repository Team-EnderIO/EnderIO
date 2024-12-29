package com.enderio.base.common.tag;

import com.enderio.EnderIOBase;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.block.glass.GlassIdentifier;
import com.enderio.base.common.block.glass.GlassLighting;
import java.util.HashMap;
import java.util.Map;
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

public class EIOTags {

    public static final String COMMON = "c";

    public static void register() {
        Items.init();
        Blocks.init();
        Fluids.init();
        EntityTypes.init();
    }

    public static class Items {

        private static void init() {
        }

        // TODO: use these for slot filtering again.
        public static final TagKey<Item> ENTITY_STORAGE = tag("entity_storage");
        public static final TagKey<Item> GRINDING_BALLS = tag("grinding_balls");

        public static final TagKey<Item> HIDE_FACADES = tag("enderio/hide_facades");

        public static final TagKey<Item> WRENCH = commonTag("tools/wrench");
        public static final TagKey<Item> GLIDER = tag("tools/glider");

        public static final TagKey<Item> DUSTS_LAPIS = commonTag("dusts/lapis");
        public static final TagKey<Item> DUSTS_COAL = commonTag("dusts/coal");
        public static final TagKey<Item> DUSTS_IRON = commonTag("dusts/iron");
        public static final TagKey<Item> DUSTS_GOLD = commonTag("dusts/gold");
        public static final TagKey<Item> DUSTS_COPPER = commonTag("dusts/copper");
        public static final TagKey<Item> DUSTS_TIN = commonTag("dusts/tin");
        public static final TagKey<Item> DUSTS_ENDER = commonTag("dusts/ender_pearl");
        public static final TagKey<Item> DUSTS_OBSIDIAN = commonTag("dusts/obsidian");
        public static final TagKey<Item> DUSTS_COBALT = commonTag("dusts/cobalt");
        public static final TagKey<Item> DUSTS_QUARTZ = commonTag("dusts/quartz");
        public static final TagKey<Item> DUSTS_SULFUR = commonTag("dusts/sulfur");
        public static final TagKey<Item> DUSTS_PRISMARINE = commonTag("dusts/prismarine");

        public static final TagKey<Item> DUSTS_GRAINS_OF_INFINITY = commonTag("dusts/grains_of_infinity");
        public static final TagKey<Item> DUSTS_GRAINS_OF_PRESCIENCE = commonTag("dusts/grains_of_prescience");
        public static final TagKey<Item> DUSTS_GRAINS_OF_VIBRANCY = commonTag("dusts/grains_of_vibrancy");
        public static final TagKey<Item> DUSTS_GRAINS_OF_PIZEALLITY = commonTag("dusts/grains_of_pizeallity");
        public static final TagKey<Item> DUSTS_GRAINS_OF_THE_END = commonTag("dusts/grains_of_the_end");

        public static final TagKey<Item> GEMS_PULSATING_CRYSTAL = commonTag("gems/pulsating_crystal");
        public static final TagKey<Item> GEMS_VIBRANT_CRYSTAL = commonTag("gems/vibrant_crystal");
        public static final TagKey<Item> GEMS_ENDER_CRYSTAL = commonTag("gems/ender_crystal");
        public static final TagKey<Item> GEMS_ENTICING_CRYSTAL = commonTag("gems/enticing_crystal");
        public static final TagKey<Item> GEMS_WEATHER_CRYSTAL = commonTag("gems/weather_crystal");
        public static final TagKey<Item> GEMS_PRESCIENT_CRYSTAL = commonTag("gems/prescient_crystal");

        public static final TagKey<Item> INGOTS_CONDUCTIVE_ALLOY = commonTag("ingots/conductive_alloy");
        public static final TagKey<Item> INGOTS_COPPER_ALLOY = commonTag("ingots/copper_alloy");
        public static final TagKey<Item> INGOTS_DARK_STEEL = commonTag("ingots/dark_steel");
        public static final TagKey<Item> INGOTS_END_STEEL = commonTag("ingots/end_steel");
        public static final TagKey<Item> INGOTS_ENERGETIC_ALLOY = commonTag("ingots/energetic_alloy");
        public static final TagKey<Item> INGOTS_PULSATING_ALLOY = commonTag("ingots/pulsating_alloy");
        public static final TagKey<Item> INGOTS_REDSTONE_ALLOY = commonTag("ingots/redstone_alloy");
        public static final TagKey<Item> INGOTS_SOULARIUM = commonTag("ingots/soularium");
        public static final TagKey<Item> INGOTS_VIBRANT_ALLOY = commonTag("ingots/vibrant_alloy");

        public static final TagKey<Item> NUGGETS_CONDUCTIVE_ALLOY = commonTag("nuggets/conductive_alloy");
        public static final TagKey<Item> NUGGETS_COPPER_ALLOY = commonTag("nuggets/copper_alloy");
        public static final TagKey<Item> NUGGETS_DARK_STEEL = commonTag("nuggets/dark_steel");
        public static final TagKey<Item> NUGGETS_END_STEEL = commonTag("nuggets/end_steel");
        public static final TagKey<Item> NUGGETS_ENERGETIC_ALLOY = commonTag("nuggets/energetic_alloy");
        public static final TagKey<Item> NUGGETS_PULSATING_ALLOY = commonTag("nuggets/pulsating_alloy");
        public static final TagKey<Item> NUGGETS_REDSTONE_ALLOY = commonTag("nuggets/redstone_alloy");
        public static final TagKey<Item> NUGGETS_SOULARIUM = commonTag("nuggets/soularium");
        public static final TagKey<Item> NUGGETS_VIBRANT_ALLOY = commonTag("nuggets/vibrant_alloy");

        public static final TagKey<Item> INSULATION_METAL = tag("insulation_metals");

        public static final TagKey<Item> SILICON = commonTag("silicon");
        public static final TagKey<Item> GEARS = commonTag("gears");
        public static final TagKey<Item> GEARS_WOOD = commonTag("gears/wood");
        public static final TagKey<Item> GEARS_STONE = commonTag("gears/stone");
        public static final TagKey<Item> GEARS_IRON = commonTag("gears/iron");
        public static final TagKey<Item> GEARS_ENERGIZED = commonTag("gears/energized");
        public static final TagKey<Item> GEARS_VIBRANT = commonTag("gears/vibrant");
        public static final TagKey<Item> GEARS_DARK_STEEL = commonTag("gears/dark_steel");

        public static final TagKey<Item> FUSED_QUARTZ = commonTag("glass_blocks/fused_quartz");
        public static final TagKey<Item> ENLIGHTENED_FUSED_QUARTZ = commonTag("glass_blocks/enlighted_fused_quartz");
        public static final TagKey<Item> DARK_FUSED_QUARTZ = commonTag("glass_blocks/dark_fused_quartz");
        public static final TagKey<Item> CLEAR_GLASS = commonTag("glass_blocks/clear");

        public static final TagKey<Item> BROKEN_SPAWNER_BLACKLIST = tag("blacklists/broken_spawner");
        public static final TagKey<Item> ELECTROMAGNET_BLACKLIST = tag("blacklists/electromagnet");

        public static final TagKey<Item> BLOCKS_CONDUCTIVE_ALLOY = commonTag("storage_blocks/conductive_alloy");
        public static final TagKey<Item> BLOCKS_COPPER_ALLOY = commonTag("storage_blocks/copper_alloy");
        public static final TagKey<Item> BLOCKS_DARK_STEEL = commonTag("storage_blocks/dark_steel");
        public static final TagKey<Item> BLOCKS_END_STEEL = commonTag("storage_blocks/end_steel");
        public static final TagKey<Item> BLOCKS_ENERGETIC_ALLOY = commonTag("storage_blocks/energetic_alloy");
        public static final TagKey<Item> BLOCKS_PULSATING_ALLOY = commonTag("storage_blocks/pulsating_alloy");
        public static final TagKey<Item> BLOCKS_REDSTONE_ALLOY = commonTag("storage_blocks/redstone_alloy");
        public static final TagKey<Item> BLOCKS_SOULARIUM = commonTag("storage_blocks/soularium");
        public static final TagKey<Item> BLOCKS_VIBRANT_ALLOY = commonTag("storage_blocks/vibrant_alloy");

        public static final TagKey<Item> STORAGE_BLOCKS_QUARTZ = commonTag("storage_blocks/quartz");
        public static final TagKey<Item> STORAGE_BLOCKS_AMETHYST = commonTag("storage_blocks/amethyst");

        public static final Map<GlassIdentifier, TagKey<Item>> GLASS_TAGS = createGlassTags();

        public static Map<GlassIdentifier, TagKey<Item>> createGlassTags() {
            Map<GlassIdentifier, TagKey<Item>> map = new HashMap<>();
            for (GlassLighting lighting : GlassLighting.values()) {
                for (GlassCollisionPredicate collisionPredicate : GlassCollisionPredicate.values()) {
                    for (Boolean isFused : new boolean[] { false, true }) {
                        GlassIdentifier identifier = new GlassIdentifier(lighting, collisionPredicate, isFused);
                        map.put(identifier, tag(identifier.glassName()));
                    }
                }
            }
            return map;
        }

        private static TagKey<Item> commonTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(COMMON, name));
        }

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(EnderIOBase.loc(name));
        }
    }

    public static class Blocks {

        private static void init() {
        }

        public static final TagKey<Block> FUSED_QUARTZ = commonTag("glass_blocks/fused_quartz");
        public static final TagKey<Block> CLEAR_GLASS = tag("glass_blocks/clear_glass");

        public static final TagKey<Block> BLOCKS_CONDUCTIVE_ALLOY = commonTag("storage_blocks/conductive_alloy");
        public static final TagKey<Block> BLOCKS_COPPER_ALLOY = commonTag("storage_blocks/copper_alloy");
        public static final TagKey<Block> BLOCKS_DARK_STEEL = commonTag("storage_blocks/dark_steel");
        public static final TagKey<Block> BLOCKS_END_STEEL = commonTag("storage_blocks/end_steel");
        public static final TagKey<Block> BLOCKS_ENERGETIC_ALLOY = commonTag("storage_blocks/energetic_alloy");
        public static final TagKey<Block> BLOCKS_PULSATING_ALLOY = commonTag("storage_blocks/pulsating_alloy");
        public static final TagKey<Block> BLOCKS_REDSTONE_ALLOY = commonTag("storage_blocks/redstone_alloy");
        public static final TagKey<Block> BLOCKS_SOULARIUM = commonTag("storage_blocks/soularium");
        public static final TagKey<Block> BLOCKS_VIBRANT_ALLOY = commonTag("storage_blocks/vibrant_alloy");

        private static TagKey<Block> commonTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(COMMON, name));
        }

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(EnderIOBase.loc(name));
        }
    }

    public static class Fluids {
        private static void init() {
        }

        public static final TagKey<Fluid> COLD_FIRE_IGNITER_FUEL = tag("fluid_fuel/cold_fire_igniter");
        public static final TagKey<Fluid> STAFF_OF_LEVITY_FUEL = tag("fluid_fuel/staff_of_levity");
        public static final TagKey<Fluid> EXPERIENCE = commonTag("experience");
        public static final TagKey<Fluid> SOLAR_PANEL_LIGHT = tag("solar_panel_light");

        private static TagKey<Fluid> commonTag(String name) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath(COMMON, name));
        }

        private static TagKey<Fluid> tag(String name) {
            return FluidTags.create(EnderIOBase.loc(name));
        }
    }

    public static class EntityTypes {
        private static void init() {
        }

        public static TagKey<EntityType<?>> SOUL_VIAL_BLACKLIST = tag("soul_vial_blacklist");

        private static TagKey<EntityType<?>> tag(String pName) {
            return TagKey.create(Registries.ENTITY_TYPE, EnderIOBase.loc(pName));
        }

    }
}
