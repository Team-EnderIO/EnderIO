package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.enchanter.EnchanterRecipe;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.enderio.enderio.content.machines.vat.FermentingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIORecipeDisplays {
    private static final DeferredRegister<RecipeDisplay.Type<?>> RECIPE_DISPLAYS = DeferredRegister.create(Registries.RECIPE_DISPLAY, EnderIO.MOD_ID);

    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<EnchanterRecipe.EnchantingDisplay>> ENCHANTING =
        RECIPE_DISPLAYS.register("enchanting", () -> EnchanterRecipe.EnchantingDisplay.TYPE);
    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<AlloySmeltingRecipe.AlloySmelterDisplay>> ALLOY_SMELTING =
        RECIPE_DISPLAYS.register("alloy_smelting", () -> AlloySmeltingRecipe.AlloySmelterDisplay.TYPE);
    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<FermentingRecipe.FermentingDisplay>> FERMENTING =
        RECIPE_DISPLAYS.register("fermenting", () -> FermentingRecipe.FermentingDisplay.TYPE);

    public static void register(IEventBus bus) {
        RECIPE_DISPLAYS.register(bus);
    }
}
