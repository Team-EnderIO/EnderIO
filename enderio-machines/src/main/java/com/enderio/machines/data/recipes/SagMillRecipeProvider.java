package com.enderio.machines.data.recipes;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.data.recipe.EnderRecipeProvider;
import com.enderio.machines.EIOMachines;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.versions.forge.ForgeVersion;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SagMillRecipeProvider extends EnderRecipeProvider {

    private static final TagKey<Item> SULFUR = ItemTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "dusts/sulfur"));

    public SagMillRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        build("coal", Ingredient.of(Items.COAL), List.of(
            output(EIOItems.COAL_POWDER.get()),
            output(EIOItems.COAL_POWDER.get(), 0.1f),
            output(SULFUR, 1, 0.1f, true)),
            100, finishedRecipeConsumer);
    }

    protected void build(String name, Ingredient input, List<SagMillingRecipe.OutputItem> outputs, int energy, Consumer<FinishedRecipe> recipeConsumer) {
        build(EIOMachines.loc("sagmilling/" + name), input, outputs, energy, BonusType.MULTIPLY_OUTPUT, recipeConsumer);
    }

    protected void build(String name, Ingredient input, List<SagMillingRecipe.OutputItem> outputs, int energy, BonusType bonusType, Consumer<FinishedRecipe> recipeConsumer) {
        build(EIOMachines.loc("sagmilling/" + name), input, outputs, energy, bonusType, recipeConsumer);
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
                    obj.addProperty("item", item.getItem().getRegistryName().toString());
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
            return MachineRecipes.Serializer.SAGMILLING.get();
        }
    }
}
