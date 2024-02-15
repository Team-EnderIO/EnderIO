package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.recipe.RecipeDataUtil;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.souldata.EngineSoul;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SoulBindingRecipeProvider extends EnderRecipeProvider {

    public SoulBindingRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
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
        build(EIOItems.PLAYER_TOKEN, Ingredient.of(EIOItems.DARK_STEEL_BALL), 12800, 1, EntityType.VILLAGER, recipeOutput);
        build(EIOItems.MONSTER_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), 12800, 1, MobCategory.MONSTER, recipeOutput);
        build(EIOItems.ANIMAL_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), 12800, 1, MobCategory.CREATURE, recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, EntityType<? extends Entity> entityType, RecipeOutput recipeOutput) {
        recipeOutput.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath()), output, input, energy, exp, BuiltInRegistries.ENTITY_TYPE.getKey(entityType), null, null));
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, MobCategory mobCategory, RecipeOutput recipeOutput) {
        recipeOutput.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath()), output, input, energy, exp, null, mobCategory, null));
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, String souldata, RecipeOutput recipeOutput) {
        recipeOutput.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath()), output, input, energy, exp, null, null, souldata));
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, RecipeOutput recipeOutput) {
        recipeOutput.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath()), output, input, energy, exp, null, null, null));
    }

    protected static class FinishedSoulBindingRecipe extends EnderFinishedRecipe {

        private final Item output;
        private final Ingredient input;
        private final int energy;
        private final int exp;
        @Nullable
        private final ResourceLocation entityType;
        @Nullable
        private final MobCategory mobCategory;
        @Nullable
        private final String souldata;

        public FinishedSoulBindingRecipe(ResourceLocation id, ItemLike output, Ingredient input, int energy, int exp, @Nullable ResourceLocation entityType, @Nullable MobCategory mobCategory, @Nullable String souldata) {
            super(id);
            this.output = output.asItem();
            this.input = input;
            this.energy = energy;
            this.exp = exp;

            if (entityType != null && mobCategory != null) {
                throw new IllegalStateException("entityType and mobCategory are mutually exclusive!");
            }

            if (souldata != null && mobCategory != null) {
                throw new IllegalStateException("souldata and mobCategory are mutually exclusive!");
            }

            if (entityType != null && souldata != null) {
                throw new IllegalStateException("entityType and souldata are mutually exclusive!");
            }

            this.entityType = entityType;
            this.mobCategory = mobCategory;
            this.souldata = souldata;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("output", BuiltInRegistries.ITEM.getKey(output).toString());
            json.add("input", input.toJson(false));

            json.addProperty("energy", energy);
            json.addProperty("exp", exp);

            if (entityType != null) {
                json.addProperty("entity_type", entityType.toString());
            }

            if (mobCategory != null) {
                json.addProperty("mob_category", mobCategory.getName());
            }

            if (souldata != null) {
                json.addProperty("souldata", souldata);
            }

            super.serializeRecipeData(json);
        }

        @Override
        protected Set<String> getModDependencies() {
            Set<String> mods = new HashSet<>(RecipeDataUtil.getIngredientModIds(input));
            mods.add(BuiltInRegistries.ITEM.getKey(output).getNamespace());
            return mods;
        }

        @Override
        public RecipeSerializer<?> type() {
            return MachineRecipes.SOUL_BINDING.serializer().get();
        }
    }
}
