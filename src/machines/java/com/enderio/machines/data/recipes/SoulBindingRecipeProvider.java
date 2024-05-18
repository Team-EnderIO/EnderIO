package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.recipe.SoulBindingRecipe;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.FarmSoul;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SoulBindingRecipeProvider extends RecipeProvider {

    public SoulBindingRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        build(EIOItems.ENTICING_CRYSTAL, Ingredient.of(Tags.Items.GEMS_EMERALD), 51200, 4, EntityType.VILLAGER, recipeOutput);
        build(EIOItems.ENDER_CRYSTAL, Ingredient.of(EIOTags.Items.GEMS_VIBRANT_CRYSTAL), 76800, 6, EntityType.ENDERMAN, recipeOutput);
        build(EIOItems.PRESCIENT_CRYSTAL, Ingredient.of(EIOTags.Items.GEMS_VIBRANT_CRYSTAL), 100000, 8, EntityType.SHULKER, recipeOutput);
        build(EIOItems.FRANK_N_ZOMBIE, Ingredient.of(EIOItems.Z_LOGIC_CONTROLLER), 51200, 4, EntityType.ZOMBIE, recipeOutput);
        build(EIOItems.SENTIENT_ENDER, Ingredient.of(EIOItems.ENDER_RESONATOR), 51200, 4, EntityType.WITCH, recipeOutput);
        build(EIOItems.BROKEN_SPAWNER, Ingredient.of(EIOItems.BROKEN_SPAWNER), 288000, 8, recipeOutput);
        build(MachineBlocks.SOUL_ENGINE, Ingredient.of(MachineBlocks.SOUL_ENGINE), 188000, 5, EngineSoul.NAME, recipeOutput);
        build(MachineBlocks.FARMING_STATION, Ingredient.of(MachineBlocks.FARMING_STATION), 188000, 5, FarmSoul.NAME, recipeOutput);
        build(EIOItems.PLAYER_TOKEN, Ingredient.of(EIOItems.DARK_STEEL_BALL), 12800, 1, EntityType.VILLAGER, recipeOutput);
        build(EIOItems.MONSTER_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), 12800, 1, MobCategory.MONSTER, recipeOutput);
        build(EIOItems.ANIMAL_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), 12800, 1, MobCategory.CREATURE, recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, EntityType<? extends Entity> entityType, RecipeOutput recipeOutput) {
        build(output, input, energy, exp, Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(entityType)),
            Optional.empty(), Optional.empty(), recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, MobCategory mobCategory, RecipeOutput recipeOutput) {
        build(output, input, energy, exp, Optional.empty(), Optional.of(mobCategory), Optional.empty(), recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, String souldata, RecipeOutput recipeOutput) {
        build(output, input, energy, exp, Optional.empty(), Optional.empty(), Optional.of(souldata), recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, RecipeOutput recipeOutput) {
        build(output, input, energy, exp, Optional.empty(), Optional.empty(), Optional.empty(), recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, Optional<ResourceLocation> entityType, Optional<MobCategory> mobCategory,
        Optional<String> souldata, RecipeOutput recipeOutput) {
        recipeOutput.accept(
            EnderIO.loc("soulbinding/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath()),
            new SoulBindingRecipe(output.asItem(), input, energy, exp, entityType, mobCategory, souldata),
            null);
    }

}
