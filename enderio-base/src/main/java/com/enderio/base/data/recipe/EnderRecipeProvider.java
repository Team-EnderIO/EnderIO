package com.enderio.base.data.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A base recipe provider, does nothing but contain {@link EnderRecipeProvider.EnderFinishedRecipe}.
 */
public abstract class EnderRecipeProvider extends RecipeProvider {
    public EnderRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    /**
     * Base class for a finished recipe.
     */
    protected abstract static class EnderFinishedRecipe implements FinishedRecipe {
        private final ResourceLocation id;
        private final List<ICondition> conditions = new ArrayList<>();

        public EnderFinishedRecipe(ResourceLocation id) {
            this.id = id;
        }

        /**
         * Get the list of mod dependencies for the recipe conditions.
         */
        protected abstract Set<String> getModDependencies();

        @Override
        public ResourceLocation getId() {
            return id;
        }

        /**
         * @apiNote Should still be overridden to serialize recipe data.
         * Should match the structure of the deserializer.
         */
        @Override
        public void serializeRecipeData(JsonObject json) {
            // Build mod loaded conditions
            Set<String> modDeps = getModDependencies();
            for (String mod : modDeps) {
                // Exclude always-present mods :)
                if (!StringUtils.equalsAny(mod, "minecraft", "forge", "enderio", getId().getNamespace()))
                    conditions.add(new ModLoadedCondition(mod));
            }

            // Write to json
            if (!conditions.isEmpty()) {
                JsonArray jsonConditions = new JsonArray();
                for (ICondition condition : conditions) {
                    jsonConditions.add(CraftingHelper.serialize(condition));
                }
                json.add("conditions", jsonConditions);
            }
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
