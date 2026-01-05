package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe.BonusType;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

import static com.enderio.enderio.init.EIOItems.*;
import static net.minecraft.world.item.Items.*;

public class SagMillRecipeProvider extends SubRecipeProvider {

    private static final int BASE_ENERGY_PER_OPERATION = 2400;

    private HolderLookup.RegistryLookup<Item> items;

    protected Ingredient ingredientFromTag(TagKey<Item> tag) {
        return Ingredient.of(this.items.getOrThrow(tag));
    }

    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        this.items = registries.lookupOrThrow(Registries.ITEM);

        buildOreBlockRecipes(items, recipeOutput);
        buildRawOreRecipes(items, recipeOutput);

        build1toN("iron", Tags.Items.INGOTS_IRON, POWDERED_IRON.get(), 1, BonusType.NONE, recipeOutput);
        build1toN("gold", Tags.Items.INGOTS_GOLD, POWDERED_GOLD.get(), 1, BonusType.NONE, recipeOutput);
        build1toN("obsidian", Tags.Items.OBSIDIANS, POWDERED_OBSIDIAN.get(), 4, recipeOutput);

        build1toN("precient_crystal", EIOTags.Items.GEMS_PRESCIENT_CRYSTAL, PRESCIENT_POWDER.get(), 1, BonusType.NONE,
                recipeOutput);
        build1toN("vibrant_crystal", EIOTags.Items.GEMS_VIBRANT_CRYSTAL, VIBRANT_POWDER.get(), 1, BonusType.NONE,
                recipeOutput);
        build1toN("pulsating_crystal", EIOTags.Items.GEMS_PULSATING_CRYSTAL, PULSATING_POWDER.get(), 1, BonusType.NONE,
                recipeOutput);
        build1toN("ender_crystal", EIOTags.Items.GEMS_ENDER_CRYSTAL, ENDER_CRYSTAL_POWDER.get(), 1, BonusType.NONE,
                recipeOutput);

        build1toN("ender_pearl", Tags.Items.ENDER_PEARLS, POWDERED_ENDER_PEARL.get(), 9, BonusType.NONE, recipeOutput);
        build1toN("blaze_powder", Tags.Items.RODS_BLAZE, BLAZE_POWDER, 4, recipeOutput);
        build1toN("glass", Tags.Items.GLASS_BLOCKS, SAND, 1, BonusType.NONE, recipeOutput);
        build1toN("bone_block", BONE_BLOCK, BONE_MEAL, 9, BonusType.NONE, recipeOutput);
        build1toN("soularium", EIOTags.Items.INGOTS_SOULARIUM, SOUL_POWDER.get(), 1, BonusType.NONE, recipeOutput);

        build1toN("stone", Tags.Items.STONES, COBBLESTONE, 1, BonusType.NONE, recipeOutput);
        build1toN("deepslate", Items.DEEPSLATE, Items.COBBLED_DEEPSLATE, 1, BonusType.NONE, recipeOutput);

        build("cobbled_deepslate",ingredientFromTag(Tags.Items.COBBLESTONES_DEEPSLATE),
                List.of(output(COBBLESTONE), output(EIOItems.GRAINS_OF_INFINITY.get(), 0.12f)),
                BASE_ENERGY_PER_OPERATION, BonusType.CHANCE_ONLY, recipeOutput);

        build("coal", ingredientFromTag(ItemTags.COALS), List.of(output(POWDERED_COAL.get()),
                output(POWDERED_COAL.get(), 0.1f), output(EIOTags.Items.DUSTS_SULFUR, 1, 0.1f, true)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("lapis_ore", ingredientFromTag(Tags.Items.ORES_LAPIS),
                List.of(output(LAPIS_LAZULI, 8), output(LAPIS_LAZULI, 0.2f), output(COBBLESTONE, 0.15f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("lapis", ingredientFromTag(Tags.Items.GEMS_LAPIS), List.of(output(POWDERED_LAPIS_LAZULI.get(), 1)),
                BASE_ENERGY_PER_OPERATION, BonusType.NONE, recipeOutput);

        build("quartz", ingredientFromTag(Tags.Items.GEMS_QUARTZ),
                List.of(output(POWDERED_QUARTZ.get(), 1), output(POWDERED_QUARTZ.get(), 0.1f)),
                BASE_ENERGY_PER_OPERATION, BonusType.CHANCE_ONLY, recipeOutput);

        build("cobblestone", ingredientFromTag(Tags.Items.COBBLESTONES_NORMAL),
                List.of(output(GRAVEL, 0.7f), output(GRAVEL, 0.3f), output(SAND, 0.1f), output(FLINT, 0.05f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("mossy_cobblestone", ingredientFromTag(Tags.Items.COBBLESTONES_MOSSY),
                List.of(output(COBBLESTONE), output(VINE, 0.7f)), BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("gravel", ingredientFromTag(Tags.Items.GRAVELS),
                List.of(output(SAND, 0.7f), output(SAND, 0.3f), output(FLINT, 0.1f)), BASE_ENERGY_PER_OPERATION,
                recipeOutput);

        build("sandstone", ingredientFromTag(Tags.Items.SANDSTONE_BLOCKS), List.of(output(SAND, 2), output(SAND, 2, 0.4f)),
                BASE_ENERGY_PER_OPERATION, BonusType.CHANCE_ONLY, recipeOutput);

        build("sand", ingredientFromTag(Tags.Items.SANDS), List.of(output(EIOTags.Items.SILICON, 0.5f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("glowstone", Ingredient.of(GLOWSTONE), List.of(// swap with any:glowstone
                output(GLOWSTONE_DUST, 3), output(GLOWSTONE_DUST, 0.8f)), BASE_ENERGY_PER_OPERATION,
                BonusType.CHANCE_ONLY, recipeOutput);

        build("bone", ingredientFromTag(Tags.Items.BONES), List.of(output(BONE_MEAL, 6), output(BONE_MEAL, 2, 0.1f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("clay", Ingredient.of(CLAY),
                List.of(output(CLAY_BALL, 2), output(CLAY_BALL, 0.1f), output(SILICON.get(), 2, 0.8f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("wool", ingredientFromTag(ItemTags.WOOL), List.of(output(STRING, 2), output(STRING, 0.1f)),
                BASE_ENERGY_PER_OPERATION, BonusType.CHANCE_ONLY, recipeOutput);

        build("quartz_block", ingredientFromTag(EIOTags.Items.STORAGE_BLOCKS_QUARTZ),
                List.of(output(QUARTZ, 2), output(QUARTZ, 2, 0.25f)), BASE_ENERGY_PER_OPERATION, BonusType.CHANCE_ONLY,
                recipeOutput);

        build("quartz_stairs", Ingredient.of(QUARTZ_STAIRS), List.of(output(QUARTZ, 2), output(QUARTZ, 2, 0.25f)),
                BASE_ENERGY_PER_OPERATION, BonusType.CHANCE_ONLY, recipeOutput);

        build("quartz_slabs", Ingredient.of(QUARTZ_SLAB), List.of(output(QUARTZ, 1), output(QUARTZ, 0.25f)),
                BASE_ENERGY_PER_OPERATION, BonusType.CHANCE_ONLY, recipeOutput);

        build("cobweb", Ingredient.of(COBWEB), List.of(output(STRING, 2), output(STRING, 0.6f), output(STRING, 0.3f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        buildFlower("dandelion", DANDELION, YELLOW_DYE, recipeOutput);
        buildFlower("poppy", POPPY, RED_DYE, recipeOutput);
        buildFlower("blue_orchid", BLUE_ORCHID, LIGHT_BLUE_DYE, recipeOutput);
        buildFlower("allium", ALLIUM, MAGENTA_DYE, recipeOutput);
        buildFlower("azure_bluet", AZURE_BLUET, LIGHT_GRAY_DYE, recipeOutput);
        buildFlower("red_tulip", RED_TULIP, RED_DYE, recipeOutput);
        buildFlower("orange_tulip", ORANGE_TULIP, ORANGE_DYE, recipeOutput);
        buildFlower("white_tulip", WHITE_TULIP, WHITE_DYE, recipeOutput);
        buildFlower("pink_tulip", PINK_TULIP, PINK_DYE, recipeOutput);
        buildFlower("oxeye_daisy", OXEYE_DAISY, WHITE_DYE, recipeOutput);

        build("shrub", Ingredient.of(DEAD_BUSH), List.of(output(PLANT_MATTER_BROWN.get(), 0.8f),
                output(PLANT_MATTER_BROWN.get(), 0.6f), output(PLANT_MATTER_BROWN.get(), 0.3f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("grass", Ingredient.of(TALL_GRASS),
                List.of(output(PLANT_MATTER_GREEN.get(), 0.6f), output(PLANT_MATTER_GREEN.get(), 0.3f),
                        output(PLANT_MATTER_GREEN.get(), 0.1f), output(PLANT_MATTER_BROWN.get(), 0.05f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("fern", Ingredient.of(FERN),
                List.of(output(PLANT_MATTER_GREEN.get(), 0.6f), output(PLANT_MATTER_GREEN.get(), 0.3f),
                        output(PLANT_MATTER_GREEN.get(), 0.1f), output(PLANT_MATTER_BROWN.get(), 0.05f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("leaves", ingredientFromTag(ItemTags.LEAVES), List.of(output(PLANT_MATTER_GREEN.get(), 0.3f),
                output(PLANT_MATTER_GREEN.get(), 0.1f), output(PLANT_MATTER_GREEN.get(), 0.02f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("vines", Ingredient.of(VINE),
                List.of(output(PLANT_MATTER_GREEN.get(), 0.15f), output(PLANT_MATTER_GREEN.get(), 0.1f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("cactus", Ingredient.of(CACTUS),
                List.of(output(PLANT_MATTER_GREEN.get(), 3), output(PLANT_MATTER_GREEN.get(), 3, 0.75f),
                        output(PLANT_MATTER_GREEN.get(), 3, 0.5f), output(PLANT_MATTER_BROWN.get(), 3, 0.25f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("lily_pad", Ingredient.of(LILY_PAD),
                List.of(output(PLANT_MATTER_GREEN.get(), 3, 0.15f), output(PLANT_MATTER_GREEN.get(), 3, 0.1f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("sun_flower", Ingredient.of(Items.SUNFLOWER), List.of(output(YELLOW_DYE, 2, 0.8f),
                output(YELLOW_DYE, 0.6f), output(YELLOW_DYE, 2, 0.3f), output(PLANT_MATTER_GREEN.get(), 0.2f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("tall_grass", Ingredient.of(TALL_GRASS),
                List.of(output(PLANT_MATTER_GREEN.get(), 2, 0.6f), output(PLANT_MATTER_GREEN.get(), 0.3f),
                        output(PLANT_MATTER_GREEN.get(), 2, 0.1f), output(PLANT_MATTER_BROWN.get(), 0.15f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("large_fern", Ingredient.of(LARGE_FERN),
                List.of(output(PLANT_MATTER_GREEN.get(), 2, 0.6f), output(PLANT_MATTER_GREEN.get(), 0.3f),
                        output(PLANT_MATTER_GREEN.get(), 2, 0.1f), output(PLANT_MATTER_BROWN.get(), 0.15f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("rose_bush", Ingredient.of(ROSE_BUSH), List.of(output(RED_DYE, 2, 0.8f), output(RED_DYE, 0.6f),
                output(RED_DYE, 2, 0.3f), output(PLANT_MATTER_GREEN.get(), 0.2f)), BASE_ENERGY_PER_OPERATION,
                recipeOutput);

        build("poeny", Ingredient.of(PEONY), List.of(output(PINK_DYE, 2, 0.8f), output(PINK_DYE, 0.6f),
                output(PINK_DYE, 2, 0.3f), output(PLANT_MATTER_GREEN.get(), 0.2f)), BASE_ENERGY_PER_OPERATION,
                recipeOutput);

        build("sugar_canes", Ingredient.of(SUGAR_CANE), List.of(output(SUGAR), output(SUGAR, 0.5f),
                output(SUGAR, 2, 0.5f), output(PLANT_MATTER_GREEN.get(), 0.2f)), BASE_ENERGY_PER_OPERATION,
                recipeOutput);

        build("flower_pot", Ingredient.of(FLOWER_POT), List.of(output(BRICK, 0.9f), output(BRICK, 0.3f),
                output(BRICK, 0.1f), output(POWDERED_COAL.get(), 0.05f)), BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("prismarine_shard", Ingredient.of(PRISMARINE_SHARD),
                List.of(output(PRISMARINE_CRYSTALS), output(PRISMARINE_CRYSTALS, 0.1f)), BASE_ENERGY_PER_OPERATION,
                BonusType.NONE, recipeOutput);

        build("wither_rose", Ingredient.of(WITHER_ROSE), List.of(output(WITHERING_POWDER.get(), 1, 0.4f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("wither_skull", Ingredient.of(WITHER_SKELETON_SKULL),
                List.of(output(WITHERING_POWDER.get(), 2), output(WITHERING_POWDER.get(), 1, 0.2f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);
    }

    private void buildOreBlockRecipes(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput) {
        buildOre("iron_ore", ingredientFromTag(Tags.Items.ORES_IRON), RAW_IRON, recipeOutput);
        buildOre("gold_ore", ingredientFromTag(Tags.Items.ORES_GOLD), RAW_GOLD, recipeOutput);
        buildOre("copper_ore", ingredientFromTag(Tags.Items.ORES_COPPER), RAW_COPPER, recipeOutput);

        build("coal_ore", ingredientFromTag(Tags.Items.ORES_COAL), List.of(output(COAL, 3),
                output(POWDERED_COAL.get(), 0.6f), output(DIAMOND, 0.005f), output(COBBLESTONE, 0.15f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("redstone_ore", ingredientFromTag(Tags.Items.ORES_REDSTONE), List.of(output(REDSTONE, 8),
                output(REDSTONE, 0.2f), output(SILICON.get(), 0.8f), output(COBBLESTONE, 0.15f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("diamond_ore", ingredientFromTag(Tags.Items.ORES_DIAMOND), List.of(output(DIAMOND, 2), output(DIAMOND, 0.25f),
                output(POWDERED_COAL.get(), 0.1f), output(COBBLESTONE, 0.15f)), BASE_ENERGY_PER_OPERATION,
                recipeOutput);

        build("emerald_ore", ingredientFromTag(Tags.Items.ORES_EMERALD),
                List.of(output(EMERALD, 2), output(EMERALD, 0.25f), output(COBBLESTONE, 0.15f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("quartz_ore", ingredientFromTag(Tags.Items.ORES_QUARTZ),
                List.of(output(QUARTZ, 2), output(POWDERED_QUARTZ.get(), 0.1f), output(NETHERRACK, 0.15f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);
    }

    private void buildRawOreRecipes(HolderLookup.RegistryLookup<Item> items, RecipeOutput recipeOutput) {
        build("raw_iron", ingredientFromTag(Tags.Items.RAW_MATERIALS_IRON),
                List.of(output(POWDERED_IRON), output(POWDERED_IRON, 0.8f), output(EIOTags.Items.DUSTS_TIN, 0.05f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);

        build("raw_copper", ingredientFromTag(Tags.Items.RAW_MATERIALS_COPPER), List.of(output(POWDERED_COPPER),
                output(POWDERED_COPPER, 0.8f), output(EIOTags.Items.DUSTS_GOLD, 0.12f)), BASE_ENERGY_PER_OPERATION,
                recipeOutput);

        build("raw_gold", ingredientFromTag(Tags.Items.RAW_MATERIALS_GOLD),
                List.of(output(POWDERED_GOLD), output(POWDERED_GOLD, 0.8f), output(EIOTags.Items.DUSTS_COPPER, 0.20f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);
    }

    // region Helpers

    private void buildOre(String name, Ingredient input, Item output, RecipeOutput recipeOutput) {
        build(name, input, List.of(output(output), output(output, 0.33f), output(COBBLESTONE, 0.15f)),
                BASE_ENERGY_PER_OPERATION, recipeOutput);
    }

    private void buildFlower(String name, ItemLike input, Item output, RecipeOutput recipeOutput) {
        build(name, Ingredient.of(input), List.of(output(output, 0.8f), output(output, 0.6f), output(output, 0.3f),
                output(PLANT_MATTER_GREEN.get(), 0.1f)), BASE_ENERGY_PER_OPERATION, recipeOutput);
    }

    private void build1toN(String name, ItemLike input, Item output, int number, BonusType bonusType,
            RecipeOutput recipeOutput) {
        build(name, Ingredient.of(input), List.of(output(output, number)), BASE_ENERGY_PER_OPERATION, bonusType,
                recipeOutput);
    }

    private void build1toN(String name, TagKey<Item> input, Item output, int number, RecipeOutput recipeOutput) {
        build(name, ingredientFromTag(input), List.of(output(output, number)), BASE_ENERGY_PER_OPERATION, recipeOutput);
    }

    private void build1toN(String name, TagKey<Item> input, Item output, int number, BonusType bonusType,
            RecipeOutput recipeOutput) {
        build(name, ingredientFromTag(input), List.of(output(output, number)), BASE_ENERGY_PER_OPERATION, bonusType,
                recipeOutput);
    }

    protected void build(String name, Ingredient input, List<SagMillingRecipe.OutputItem> outputs, int energy,
            RecipeOutput recipeOutput) {
        build(EnderIO.id("sag_milling/" + name), input, outputs, energy, BonusType.MULTIPLY_OUTPUT, recipeOutput);
    }

    protected void build(String name, Ingredient input, List<SagMillingRecipe.OutputItem> outputs, int energy,
            BonusType bonusType, RecipeOutput recipeOutput) {
        build(EnderIO.id("sag_milling/" + name), input, outputs, energy, bonusType, recipeOutput);
    }

    protected void build(Identifier id, Ingredient input, List<SagMillingRecipe.OutputItem> outputs, int energy,
            BonusType bonusType, RecipeOutput recipeOutput) {
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE, id), new SagMillingRecipe(input, outputs, energy, bonusType), null);
    }

    protected SagMillingRecipe.OutputItem output(ItemLike item) {
        return output(item, 1, 1.0f, false);
    }

    protected SagMillingRecipe.OutputItem output(ItemLike item, int count) {
        return output(item, count, 1.0f, false);
    }

    protected SagMillingRecipe.OutputItem output(ItemLike item, float chance) {
        return output(item, 1, chance, false);
    }

    protected SagMillingRecipe.OutputItem output(ItemLike item, int count, float chance) {
        return output(item, count, chance, false);
    }

    protected SagMillingRecipe.OutputItem output(ItemLike item, int count, float chance, boolean optional) {
        return SagMillingRecipe.OutputItem.of(item.asItem(), count, chance, optional);
    }

    protected SagMillingRecipe.OutputItem output(TagKey<Item> tag) {
        return output(tag, 1, 1.0f, false);
    }

    protected SagMillingRecipe.OutputItem output(TagKey<Item> tag, int count) {
        return output(tag, count, 1.0f, false);
    }

    protected SagMillingRecipe.OutputItem output(TagKey<Item> tag, float chance) {
        return output(tag, 1, chance, false);
    }

    protected SagMillingRecipe.OutputItem output(TagKey<Item> tag, int count, float chance) {
        return output(tag, count, chance, false);
    }

    protected SagMillingRecipe.OutputItem output(TagKey<Item> tag, int count, float chance, boolean optional) {
        return SagMillingRecipe.OutputItem.of(tag, count, chance, optional);
    }

    // endregion

}
