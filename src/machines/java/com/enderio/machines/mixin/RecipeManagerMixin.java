package com.enderio.machines.mixin;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.data.recipes.AlloyRecipeProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@Mixin(value = RecipeManager.class, priority = 1_098)
public class RecipeManagerMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    private final ICondition.IContext context = ICondition.IContext.EMPTY;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void onRecipeReload(Map<ResourceLocation, JsonElement> recipes, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        Map<ResourceLocation, JsonElement> inheritedRecipes = new HashMap<>();

        for (var e : recipes.entrySet()) {
            ResourceLocation recipeId = e.getKey();
            if (recipeId.getPath().startsWith("_")) {
                continue;
            }

            JsonElement jsonElement = e.getValue();
            if (!jsonElement.isJsonObject()) {
                continue;
            }

            enderio$handleRecipe(recipeId, jsonElement.getAsJsonObject(), inheritedRecipes::put);
        }

        recipes.putAll(inheritedRecipes);
    }

    @Unique
    private void enderio$handleRecipe(ResourceLocation recipeId, JsonObject recipeJson,
        BiFunction<ResourceLocation, JsonElement, JsonElement> recipeCallback) {
        if (!recipeJson.has("type") || !recipeJson.get("type").getAsString().equals("minecraft:smelting")) {
            return;
        }

        try {
            if (!recipeJson.isJsonObject() || CraftingHelper.processConditions(recipeJson.getAsJsonObject(), "conditions", this.context)) {
                var recipe = RecipeManager.fromJson(recipeId, GsonHelper.convertToJsonObject(recipeJson, "top element"), this.context);

                if (recipe != null && recipe instanceof SmeltingRecipe smeltingRecipe) {
                    var convertedRecipe = enderio$convert(recipeId, smeltingRecipe);

                    if (convertedRecipe.isEmpty()) {
                        return;
                    }

                    JsonElement serialized = convertedRecipe.get().serializeRecipe();
                    recipeCallback.apply(convertedRecipe.get().getId(), serialized);
                }
            }

        } catch (Exception exception) {
            EnderIO.LOGGER.error("Skipping inheritance of smelting recipe {}: {}", recipeId, exception);
        }
    }

    @Unique
    private static Optional<AlloyRecipeProvider.FinishedAlloyingRecipe> enderio$convert(ResourceLocation originalId, SmeltingRecipe smeltingRecipe) {
        AbstractCookingRecipeAccessor accessor = (AbstractCookingRecipeAccessor) smeltingRecipe;

        if (accessor.getResult().isEmpty()) {
            EnderIO.LOGGER.warn("Unable to inherit the cooking recipe with ID: {}. Reason: The result item is empty.", originalId);
            return Optional.empty();
        }

        String path = "smelting/" + originalId.getNamespace() + "/" + originalId.getPath();
        ResourceLocation id = EnderIO.loc(path);

        CountedIngredient input = new CountedIngredient(accessor.getIngredient(), 1);
        int energy = MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_VANILLA_ITEM_ENERGY.get();
        var recipe = new AlloyRecipeProvider.FinishedAlloyingRecipe(id, List.of(input), accessor.getResult(), energy, accessor.getExperience(), true);
        return Optional.of(recipe);
    }}
