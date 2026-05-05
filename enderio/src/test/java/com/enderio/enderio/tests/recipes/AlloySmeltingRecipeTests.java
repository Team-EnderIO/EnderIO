package com.enderio.enderio.tests.recipes;

import com.enderio.enderio.api.recipes.alloy.AlloySmeltingInput;
import com.enderio.enderio.content.machines.alloy.AlloySmelterMode;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

// Note; we're passing 'null' in matches for level because we *know* level is unused.
@ExtendWith(EphemeralTestServerProvider.class)
public class AlloySmeltingRecipeTests {
    // region Smelting Recipe Matches

    private final AlloySmeltingRecipe SMELTING_RECIPE = new AlloySmeltingRecipe(List.of(SizedIngredient.of(Items.BEEF, 1)),
        new ItemStackTemplate(Items.COOKED_BEEF), 100, 0f, true);

    @Test
    public void smeltingRecipe_SingleItem_Matches(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.FURNACE, List.of(new ItemStack(Items.BEEF, 1), ItemStack.EMPTY, ItemStack.EMPTY));

        // Act & Assert
        Assertions.assertTrue(SMELTING_RECIPE.matches(input, null));
        Assertions.assertEquals(1, SMELTING_RECIPE.getSmeltingInputCount(input));
    }

    @Test
    public void smeltingRecipe_ThreeItemsOneSlot_Matches(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.FURNACE, List.of(new ItemStack(Items.BEEF, 3), ItemStack.EMPTY, ItemStack.EMPTY));

        // Act & Assert
        Assertions.assertTrue(SMELTING_RECIPE.matches(input, null));
        Assertions.assertEquals(3, SMELTING_RECIPE.getSmeltingInputCount(input));
    }

    @Test
    public void smeltingRecipe_ThreeItemsThreeSlots_Matches(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.FURNACE,
            List.of(new ItemStack(Items.BEEF, 1), new ItemStack(Items.BEEF, 1), new ItemStack(Items.BEEF, 1)));

        // Act & Assert
        Assertions.assertTrue(SMELTING_RECIPE.matches(input, null));
        Assertions.assertEquals(3, SMELTING_RECIPE.getSmeltingInputCount(input));
    }

    @Test
    public void smeltingRecipe_ManyItemsOneSlot_Matches_ConsumeCountDoesNotExceedThree(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.FURNACE, List.of(new ItemStack(Items.BEEF, 12), ItemStack.EMPTY, ItemStack.EMPTY));

        // Act & Assert
        Assertions.assertTrue(SMELTING_RECIPE.matches(input, null));
        Assertions.assertEquals(3, SMELTING_RECIPE.getSmeltingInputCount(input));
    }

    @Test
    public void smeltingRecipe_ManyItemsThreeSlot_Matches_ConsumeCountDoesNotExceedThree(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.FURNACE,
            List.of(new ItemStack(Items.BEEF, 12), new ItemStack(Items.BEEF, 18), new ItemStack(Items.BEEF, 24)));

        // Act & Assert
        Assertions.assertTrue(SMELTING_RECIPE.matches(input, null));
        Assertions.assertEquals(3, SMELTING_RECIPE.getSmeltingInputCount(input));
    }

    @Test
    public void smeltingRecipe_OneMatchOtherJunk_NoMatch(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.FURNACE,
            List.of(new ItemStack(Items.BEEF, 12), new ItemStack(Items.COBBLED_DEEPSLATE, 18), ItemStack.EMPTY));

        // Act & Assert
        Assertions.assertFalse(SMELTING_RECIPE.matches(input, null));
    }

    // endregion

    // region Alloy Smelting Matches

    private AlloySmeltingRecipe ONE_INPUT = new AlloySmeltingRecipe(List.of(SizedIngredient.of(Items.IRON_INGOT, 2)),
        new ItemStackTemplate(Items.GOLD_INGOT, 1), 100, 0f, false);

    private AlloySmeltingRecipe TWO_INPUT = new AlloySmeltingRecipe(List.of(SizedIngredient.of(Items.IRON_INGOT, 2), SizedIngredient.of(Items.DIAMOND, 1)),
        new ItemStackTemplate(Items.GOLD_INGOT, 1), 100, 0f, false);

    private AlloySmeltingRecipe THREE_INPUT = new AlloySmeltingRecipe(
        List.of(SizedIngredient.of(Items.IRON_INGOT, 2), SizedIngredient.of(Items.DIAMOND, 1), SizedIngredient.of(Items.EMERALD, 1)),
        new ItemStackTemplate(Items.GOLD_INGOT, 1), 100, 0f, false);

    @Test
    public void alloySmeltingRecipe_OneInput_Matches(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.ALLOYS, List.of(new ItemStack(Items.IRON_INGOT, 5), ItemStack.EMPTY, ItemStack.EMPTY));

        // Act & Assert
        Assertions.assertTrue(ONE_INPUT.matches(input, null));
    }

    @Test
    public void alloySmeltingRecipe_TwoInput_Matches(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.ALLOYS,
            List.of(new ItemStack(Items.IRON_INGOT, 5), new ItemStack(Items.DIAMOND, 7), ItemStack.EMPTY));

        // Act & Assert
        Assertions.assertTrue(TWO_INPUT.matches(input, null));
    }

    @Test
    public void alloySmeltingRecipe_ThreeInput_Matches(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.ALLOYS,
            List.of(new ItemStack(Items.IRON_INGOT, 5), new ItemStack(Items.DIAMOND, 7), new ItemStack(Items.EMERALD, 11)));

        // Act & Assert
        Assertions.assertTrue(THREE_INPUT.matches(input, null));
    }

    @Test
    public void alloySmeltingRecipe_OneInputOtherJunk_NoMatch(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.ALLOYS, List.of(new ItemStack(Items.IRON_INGOT, 5), new ItemStack(Items.COAL, 2), ItemStack.EMPTY));

        // Act & Assert
        Assertions.assertFalse(ONE_INPUT.matches(input, null));
    }

    @Test
    public void alloySmeltingRecipe_TwoInputOtherJunk_NoMatch(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.ALLOYS,
            List.of(new ItemStack(Items.IRON_INGOT, 5), new ItemStack(Items.DIAMOND, 7), new ItemStack(Items.BEEF, 6)));

        // Act & Assert
        Assertions.assertFalse(TWO_INPUT.matches(input, null));
    }

    @Test
    public void alloySmeltingRecipe_ThreeInputOneWrong_NoMatch(MinecraftServer server) {
        // Arrange.
        var input = new AlloySmeltingInput(AlloySmelterMode.ALLOYS,
            List.of(new ItemStack(Items.IRON_INGOT, 5), new ItemStack(Items.DIAMOND, 7), new ItemStack(Items.CLAY, 11)));

        // Act & Assert
        Assertions.assertFalse(THREE_INPUT.matches(input, null));
    }

    // endregion
}
