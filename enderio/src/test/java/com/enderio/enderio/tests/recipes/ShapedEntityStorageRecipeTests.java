package com.enderio.enderio.tests.recipes;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.foundation.soul.ShapedEntityStorageRecipe;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

@ExtendWith(EphemeralTestServerProvider.class)
public class ShapedEntityStorageRecipeTests {
    @Test
    public void testSoulTransferredToResult(MinecraftServer server) {
        var shapedRecipe = new ShapedRecipe(new Recipe.CommonInfo(true), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, "no"),
            ShapedRecipePattern.of(
                Map.of(
                    'A', Ingredient.of(EIOItems.SOUL_VIAL.get()),
                    'P', Ingredient.of(Items.SAND)),
                "AP"),
            new ItemStackTemplate(EIOItems.BROKEN_SPAWNER));

        var input = CraftingInput.of(3, 3, List.of(
            SoulVialItem.forSoul(Soul.of(EntityType.ALLAY)), Items.SAND.getDefaultInstance(), ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));

        var wrappedRecipe = new ShapedEntityStorageRecipe(shapedRecipe);
        var result = wrappedRecipe.assemble(input);

        Assertions.assertEquals(EIOItems.BROKEN_SPAWNER.get(), result.getItem());
        Assertions.assertEquals(Soul.of(EntityType.ALLAY), SoulBoundUtils.getBoundSoul(result));
    }

    @Test
    public void testSoulNotTransferredToReadOnlyBindableResult(MinecraftServer server) {
        var shapedRecipe = new ShapedRecipe(new Recipe.CommonInfo(true), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, "no"),
            ShapedRecipePattern.of(
                Map.of(
                    'A', Ingredient.of(EIOItems.SOUL_VIAL.get()),
                    'P', Ingredient.of(Items.SAND)),
                "AP"),
            new ItemStackTemplate(EIOItems.SOUL_VIAL));

        var input = CraftingInput.of(3, 3, List.of(
            SoulVialItem.forSoul(Soul.of(EntityType.ALLAY)), Items.SAND.getDefaultInstance(), ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));

        var wrappedRecipe = new ShapedEntityStorageRecipe(shapedRecipe);
        var result = wrappedRecipe.assemble(input);

        Assertions.assertEquals(EIOItems.SOUL_VIAL.get(), result.getItem());
        Assertions.assertEquals(Soul.EMPTY, SoulBoundUtils.getBoundSoul(result));
    }

    @Test
    public void testSoulNotTransferredToNonBindableResult(MinecraftServer server) {
        var shapedRecipe = new ShapedRecipe(new Recipe.CommonInfo(true), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, "no"),
            ShapedRecipePattern.of(
                Map.of(
                    'A', Ingredient.of(EIOItems.SOUL_VIAL.get()),
                    'P', Ingredient.of(Items.SAND)),
                "AP"),
            new ItemStackTemplate(Items.OAK_BUTTON));

        var input = CraftingInput.of(3, 3, List.of(
            SoulVialItem.forSoul(Soul.of(EntityType.ALLAY)), Items.SAND.getDefaultInstance(), ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));

        var wrappedRecipe = new ShapedEntityStorageRecipe(shapedRecipe);
        var result = wrappedRecipe.assemble(input);

        Assertions.assertEquals(Items.OAK_BUTTON, result.getItem());
        Assertions.assertEquals(Soul.EMPTY, SoulBoundUtils.getBoundSoul(result));
    }
}
