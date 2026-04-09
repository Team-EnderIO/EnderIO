package com.enderio.enderio.mixin;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.google.gson.JsonElement;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;
import java.util.SortedMap;

@Mixin(value = RecipeManager.class)
public abstract class RecipeManagerMixin {

    // Injects right before the recipemap is created so that the additional recipes are included.
    // We can't inject before RETURN because that's before the return instruction, not before the map is created.
    @Inject(method = "prepare", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeMap;create(Ljava/lang/Iterable;)Lnet/minecraft/world/item/crafting/RecipeMap;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void enderio$prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfoReturnable<RecipeMap> ci,
        SortedMap<Identifier, Recipe<?>> sortedmap, ConditionalOps<JsonElement> conditionalOps, List<RecipeHolder<?>> recipeHolders) {
        // Loop over all recipes at this point and insert
        for (int i = 0; i < recipeHolders.size(); i++) {
            var recipeHolder = recipeHolders.get(i);
            if (recipeHolder.value() instanceof SmeltingRecipe smeltingRecipe) {
                var optionalAlloyRecipe = enderio$convertSmeltingRecipe(recipeHolder.id().identifier(), smeltingRecipe);
                if (optionalAlloyRecipe.isPresent()) {
                    recipeHolders.add(i + 1, optionalAlloyRecipe.get());
                    i++; // Skip the inserted recipe
                }
            }
        }
    }

    @Unique
    private static Optional<RecipeHolder<AlloySmeltingRecipe>> enderio$convertSmeltingRecipe(
            Identifier originalId, SmeltingRecipe smeltingRecipe) {

        SizedIngredient input = new SizedIngredient(smeltingRecipe.input(), 1);
        int energy = MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_VANILLA_ITEM_ENERGY.get();
        AlloySmeltingRecipe recipe = new AlloySmeltingRecipe(List.of(input), smeltingRecipe.result(), energy,
                smeltingRecipe.experience(), true);

        String path = "smelting/" + originalId.getNamespace() + "/" + originalId.getPath();
        ResourceKey<Recipe<?>> id = ResourceKey.create(Registries.RECIPE, EnderIO.id(path));
        
        return Optional.of(new RecipeHolder<>(id, recipe));
    }
}
