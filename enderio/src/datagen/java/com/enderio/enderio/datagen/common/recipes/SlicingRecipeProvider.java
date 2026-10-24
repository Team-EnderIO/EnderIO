package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class SlicingRecipeProvider extends SubRecipeProvider {

    private HolderGetter<Item> items;

    protected Ingredient ingredientFromTag(TagKey<Item> tag) {
        return Ingredient.of(this.items.getOrThrow(tag));
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        this.items = recipeOutput.lookup(Registries.ITEM);
        // TODO: Tormented enderman head

        build(EIOItems.ZOMBIE_ELECTRODE.get(),
                List.of(ingredientFromTag(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), Ingredient.of(Items.ZOMBIE_HEAD),
                    ingredientFromTag(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), ingredientFromTag(EIOTags.Items.SILICON),
                        Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), ingredientFromTag(EIOTags.Items.SILICON)),
                20000, recipeOutput, ItemPredicate.Builder.item().of(items, Items.ZOMBIE_HEAD).build());

        build(EIOItems.Z_LOGIC_CONTROLLER.get(),
                List.of(ingredientFromTag(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.ZOMBIE_HEAD),
                    ingredientFromTag(EIOTags.Items.INGOTS_SOULARIUM), ingredientFromTag(EIOTags.Items.SILICON),
                        Ingredient.of(Items.REDSTONE), ingredientFromTag(EIOTags.Items.SILICON)),
                20000, recipeOutput, ItemPredicate.Builder.item().of(items, Items.ZOMBIE_HEAD).build());

        build(EIOItems.SKELETAL_CONTRACTOR.get(),
                List.of(ingredientFromTag(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.SKELETON_SKULL),
                    ingredientFromTag(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.ROTTEN_FLESH),
                        Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(Items.ROTTEN_FLESH)),
                20000, recipeOutput, ItemPredicate.Builder.item().of(items, Items.SKELETON_SKULL).build());

        build(EIOItems.GUARDIAN_DIODE.get(),
                List.of(ingredientFromTag(EIOTags.Items.INGOTS_ENERGETIC_ALLOY),
                        ingredientFromTag(EIOTags.Items.DUSTS_PRISMARINE),
                        ingredientFromTag(EIOTags.Items.INGOTS_ENERGETIC_ALLOY),ingredientFromTag(Tags.Items.GEMS_PRISMARINE),
                        Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), ingredientFromTag(Tags.Items.GEMS_PRISMARINE)),
                20000, recipeOutput, ItemPredicate.Builder.item().of(items, EIOTags.Items.DUSTS_PRISMARINE).build());

        build(EIOItems.ENDER_RESONATOR.get(),
                List.of(ingredientFromTag(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(EIOBlocks.ENDERMAN_HEAD),
                        ingredientFromTag(EIOTags.Items.INGOTS_SOULARIUM), // TODO EnderSkull
                        ingredientFromTag(EIOTags.Items.SILICON), Ingredient.of(EIOItems.VIBRANT_ALLOY_INGOT.get()),
                        ingredientFromTag(EIOTags.Items.SILICON)),
                20000, recipeOutput, ItemPredicate.Builder.item().of(items, EIOBlocks.ENDERMAN_HEAD).build());

    }

    protected void build(Item output, List<Ingredient> inputs, int energy, RecipeOutput recipeOutput, ItemPredicate unlock) {
        RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
        advancementBuilder.unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(unlock));
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, EnderIO.id("slicing/" + BuiltInRegistries.ITEM.getKey(output).getPath()));
        var advancement = advancementBuilder.build(recipeOutput, key, RecipeCategory.MISC);
        recipeOutput.accept(key, new SlicingRecipe(new ItemStackTemplate(output), inputs, energy), advancement);
    }

}
