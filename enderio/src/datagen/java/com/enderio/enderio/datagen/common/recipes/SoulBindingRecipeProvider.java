package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.soul.binding.ingredients.AnySoulBindableIngredient;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelTier;
import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.foundation.souldata.EngineSoul;
import com.enderio.enderio.foundation.souldata.FarmSoul;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.InfestedBlock;
import net.neoforged.neoforge.common.Tags;

import java.util.Optional;

public class SoulBindingRecipeProvider extends SubRecipeProvider {

    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        build(EIOItems.ENTICING_CRYSTAL, Ingredient.of(Tags.Items.GEMS_EMERALD), 51200, 4, EntityType.VILLAGER,
                recipeOutput);
        build(EIOItems.ENDER_CRYSTAL, Ingredient.of(EIOTags.Items.GEMS_VIBRANT_CRYSTAL), 76800, 6, EntityType.ENDERMAN,
                recipeOutput);
        build(EIOItems.PRESCIENT_CRYSTAL, Ingredient.of(EIOTags.Items.GEMS_VIBRANT_CRYSTAL), 100000, 8,
                EntityType.SHULKER, recipeOutput);
        build(EIOItems.FRANK_N_ZOMBIE, Ingredient.of(EIOItems.Z_LOGIC_CONTROLLER), 51200, 4, EntityType.ZOMBIE,
                recipeOutput);
        build(EIOItems.SENTIENT_ENDER, Ingredient.of(EIOItems.ENDER_RESONATOR), 51200, 4, EntityType.WITCH,
                recipeOutput);
        build(EIOItems.BROKEN_SPAWNER, AnySoulBindableIngredient.of(EIOItems.BROKEN_SPAWNER), 288000, 8, recipeOutput);
        build(EIOBlocks.POWERED_SPAWNER, AnySoulBindableIngredient.of(EIOBlocks.POWERED_SPAWNER), 288000, 8, true,
                recipeOutput);
        build(EIOBlocks.SOUL_ENGINE, Ingredient.of(EIOBlocks.SOUL_ENGINE), 188000, 5, EngineSoul.NAME,
                recipeOutput);
        build(EIOBlocks.FARMING_STATION, Ingredient.of(EIOBlocks.FARMING_STATION), 188000, 5, FarmSoul.NAME,
                recipeOutput);
        build(EIOItems.PLAYER_TOKEN, Ingredient.of(EIOItems.DARK_STEEL_BALL), 12800, 1, EntityType.VILLAGER,
                recipeOutput);
        build(EIOItems.MONSTER_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), 12800, 1, MobCategory.MONSTER,
                recipeOutput);
        build(EIOItems.ANIMAL_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), 12800, 1, MobCategory.CREATURE,
                recipeOutput);
        build(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC),
                Ingredient.of(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.ENERGETIC)), 12800, 8, EntityType.PHANTOM,
                recipeOutput);
        build(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING),
                Ingredient.of(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.PULSATING)), 51200, 12, EntityType.PHANTOM,
                recipeOutput);
        build(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT),
                Ingredient.of(EIOBlocks.SOLAR_PANELS.get(SolarPanelTier.VIBRANT)), 288000, 14, EntityType.PHANTOM,
                recipeOutput);

        InfestedBlock.BLOCK_BY_HOST_BLOCK.forEach((original, infested) -> buildInfested(infested, original, recipeOutput));
    }

    protected void buildInfested(ItemLike infestedItem, ItemLike original, RecipeOutput recipeOutput) {
        build(infestedItem, Ingredient.of(original), 10000, 0, EntityType.SILVERFISH, recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp,
            EntityType<? extends Entity> entityType, RecipeOutput recipeOutput) {
        build(output, input, energy, exp, Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(entityType)),
                Optional.empty(), Optional.empty(), false, recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, MobCategory mobCategory,
            RecipeOutput recipeOutput) {
        build(output, input, energy, exp, Optional.empty(), Optional.of(mobCategory), Optional.empty(), false,
                recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, String souldata,
            RecipeOutput recipeOutput) {
        build(output, input, energy, exp, Optional.empty(), Optional.empty(), Optional.of(souldata), false,
                recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, RecipeOutput recipeOutput) {
        build(output, input, energy, exp, Optional.empty(), Optional.empty(), Optional.empty(), false, recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, boolean copyInputData,
            RecipeOutput recipeOutput) {
        build(output, input, energy, exp, Optional.empty(), Optional.empty(), Optional.empty(), copyInputData,
                recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, Optional<ResourceLocation> entityType,
            Optional<MobCategory> mobCategory, Optional<String> souldata, boolean copyInputData,
            RecipeOutput recipeOutput) {
        recipeOutput.accept(EnderIO.rl("soulbinding/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath()),
                new SoulBindingRecipe(new ItemStack(output), input, energy, exp, entityType, mobCategory, souldata,
                        copyInputData),
                null);
    }

}
