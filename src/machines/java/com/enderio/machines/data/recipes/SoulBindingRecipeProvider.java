package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SoulBindingRecipeProvider extends EnderRecipeProvider {

    public SoulBindingRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        build(MachineBlocks.POWERED_SPAWNER.get().asItem(), List.of(Ingredient.of(EIOItems.FILLED_SOUL_VIAL.get()), Ingredient.of(EIOItems.BROKEN_SPAWNER.get())), 2000, 10, pFinishedRecipeConsumer);
    }

    protected void build(Item output, List<Ingredient> inputs, int energy, int exp, EntityType<? extends Entity> entityType, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + ForgeRegistries.ITEMS.getKey(output).getPath()), output, inputs, energy, exp, ForgeRegistries.ENTITY_TYPES.getKey(entityType)));
    }

    protected void build(Item output, List<Ingredient> inputs, int energy, int exp, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSoulBindingRecipe(EnderIO.loc("soulbinding/" + ForgeRegistries.ITEMS.getKey(output).getPath()), output, inputs, energy, exp, null));
    }

    protected static class FinishedSoulBindingRecipe extends EnderFinishedRecipe {

        private final Item output;
        private final List<Ingredient> inputs;
        private final int energy;
        private final int exp;
        @Nullable
        private final ResourceLocation entityType;

        public FinishedSoulBindingRecipe(ResourceLocation id, Item output, List<Ingredient> inputs, int energy, int exp, @Nullable ResourceLocation entityType) {
            super(id);
            this.output = output;
            this.inputs = inputs;
            this.energy = energy;
            this.exp = exp;
            this.entityType = entityType;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("output", ForgeRegistries.ITEMS.getKey(output).toString());
            JsonArray inputsArray = new JsonArray();
            for (Ingredient input : inputs) {
                inputsArray.add(input.toJson());
            }
            json.add("inputs", inputsArray);

            json.addProperty("energy", energy);
            json.addProperty("exp", exp);

            if (entityType != null) {
                json.addProperty("entitytype", entityType.toString());
            }

            super.serializeRecipeData(json);
        }

        @Override
        protected Set<String> getModDependencies() {
            Set<String> mods = new HashSet<>();
            // TODO: 1.19: Ingredient#getItems cannot be called during datagen. Needs a new solution.
            //            inputs.stream().map(ing -> Arrays.stream(ing.getItems()).map(item -> mods.add(ForgeRegistries.ITEMS.getKey(item.getItem()).getNamespace())));
            mods.add(ForgeRegistries.ITEMS.getKey(output).getNamespace());
            return mods;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return MachineRecipes.SOUL_BINDING.serializer().get();
        }
    }
}
