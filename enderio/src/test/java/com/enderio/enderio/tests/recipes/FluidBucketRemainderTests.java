package com.enderio.enderio.tests.recipes;

import com.enderio.enderio.init.EIOFluids;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

@ExtendWith(EphemeralTestServerProvider.class)
public class FluidBucketRemainderTests {
    @Test
    public void testCraftingReturnsEachFluidBucket(MinecraftServer server) {
        for (var bucket : EIOFluids.FLUIDS.itemsRegister().getEntries()) {
            var recipe = new ShapedRecipe("", CraftingBookCategory.MISC,
                ShapedRecipePattern.of(Map.of('B', Ingredient.of(bucket.get())), "BB", "BB"),
                Items.STONE.getDefaultInstance());
            var input = CraftingInput.of(2, 2, List.of(
                bucket.get().getDefaultInstance(), bucket.get().getDefaultInstance(),
                bucket.get().getDefaultInstance(), bucket.get().getDefaultInstance()));

            Assertions.assertTrue(recipe.matches(input, server.overworld()));
            var remainingItems = recipe.getRemainingItems(input);
            Assertions.assertEquals(4, remainingItems.size());
            for (var remainder : remainingItems) {
                Assertions.assertTrue(remainder.is(Items.BUCKET), bucket.getId() + " must return an empty bucket");
                Assertions.assertEquals(1, remainder.getCount());
            }
        }
    }
}
