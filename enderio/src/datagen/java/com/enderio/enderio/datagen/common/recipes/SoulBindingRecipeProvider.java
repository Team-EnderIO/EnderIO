package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.soul.binding.ingredients.AnySoulBindableIngredient;
import com.enderio.enderio.api.soul.ingredient.SoulIngredient;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelTier;
import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.foundation.soul.ingredient.EngineSoulIngredient;
import com.enderio.enderio.foundation.soul.ingredient.FarmSoulIngredient;
import com.enderio.enderio.foundation.soul.ingredient.SolarSoulIngredient;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class SoulBindingRecipeProvider extends SubRecipeProvider {

    private HolderGetter<Item> items;

    protected Ingredient ingredientFromTag(TagKey<Item> tag) {
        return Ingredient.of(this.items.getOrThrow(tag));
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        this.items = recipeOutput.lookup(Registries.ITEM);

        build(EIOItems.ENTICING_CRYSTAL, Tags.Items.GEMS_EMERALD, SoulIngredient.of(EntityTypes.VILLAGER), 51200, 4, recipeOutput);
        build(EIOItems.ENDER_CRYSTAL, EIOTags.Items.GEMS_VIBRANT_CRYSTAL, SoulIngredient.of(EntityTypes.ENDERMAN), 76800, 6, recipeOutput);
        build(EIOItems.PRESCIENT_CRYSTAL, EIOTags.Items.GEMS_VIBRANT_CRYSTAL, SoulIngredient.of(EntityTypes.SHULKER), 100000, 8,
            recipeOutput);
        build(EIOItems.FRANK_N_ZOMBIE, EIOItems.Z_LOGIC_CONTROLLER, SoulIngredient.of(EntityTypes.ZOMBIE), 51200, 4, recipeOutput);
        build(EIOItems.SENTIENT_ENDER, EIOItems.ENDER_RESONATOR, SoulIngredient.of(EntityTypes.WITCH), 51200, 4, recipeOutput);
        buildAny(EIOItems.BROKEN_SPAWNER, EIOItems.BROKEN_SPAWNER, SoulIngredient.any(), 288000, 8, false, recipeOutput);
        buildAny(EIOBlocks.POWERED_SPAWNER, EIOBlocks.POWERED_SPAWNER, SoulIngredient.any(), 288000, 8, true, recipeOutput);
        build(EIOBlocks.SOUL_ENGINE, EIOBlocks.SOUL_ENGINE, EngineSoulIngredient.INSTANCE, 188000, 5, recipeOutput);
        build(EIOBlocks.FARMING_STATION, EIOBlocks.FARMING_STATION, FarmSoulIngredient.INSTANCE, 188000, 5, recipeOutput);
        build(EIOItems.PLAYER_TOKEN, EIOItems.DARK_STEEL_BALL, SoulIngredient.of(EntityTypes.VILLAGER), 12800, 1, recipeOutput);
        build(EIOItems.MONSTER_TOKEN, EIOItems.SOULARIUM_BALL, SoulIngredient.of(MobCategory.MONSTER), 12800, 1, recipeOutput);
        build(EIOItems.ANIMAL_TOKEN, EIOItems.SOULARIUM_BALL, SoulIngredient.of(MobCategory.CREATURE), 12800, 1, recipeOutput);
        build(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC), EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC),
            SolarSoulIngredient.INSTANCE, 12800, 8, recipeOutput);
        build(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING), EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING),
            SolarSoulIngredient.INSTANCE, 51200, 12, recipeOutput);
        build(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT), EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT),
            SolarSoulIngredient.INSTANCE, 288000, 14, recipeOutput);

        InfestedBlock.BLOCK_BY_HOST_BLOCK.forEach((original, infested) -> buildInfested(infested, original, recipeOutput));
    }

    private void build(ItemLike output, ItemLike input, SoulIngredient inputSoul, int energy, int exp, RecipeOutput recipeOutput) {
        build(output, Ingredient.of(input), inputSoul, energy, exp, recipeOutput, ItemPredicate.Builder.item().of(items, input).build());
    }

    private void build(ItemLike output, TagKey<Item> input, SoulIngredient inputSoul, int energy, int exp, RecipeOutput recipeOutput) {
        build(output, ingredientFromTag(input), inputSoul, energy, exp, recipeOutput, ItemPredicate.Builder.item().of(items, input).build());
    }

    private void buildAny(ItemLike output, ItemLike input, SoulIngredient inputSoul, int energy, int exp, boolean copyInputData, RecipeOutput recipeOutput) {
        build(output, AnySoulBindableIngredient.of(input), inputSoul, energy, exp, recipeOutput, ItemPredicate.Builder.item().of(items, input).build());
    }

    protected void buildInfested(ItemLike infestedItem, ItemLike original, RecipeOutput recipeOutput) {
        build(infestedItem, Ingredient.of(original), SoulIngredient.of(EntityTypes.SILVERFISH), 10000, 0, recipeOutput,
            ItemPredicate.Builder.item().of(items, original).build());
    }

    protected void build(ItemLike output, Ingredient input, SoulIngredient inputSoul, int energy, int exp, RecipeOutput recipeOutput, ItemPredicate unlock) {
        build(output, input, inputSoul, energy, exp, false, recipeOutput, unlock);
    }

    protected void build(ItemLike output, Ingredient input, SoulIngredient inputSoul, int energy, int exp, boolean copyInputData, RecipeOutput recipeOutput,
        ItemPredicate unlock) {
        RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
        advancementBuilder.unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(unlock));
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, EnderIO.id("soulbinding/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath()));
        var advancement = advancementBuilder.build(recipeOutput, key, RecipeCategory.MISC);
        recipeOutput.accept(key, new SoulBindingRecipe(new ItemStackTemplate(output.asItem()), input, inputSoul, energy, exp, copyInputData), advancement);
    }

}
