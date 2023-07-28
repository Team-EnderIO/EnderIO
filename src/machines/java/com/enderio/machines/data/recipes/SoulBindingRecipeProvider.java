package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.data.recipe.RecipeDataUtil;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SoulBindingRecipeProvider extends EnderRecipeProvider {

    public SoulBindingRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        build(EIOItems.ENTICING_CRYSTAL, Ingredient.of(Items.EMERALD), 100000, 4, EntityType.VILLAGER, pFinishedRecipeConsumer);
        build(EIOItems.ENDER_CRYSTAL, Ingredient.of(EIOItems.VIBRANT_CRYSTAL), 150000, 6, EntityType.ENDERMAN, pFinishedRecipeConsumer);
        build(EIOItems.PRESCIENT_CRYSTAL, Ingredient.of(EIOItems.VIBRANT_CRYSTAL), 200000, 8, EntityType.SHULKER, pFinishedRecipeConsumer);
        build(EIOItems.FRANK_N_ZOMBIE, Ingredient.of(EIOItems.Z_LOGIC_CONTROLLER), 100000, 4, EntityType.ZOMBIE, pFinishedRecipeConsumer);
        build(EIOItems.SENTIENT_ENDER, Ingredient.of(EIOItems.ENDER_RESONATOR), 100000, 4, EntityType.WITCH, pFinishedRecipeConsumer);
        build(EIOItems.BROKEN_SPAWNER, Ingredient.of(EIOItems.BROKEN_SPAWNER), 2500000, 8, pFinishedRecipeConsumer);
        build(EIOItems.PLAYER_TOKEN, Ingredient.of(EIOItems.DARK_STEEL_BALL), 25000, 1, EntityType.VILLAGER, pFinishedRecipeConsumer);
        build(EIOItems.MONSTER_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), 25000, 1, MobCategory.MONSTER, pFinishedRecipeConsumer);
        build(EIOItems.ANIMAL_TOKEN, Ingredient.of(EIOItems.SOULARIUM_BALL), 25000, 1, MobCategory.CREATURE, pFinishedRecipeConsumer);
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, EntityType<? extends Entity> entityType, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + ForgeRegistries.ITEMS.getKey(output.asItem()).getPath()), output, input, energy, exp, ForgeRegistries.ENTITY_TYPES.getKey(entityType), null));
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, MobCategory mobCategory, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + ForgeRegistries.ITEMS.getKey(output.asItem()).getPath()), output, input, energy, exp, null, mobCategory));
    }

    protected void build(ItemLike output, Ingredient input, int energy, int exp, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + ForgeRegistries.ITEMS.getKey(output.asItem()).getPath()), output, input, energy, exp, null, null));
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

        public FinishedSoulBindingRecipe(ResourceLocation id, ItemLike output, Ingredient input, int energy, int exp, @Nullable ResourceLocation entityType, @Nullable MobCategory mobCategory) {
            super(id);
            this.output = output.asItem();
            this.input = input;
            this.energy = energy;
            this.exp = exp;

            if (entityType != null && mobCategory != null) {
                throw new IllegalArgumentException("entityType and mobCategory are mutually exclusive!");
            }

            this.entityType = entityType;
            this.mobCategory = mobCategory;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("output", ForgeRegistries.ITEMS.getKey(output).toString());
            json.add("input", input.toJson());

            json.addProperty("energy", energy);
            json.addProperty("exp", exp);

            if (entityType != null) {
                json.addProperty("entity_type", entityType.toString());
            }

            if (mobCategory != null) {
                json.addProperty("mob_category", mobCategory.getName());
            }

            super.serializeRecipeData(json);
        }

        @Override
        protected Set<String> getModDependencies() {
            Set<String> mods = new HashSet<>(RecipeDataUtil.getIngredientModIds(input));
            mods.add(ForgeRegistries.ITEMS.getKey(output).getNamespace());
            return mods;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return MachineRecipes.SOUL_BINDING.serializer().get();
        }
    }
}
