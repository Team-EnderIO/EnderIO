package com.enderio.machines.mixin;

import com.enderio.EnderIOBase;
import com.enderio.machines.common.blocks.alloy.AlloySmeltingRecipe;
import com.enderio.machines.common.config.MachinesConfig;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ClassWithOnlyPrivateConstructors")
@Mixin(value = RecipeManager.class, priority = 1_098)
public abstract class RecipeManagerMixin extends SimpleJsonResourceReloadListener {

    private static Logger LOGGER;

    private RecipeManagerMixin(Gson gson, String directory) {
        super(gson, directory);
    }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void onRecipeReload(Map<ResourceLocation, JsonElement> recipes, ResourceManager resourceManager,
            ProfilerFiller profiler, CallbackInfo ci) {
        RegistryOps<JsonElement> registryOps = makeConditionalOps();
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

            enderio$handleRecipe(registryOps, recipeId, jsonElement.getAsJsonObject(), inheritedRecipes::put);
        }

        recipes.putAll(inheritedRecipes);
    }

    @Unique
    private void enderio$handleRecipe(RegistryOps<JsonElement> registryOps, ResourceLocation recipeId,
            JsonObject recipeJson, BiFunction<ResourceLocation, JsonElement, JsonElement> recipeCallback) {
        if (!recipeJson.has("type") || !recipeJson.get("type").getAsString().equals("minecraft:smelting")) {
            return;
        }

        try {
            var decoded = Recipe.CONDITIONAL_CODEC.parse(registryOps, recipeJson).getOrThrow(JsonParseException::new);
            if (decoded.isPresent() && decoded.get().carrier() instanceof SmeltingRecipe smeltingRecipe) {
                var convertedHolder = enderio$convertSmeltingRecipe(recipeId, smeltingRecipe);
                if (convertedHolder.isEmpty()) {
                    return;
                }

                DataResult<JsonElement> result = Recipe.CODEC.encodeStart(JsonOps.INSTANCE,
                        convertedHolder.get().value());
                recipeCallback.apply(convertedHolder.get().id(), result.getOrThrow());
            }
        } catch (Exception exception) {
            LOGGER.error("[EnderIO] Skipping inheritance of smelting recipe {}: {}", recipeId, exception);
        }
    }

    @Unique
    private Optional<RecipeHolder<AlloySmeltingRecipe>> enderio$convertSmeltingRecipe(ResourceLocation originalId,
            SmeltingRecipe smeltingRecipe) {
        AbstractCookingRecipeAccessor accessor = (AbstractCookingRecipeAccessor) smeltingRecipe;

        if (accessor.getResult().isEmpty()) {
            LOGGER.warn("[EnderIO] Unable to inherit the cooking recipe with ID: {}. Reason: The result item is empty.",
                    originalId);
            return Optional.empty();
        }

        SizedIngredient input = new SizedIngredient(accessor.getIngredient(), 1);
        int energy = MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_VANILLA_ITEM_ENERGY.get();
        AlloySmeltingRecipe recipe = new AlloySmeltingRecipe(List.of(input), accessor.getResult(), energy,
                accessor.getExperience(), true);

        String path = "smelting/" + originalId.getNamespace() + "/" + originalId.getPath();
        ResourceLocation id = EnderIOBase.loc(path);
        return Optional.of(new RecipeHolder<>(id, recipe));
    }
}
