package com.enderio.enderio.compat.jei;

import com.enderio.enderio.compat.jei.helper.FakeGrindingRecipe;
import com.enderio.enderio.content.fire_crafting.FireCraftingRecipe;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(Dist.CLIENT)
public class EnderIOJEIRecipes {
    private static RecipeMap RECIPE_MAP;
    private static final Set<RecipeType<?>> KNOWN_TYPES = new HashSet<>();

    public EnderIOJEIRecipes() {

    }

    public List<RecipeHolder<FireCraftingRecipe>> getAllFireCraftingRecipes() {
        return RECIPE_MAP.byType(EIORecipeTypes.FIRE_CRAFTING.get()).stream().toList();
    }

    public List<FakeGrindingRecipe> getAllGrindingRecipes() {
        return List.of(
            new FakeGrindingRecipe(
                new SizedIngredient(Ingredient.of(Items.DEEPSLATE, Items.COBBLED_DEEPSLATE), 1),
                SizedIngredient.of(Items.FLINT, 1),
                new ItemStack(EIOItems.GRAINS_OF_INFINITY.get())),
            new FakeGrindingRecipe(
                SizedIngredient.of(Items.COAL, 3),
                SizedIngredient.of(Items.FLINT, 1),
                new ItemStack(EIOItems.POWDERED_COAL.get()))
        );
    }

    @SubscribeEvent
    public static void getRecipes(RecipesReceivedEvent event) {
        RECIPE_MAP = event.getRecipeMap();
        KNOWN_TYPES.clear();
        KNOWN_TYPES.addAll(event.getRecipeTypes());
    }
}
