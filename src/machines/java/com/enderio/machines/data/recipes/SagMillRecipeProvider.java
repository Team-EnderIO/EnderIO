package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.SagMillingRecipe;
import com.enderio.machines.common.recipe.SagMillingRecipe.BonusType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.enderio.base.common.init.EIOItems.*;
import static net.minecraft.world.item.Items.*;

public class SagMillRecipeProvider extends EnderRecipeProvider {

    private static final int BASE_ENERGY_PER_OPERATION = 2400;
	
	public SagMillRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        
        build1toN("iron", Tags.Items.INGOTS_IRON, IRON_POWDER.get(), 1, finishedRecipeConsumer);
        build1toN("gold", Tags.Items.INGOTS_GOLD, GOLD_POWDER.get(), 1, finishedRecipeConsumer);
        build1toN("obsidian", Tags.Items.OBSIDIAN, OBSIDIAN_POWDER.get(), 4, finishedRecipeConsumer);
        
        build1toN("precient_crystal", PRECIENT_CRYSTAL.get(), PRECIENT_POWDER.get(), 1, finishedRecipeConsumer);
        build1toN("vibrant_crystal", VIBRANT_CRYSTAL.get(), VIBRANT_POWDER.get(), 1, finishedRecipeConsumer);
        build1toN("pulsating_crystal", PULSATING_CRYSTAL.get(), PULSATING_POWDER.get(), 1, finishedRecipeConsumer);
        build1toN("ender_crystal", ENDER_CRYSTAL.get(), ENDER_CRYSTAL_POWDER.get(), 1, finishedRecipeConsumer);
        
        build1toN("ender_pearl", ENDER_PEARL, ENDER_PEARL_POWDER.get(), 9, finishedRecipeConsumer);
        build1toN("blaze_powder", BLAZE_ROD, BLAZE_POWDER, 4, finishedRecipeConsumer);
        build1toN("glass", GLASS, SAND, 1, finishedRecipeConsumer);
        build1toN("bone_block",BONE_BLOCK, BONE_MEAL, 9, finishedRecipeConsumer);
        build1toN("soularium", SOULARIUM_INGOT.get(), SOUL_POWDER.get(), 1, finishedRecipeConsumer);
        
        buildOre("iron_ore", Ingredient.of(Tags.Items.ORES_IRON), IRON_POWDER.get(), finishedRecipeConsumer);
        buildOre("gold_ore", Ingredient.of(Tags.Items.ORES_GOLD), GOLD_POWDER.get(), finishedRecipeConsumer);
        
        build1toN("iron_block", Tags.Items.STORAGE_BLOCKS_IRON, IRON_POWDER.get(), 9, finishedRecipeConsumer);
        build1toN("gold_block", Tags.Items.STORAGE_BLOCKS_GOLD, GOLD_POWDER.get(), 9, finishedRecipeConsumer);
        build1toN("copper_block", Tags.Items.STORAGE_BLOCKS_COPPER, COPPER_POWDER.get(), 9, finishedRecipeConsumer);
        build1toN("coal_block", Tags.Items.STORAGE_BLOCKS_COAL, COAL_POWDER.get(), 9, finishedRecipeConsumer);
        
        build("coal", Ingredient.of(COAL), List.of(
        		output(COAL_POWDER.get()),
            	output(COAL_POWDER.get(), 0.1f),
            	output(EIOTags.Items.DUSTS_SULFUR, 1, 0.1f, true)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("coal_ore", Ingredient.of(COAL_ORE), List.of(
        		output(COAL, 3),
            	output(COAL_POWDER.get(), 0.6f),
            	output(DIAMOND, 0.005f),
            	output(COBBLESTONE, 0.15f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("redstone_ore", Ingredient.of(REDSTONE_ORE), List.of(
        		output(REDSTONE, 8),
            	output(REDSTONE, 0.2f),
            	output(SILICON.get(), 0.8f),
            	output(COBBLESTONE, 0.15f)
            	), BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("diamond_ore", Ingredient.of(DIAMOND_ORE), List.of(
        		output(DIAMOND, 2),
            	output(DIAMOND, 0.25f),
            	output(COAL_POWDER.get(), 0.1f),
        		output(COBBLESTONE, 0.15f)
        		),BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("emerald_ore", Ingredient.of(EMERALD_ORE), List.of(
        		output(EMERALD, 2),
            	output(EMERALD, 0.25f),
        		output(COBBLESTONE, 0.15f)
        		),BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("lapis_ore", Ingredient.of(LAPIS_ORE), List.of(
        		output(LAPIS_LAZULI, 8),
            	output(LAPIS_LAZULI, 0.2f),
        		output(COBBLESTONE, 0.15f)
        		),BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("quartz_ore", Ingredient.of(NETHER_QUARTZ_ORE), List.of(
        		output(QUARTZ, 2),
            	output(QUARTZ_POWDER.get(), 0.1f),
        		output(NETHERRACK, 0.15f)
        		),BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("quartz", Ingredient.of(QUARTZ), List.of(
        		output(QUARTZ_POWDER.get(), 1),
            	output(QUARTZ_POWDER.get(), 0.1f)
        		),BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("wheat", Ingredient.of(WHEAT), List.of(
        		output(FLOUR.get()),
        		output(Tags.Items.SEEDS_WHEAT, 1, 0.2f, true)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("cobblestone", Ingredient.of(Tags.Items.COBBLESTONE), List.of(
        		output(GRAVEL, 0.7f), 
        		output(GRAVEL, 0.3f), 
        		output(SAND, 0.1f), 
        		output(FLINT, 0.05f)), 
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("sandstone", Ingredient.of(Tags.Items.SANDSTONE), List.of(
        		output(SAND, 2), 
        		output(SAND, 2, 0.4f)), 
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("sand", Ingredient.of(SAND), List.of(
        		output(SILICON.get(), 2)
        		),BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("glowstone", Ingredient.of(GLOWSTONE), List.of(//swap with any:glowstone
        		output(GLOWSTONE_DUST, 3), 
        		output(GLOWSTONE_DUST, 0.8f)), 
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("bone", Ingredient.of(BONE), List.of(
        		output(BONE_MEAL, 6), 
        		output(BONE_MEAL, 2, 0.1f)), 
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("clay", Ingredient.of(CLAY), List.of(
        		output(CLAY_BALL, 2), 
        		output(CLAY_BALL, 0.1f), 
        		output(SILICON.get(), 2, 0.8f)), 
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("wool", Ingredient.of(ItemTags.WOOL), List.of(
        		output(STRING, 2), 
        		output(STRING, 0.1f)), 
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("quartz_block", Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ), List.of(
        		output(QUARTZ, 2), 
        		output(QUARTZ, 2, 0.25f)), 
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("quartz_stairs", Ingredient.of(QUARTZ_STAIRS), List.of(
        		output(QUARTZ, 2), 
        		output(QUARTZ, 2, 0.25f)), 
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("quartz_slabs", Ingredient.of(QUARTZ_SLAB), List.of(
        		output(QUARTZ, 1), 
        		output(QUARTZ, 0.25f)), 
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("cobweb", Ingredient.of(COBWEB), List.of(
        		output(STRING, 2), 
        		output(STRING, 0.6f),
        		output(STRING, 0.3f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        buildFlower("dandelion", DANDELION, YELLOW_DYE, finishedRecipeConsumer);
        buildFlower("poppy", POPPY, RED_DYE, finishedRecipeConsumer);
        buildFlower("blue_orchid", BLUE_ORCHID, LIGHT_BLUE_DYE, finishedRecipeConsumer);
        buildFlower("allium", ALLIUM, MAGENTA_DYE, finishedRecipeConsumer);
        buildFlower("azure_bluet", AZURE_BLUET, LIGHT_GRAY_DYE, finishedRecipeConsumer);
        buildFlower("red_tulip", RED_TULIP, RED_DYE, finishedRecipeConsumer);
        buildFlower("orange_tulip", ORANGE_TULIP, ORANGE_DYE, finishedRecipeConsumer);
        buildFlower("white_tulip", WHITE_TULIP, WHITE_DYE, finishedRecipeConsumer);
        buildFlower("pink_tulip", PINK_TULIP, PINK_DYE, finishedRecipeConsumer);
        buildFlower("oxeye_daisy", OXEYE_DAISY, WHITE_DYE, finishedRecipeConsumer);
        
        build("shrub", Ingredient.of(DEAD_BUSH), List.of(
        		output(PLANT_MATTER_BROWN.get(), 0.8f), 
        		output(PLANT_MATTER_BROWN.get(), 0.6f),
        		output(PLANT_MATTER_BROWN.get(), 0.3f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("grass", Ingredient.of(GRASS), List.of(
        		output(PLANT_MATTER_GREEN.get(), 0.6f), 
        		output(PLANT_MATTER_GREEN.get(), 0.3f),
        		output(PLANT_MATTER_GREEN.get(), 0.1f),
        		output(PLANT_MATTER_BROWN.get(), 0.05f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("fern", Ingredient.of(FERN), List.of(
        		output(PLANT_MATTER_GREEN.get(), 0.6f), 
        		output(PLANT_MATTER_GREEN.get(), 0.3f),
        		output(PLANT_MATTER_GREEN.get(), 0.1f),
        		output(PLANT_MATTER_BROWN.get(), 0.05f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("leaves", Ingredient.of(ItemTags.LEAVES), List.of(
        		output(PLANT_MATTER_GREEN.get(), 0.3f), 
        		output(PLANT_MATTER_GREEN.get(), 0.1f),
        		output(PLANT_MATTER_GREEN.get(), 0.02f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("vines", Ingredient.of(VINE), List.of(
        		output(PLANT_MATTER_GREEN.get(), 0.15f), 
        		output(PLANT_MATTER_GREEN.get(), 0.1f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("cactus", Ingredient.of(CACTUS), List.of(
        		output(PLANT_MATTER_GREEN.get(), 3), 
        		output(PLANT_MATTER_GREEN.get(), 3, 0.75f),
        		output(PLANT_MATTER_GREEN.get(), 3, 0.5f),
        		output(PLANT_MATTER_BROWN.get(), 3, 0.25f)
        		),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("lily_pad", Ingredient.of(LILY_PAD), List.of(
        		output(PLANT_MATTER_GREEN.get(), 3, 0.15f), 
        		output(PLANT_MATTER_GREEN.get(), 3, 0.1f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("sun_flower", Ingredient.of(VINE), List.of(
        		output(YELLOW_DYE, 2, 0.8f), 
        		output(YELLOW_DYE, 0.6f),
        		output(YELLOW_DYE, 2, 0.3f),
        		output(PLANT_MATTER_GREEN.get(), 0.2f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("tall_grass", Ingredient.of(TALL_GRASS), List.of(
        		output(PLANT_MATTER_GREEN.get(), 2, 0.6f), 
        		output(PLANT_MATTER_GREEN.get(), 0.3f),
        		output(PLANT_MATTER_GREEN.get(), 2, 0.1f),
        		output(PLANT_MATTER_BROWN.get(), 0.15f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("large_fern", Ingredient.of(LARGE_FERN), List.of(
        		output(PLANT_MATTER_GREEN.get(), 2, 0.6f), 
        		output(PLANT_MATTER_GREEN.get(), 0.3f),
        		output(PLANT_MATTER_GREEN.get(), 2, 0.1f),
        		output(PLANT_MATTER_BROWN.get(), 0.15f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("rose_bush", Ingredient.of(ROSE_BUSH), List.of(
        		output(RED_DYE, 2, 0.8f), 
        		output(RED_DYE, 0.6f),
        		output(RED_DYE, 2, 0.3f),
        		output(PLANT_MATTER_GREEN.get(), 0.2f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("poeny", Ingredient.of(PEONY), List.of(
        		output(PINK_DYE, 2, 0.8f), 
        		output(PINK_DYE, 0.6f),
        		output(PINK_DYE, 2, 0.3f),
        		output(PLANT_MATTER_GREEN.get(), 0.2f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        //TODO: find sugar cane tag
        build("sugar_canes", Ingredient.of(SUGAR_CANE), List.of(
        		output(PINK_DYE, 2, 0.8f), 
        		output(PINK_DYE, 0.6f),
        		output(PINK_DYE, 2, 0.3f),
        		output(PLANT_MATTER_GREEN.get(), 0.2f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("flower_pot", Ingredient.of(FLOWER_POT), List.of(
        		output(BRICK, 0.9f), 
        		output(BRICK, 0.3f),
        		output(PINK_DYE, 0.1f),
        		output(COAL_POWDER.get(), 0.05f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        
        build("prismarine_shard", Ingredient.of(PRISMARINE_SHARD), List.of(
        		output(PRISMARINE_CRYSTALS), 
        		output(PRISMARINE_CRYSTALS, 0.1f)),
        		BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
        //probably errorous
        build("soularium_block", Ingredient.of(EIOBlocks.SOULARIUM_BLOCK.get()), List.of(
        		output(SOUL_POWDER.get(), 9)
        		), BASE_ENERGY_PER_OPERATION, finishedRecipeConsumer);
    }
    
    private void buildOre(String name, Ingredient input, Item output, Consumer<FinishedRecipe> recipeConsumer) {
    	build(name, input, List.of(
    			output(output),
    			output(output, 0.33f),
    			output(COBBLESTONE, 0.15f)), 
    			BASE_ENERGY_PER_OPERATION, recipeConsumer);
    }
    
    private void buildFlower(String name, Item input, Item output, Consumer<FinishedRecipe> recipeConsumer){
    	build(name, Ingredient.of(input), List.of(
    			output(output, 0.8f),
    			output(output, 0.6f),
    			output(output, 0.3f),
    			output(PLANT_MATTER_GREEN.get(), 0.1f)), 
    			BASE_ENERGY_PER_OPERATION, recipeConsumer);
    }
    
    private void build1toN(String name, Item input, Item output, int number, Consumer<FinishedRecipe> recipeConsumer) {
    	build(name, Ingredient.of(input), List.of(output(output, number)), BASE_ENERGY_PER_OPERATION, recipeConsumer);
    }
    
    private void build1toN(String name, TagKey<Item> input, Item output, int number, Consumer<FinishedRecipe> recipeConsumer) {
    	build(name, Ingredient.of(input), List.of(output(output, number)), BASE_ENERGY_PER_OPERATION, recipeConsumer);
    }
    
    protected void build(String name, Ingredient input, List<SagMillingRecipe.OutputItem> outputs, int energy, Consumer<FinishedRecipe> recipeConsumer) {
        build(EnderIO.loc("sagmilling/" + name), input, outputs, energy, BonusType.MULTIPLY_OUTPUT, recipeConsumer);
    }

    protected void build(String name, Ingredient input, List<SagMillingRecipe.OutputItem> outputs, int energy, BonusType bonusType, Consumer<FinishedRecipe> recipeConsumer) {
        build(EnderIO.loc("sagmilling/" + name), input, outputs, energy, bonusType, recipeConsumer);
    }

    protected void build(ResourceLocation id, Ingredient input, List<SagMillingRecipe.OutputItem> outputs, int energy, BonusType bonusType, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new FinishedSagMillRecipe(id, input, outputs, energy, bonusType));
    }

    protected SagMillingRecipe.OutputItem output(Item item) {
        return output(item, 1, 1.0f, false);
    }

    protected SagMillingRecipe.OutputItem output(Item item, int count) {
        return output(item, count, 1.0f, false);
    }

    protected SagMillingRecipe.OutputItem output(Item item, float chance) {
        return output(item, 1, chance, false);
    }

    protected SagMillingRecipe.OutputItem output(Item item, int count, float chance) {
        return output(item, count, chance, false);
    }

    protected SagMillingRecipe.OutputItem output(Item item, int count, float chance, boolean optional) {
        return SagMillingRecipe.OutputItem.of(item, count, chance, optional);
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

    protected static class FinishedSagMillRecipe extends EnderFinishedRecipe {

        private final Ingredient input;
        private final List<SagMillingRecipe.OutputItem> outputs;
        private final int energy;
        private final BonusType bonusType;

        public FinishedSagMillRecipe(ResourceLocation id, Ingredient input, List<SagMillingRecipe.OutputItem> outputs, int energy, BonusType bonusType) {
            super(id);
            this.input = input;
            this.outputs = outputs;
            this.energy = energy;
            this.bonusType = bonusType;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("input", input.toJson());
            json.addProperty("energy", energy);
            if (bonusType != BonusType.MULTIPLY_OUTPUT) {
                json.addProperty("bonus", bonusType.toString().toLowerCase());
            }

            JsonArray outputJson = new JsonArray();
            for (SagMillingRecipe.OutputItem item : outputs) {
                JsonObject obj = new JsonObject();

                if (item.isTag()) {
                    obj.addProperty("tag", item.getTag().location().toString());
                } else {
                    obj.addProperty("item", ForgeRegistries.ITEMS.getKey(item.getItem()).toString());
                }

                if (item.getCount() != 1)
                    obj.addProperty("count", item.getCount());

                if (item.getChance() < 1.0f)
                    obj.addProperty("chance", item.getChance());

                if (item.isOptional())
                    obj.addProperty("optional", item.isOptional());

                outputJson.add(obj);
            }
            json.add("outputs", outputJson);

            super.serializeRecipeData(json);
        }

        @Override
        protected Set<String> getModDependencies() {
            // TODO
            return Set.of();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return MachineRecipes.SAGMILLING.serializer().get();
        }
    }
}
