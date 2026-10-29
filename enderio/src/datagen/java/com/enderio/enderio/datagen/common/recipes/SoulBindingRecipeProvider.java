package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.soul.binding.ingredients.AnyBindableSoulSourceIngredient;
import com.enderio.enderio.api.soul.binding.ingredients.AnySoulBindableIngredient;
import com.enderio.enderio.api.soul.ingredient.SoulIngredient;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelTier;
import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.foundation.ingredients.AnySpawnEggIngredient;
import com.enderio.enderio.foundation.soul.ingredient.EngineSoulIngredient;
import com.enderio.enderio.foundation.soul.ingredient.FarmSoulIngredient;
import com.enderio.enderio.foundation.soul.ingredient.SolarSoulIngredient;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.InfestedBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class SoulBindingRecipeProvider extends SubRecipeProvider {

    private HolderGetter<Item> items;

    protected Ingredient ingredientFromTag(TagKey<Item> tag) {
        return Ingredient.of(this.items.getOrThrow(tag));
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        this.items = recipeOutput.lookup(Registries.ITEM);

        // Transformation recipes
        buildTransform(EIOItems.ENTICING_CRYSTAL, ingredientFromTag(Tags.Items.GEMS_EMERALD), SoulIngredient.of(EntityTypes.VILLAGER), 51200, 4, false,
            recipeOutput);
        buildTransform(EIOItems.ENDER_CRYSTAL, ingredientFromTag(EIOTags.Items.GEMS_VIBRANT_CRYSTAL), SoulIngredient.of(EntityTypes.ENDERMAN), 76800, 6, false,
            recipeOutput);
        buildTransform(EIOItems.PRESCIENT_CRYSTAL, ingredientFromTag(EIOTags.Items.GEMS_VIBRANT_CRYSTAL), SoulIngredient.of(EntityTypes.SHULKER), 100000, 8,
            false, recipeOutput);
        buildTransform(EIOItems.FRANK_N_ZOMBIE, Ingredient.of(EIOItems.Z_LOGIC_CONTROLLER), SoulIngredient.of(EntityTypes.ZOMBIE), 51200, 4, false,
            recipeOutput);
        buildTransform(EIOItems.SENTIENT_ENDER, Ingredient.of(EIOItems.ENDER_RESONATOR), SoulIngredient.of(EntityTypes.WITCH), 51200, 4, false, recipeOutput);
        buildTransform(EIOItems.PLAYER_TOKEN, Ingredient.of(EIOItems.DARK_STEEL_BALL), SoulIngredient.of(EntityTypes.VILLAGER), 12800, 1, false, recipeOutput);
        buildTransform(EIOItems.MONSTER_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), SoulIngredient.of(MobCategory.MONSTER), 12800, 1, false, recipeOutput);
        buildTransform(EIOItems.ANIMAL_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), SoulIngredient.of(MobCategory.CREATURE), 12800, 1, false, recipeOutput);

        // Binding recipes
        buildBinding("broken_spawner", AnySoulBindableIngredient.of(EIOItems.BROKEN_SPAWNER), SoulIngredient.any(), 288000, 8, false, recipeOutput);
        buildBinding("powered_spawner", AnySoulBindableIngredient.of(EIOBlocks.POWERED_SPAWNER), SoulIngredient.any(), 288000, 8, true, recipeOutput);
        buildBinding("soul_engine", Ingredient.of(EIOBlocks.SOUL_ENGINE), EngineSoulIngredient.INSTANCE, 188000, 5, true, recipeOutput);
        buildBinding("farming_station", Ingredient.of(EIOBlocks.FARMING_STATION), FarmSoulIngredient.INSTANCE, 188000, 5, false, recipeOutput);
        buildBinding("energetic_photovoltaic_module", Ingredient.of(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC)), SolarSoulIngredient.INSTANCE, 12800,
            8, false, recipeOutput);
        buildBinding("pulsating_photovoltaic_module", Ingredient.of(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING)), SolarSoulIngredient.INSTANCE, 51200,
            12, false, recipeOutput);
        buildBinding("vibrant_photovoltaic_module", Ingredient.of(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT)), SolarSoulIngredient.INSTANCE, 288000, 14,
            false, recipeOutput);

        buildBinding("spawn_egg", DifferenceIngredient.of(AnyBindableSoulSourceIngredient.INSTANCE.toVanilla(), AnySpawnEggIngredient.INSTANCE.toVanilla()),
            Ingredient.of(Items.EGG), SoulIngredient.any(), 288000, 8, false, recipeOutput);

        InfestedBlock.BLOCK_BY_HOST_BLOCK.forEach(
            (original, infested) -> buildTransform(infested, Ingredient.of(original), SoulIngredient.of(EntityTypes.SILVERFISH), 10000, 0, false,
                recipeOutput));
    }

    protected void buildTransform(ItemLike output, Ingredient input, SoulIngredient inputSoul, int energy, int exp, boolean copyInputData,
        RecipeOutput recipeOutput) {
        buildTransform(output, AnyBindableSoulSourceIngredient.INSTANCE.toVanilla(), input, inputSoul, energy, exp, copyInputData, recipeOutput);
    }

    protected void buildTransform(ItemLike output, Ingredient soulSource, Ingredient input, SoulIngredient inputSoul, int energy, int exp,
        boolean copyInputData, RecipeOutput recipeOutput) {
        recipeOutput.accept(
            ResourceKey.create(Registries.RECIPE, EnderIO.id("soulbinding/transform/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath())),
            new SoulBindingRecipe(Optional.of(new ItemStackTemplate(output.asItem())), SoulBindingRecipe.OutputMode.TRANSFORM, input, soulSource, inputSoul,
                energy, exp, copyInputData), null);
    }

    protected void buildBinding(String name, Ingredient input, SoulIngredient inputSoul, int energy, int experience, boolean copyInputData,
        RecipeOutput recipeOutput) {
        buildBinding(name, AnyBindableSoulSourceIngredient.INSTANCE.toVanilla(), input, inputSoul, energy, experience, copyInputData, recipeOutput);
    }

    protected void buildBinding(String name, Ingredient soulSource, Ingredient input, SoulIngredient inputSoul, int energy, int experience,
        boolean copyInputData, RecipeOutput recipeOutput) {
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE, EnderIO.id("soulbinding/binding/" + name)),
            new SoulBindingRecipe(Optional.empty(), SoulBindingRecipe.OutputMode.BIND, input, soulSource, inputSoul, energy, experience, copyInputData), null);
    }

}
