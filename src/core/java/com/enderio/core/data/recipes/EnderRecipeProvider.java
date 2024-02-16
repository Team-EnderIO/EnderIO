package com.enderio.core.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * A base recipe provider, does nothing but contain {@link EnderFinishedRecipe}.
 */
public abstract class EnderRecipeProvider extends RecipeProvider {
    public EnderRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    /**
     * Base class for a finished recipe.
     */
    // TODO: NEO-PORT: Remove, keeping for reference at the moment
    protected abstract static class EnderFinishedRecipe {
        private final ResourceLocation id;
        private final List<ICondition> conditions = new ArrayList<>();

        public EnderFinishedRecipe(ResourceLocation id) {
            this.id = id;
        }

        /**
         * Get the list of mod dependencies for the recipe conditions.
         */
        protected abstract Set<String> getModDependencies();

        public ResourceLocation id() {
            return id;
        }

        /**
         * @apiNote Should still be overridden to serialize recipe data.
         * Should match the structure of the deserializer.
         */
        public void serializeRecipeData(JsonObject json) {
            // Build mod loaded conditions
            Set<String> modDeps = getModDependencies();
            for (String mod : modDeps) {
                // Exclude always-present mods :)
                if (!StringUtils.equalsAny(mod, "minecraft", "forge", "enderio", id().getNamespace())) {
                    conditions.add(new ModLoadedCondition(mod));
                }
            }

            // Write to json
            // TODO: 1.20.2 how to handle the new conditions system.
            /*if (!conditions.isEmpty()) {
                JsonArray jsonConditions = new JsonArray();
                for (ICondition condition : conditions) {
                    jsonConditions.add(CraftingHelper.serialize(condition));
                }
                json.add("conditions", jsonConditions);
            }*/
        }

        public void addCondition(ICondition condition) {
            conditions.add(condition);
        }

        @Nullable
        public AdvancementHolder advancement() {
            return null;
        }
    }
}
