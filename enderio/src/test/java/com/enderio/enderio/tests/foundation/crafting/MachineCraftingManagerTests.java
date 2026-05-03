package com.enderio.enderio.tests.foundation.crafting;

import com.enderio.enderio.foundation.crafting.MachineCraftingContext;
import com.enderio.enderio.foundation.crafting.MachineCraftingManager;
import com.enderio.enderio.foundation.crafting.MachineCraftingStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(EphemeralTestServerProvider.class)
@ExtendWith(MockitoExtension.class)
public class MachineCraftingManagerTests {
    @Mock
    private ServerLevel level;

    @Mock
    private MachineCraftingContext<SmeltingRecipe, SingleRecipeInput> context;

    @BeforeEach
    public void setup(MinecraftServer server) {
        // Setup level mock
        lenient().when(level.recipeAccess()).thenReturn(server.getRecipeManager());

        // Ensure that context.level() always returns our mocked level.
        lenient().when(context.level()).thenReturn(level);
    }

    @AfterEach
    public void teardown() {
        // Ensure there are no useless mocks
        validateMockitoUsage();
    }

    @Test
    public void gracefullyHandleNullLevel(MinecraftServer server) {
        // Arrange.
        when(context.level()).thenReturn(null);
        var manager = new MachineCraftingManager<>(RecipeType.SMELTING, context);

        // Act.
        manager.tick();

        // Assert - ensures no exceptions and single fetch of the level
        verify(context, times(1)).level();
    }

    @Test
    public void completeCraftTest(MinecraftServer server) {
        // Arrange.
        ArgumentMatcher<SmeltingRecipe> isValidRecipe = recipe -> recipe.result().item().value() == Items.COOKED_BEEF;

        when(context.recipeInput()).thenReturn(new SingleRecipeInput(new ItemStack(Items.BEEF)));

        when(context.tryProgressCraft(argThat(isValidRecipe))).thenReturn(true);
        when(context.getCraftingTicks(any())).thenReturn(1);

        when(context.consumeRecipeInputs(argThat(isValidRecipe), any())).thenReturn(true);
        when(context.insertRecipeOutputs(argThat(isValidRecipe), any(), any())).thenReturn(true);

        var manager = new MachineCraftingManager<>(RecipeType.SMELTING, context);

        // Act.
        manager.tick();

        // Assert. - ensure that the manager processed the recipe and called all the methods configured above.
        verify(level, Mockito.atLeastOnce()).recipeAccess();
        verify(context).consumeRecipeInputs(argThat(isValidRecipe), any());
        verify(context).insertRecipeOutputs(argThat(isValidRecipe), any(), any());
    }

    @Test
    public void activeRecipe_ReturnsCorrectStatus(MinecraftServer server) {
        // Arrange.
        when(context.recipeInput()).thenReturn(new SingleRecipeInput(new ItemStack(Items.BEEF)));

        when(context.tryProgressCraft(any())).thenReturn(true);
        when(context.getCraftingTicks(any())).thenReturn(5);

        var manager = new MachineCraftingManager<>(RecipeType.SMELTING, context);

        // Act.
        manager.tick();

        // Assert.
        verify(context).tryProgressCraft(any());
        Assertions.assertEquals(1 / 5f, manager.craftingProgress());
        Assertions.assertEquals(MachineCraftingStatus.ACTIVE, manager.status());
        Assertions.assertEquals(Items.COOKED_BEEF, manager.currentRecipe().value().result().item().value());
    }

    @Test
    public void inputRemovedMidCraft_CraftCancelsCorrectly(MinecraftServer server) {
        // Arrange.
        when(context.recipeInput()).thenReturn(new SingleRecipeInput(new ItemStack(Items.BEEF)));

        when(context.tryProgressCraft(any())).thenReturn(true);
        when(context.getCraftingTicks(any())).thenReturn(5);

        var manager = new MachineCraftingManager<>(RecipeType.SMELTING, context);

        // Act.
        manager.tick();
        when(context.recipeInput()).thenReturn(new SingleRecipeInput(new ItemStack(Items.AIR)));
        manager.tick();

        // Assert - ensure the machine only progressed once (first tick) and shows as idle again.
        verify(context, times(1)).tryProgressCraft(any());

        Assertions.assertEquals(MachineCraftingStatus.IDLE, manager.status());
        Assertions.assertNull(manager.currentRecipe());
    }

    @Test
    public void onlyCheckForRecipeOncePerInputChange(MinecraftServer server) {
        // Arrange.
        var beefInput = new SingleRecipeInput(new ItemStack(Items.BEEF));
        var emptyInput = new SingleRecipeInput(ItemStack.EMPTY);

        when(context.recipeInput()).thenReturn(emptyInput);

        // Mock recipe manager so we can check that getRecipeFor is only called once.
        var fakeRecipeManager = mock(RecipeManager.class);
        when(fakeRecipeManager.getRecipeFor(any(), any(), any())).thenReturn(Optional.empty());
        when(level.recipeAccess()).thenReturn(fakeRecipeManager);

        var manager = new MachineCraftingManager<>(RecipeType.SMELTING, context);

        // Act.
        for (int i = 0; i < 5; i++) {
            manager.tick();
        }

        when(context.recipeInput()).thenReturn(beefInput);
        for (int i = 0; i < 5; i++) {
            manager.tick();
        }

        // Assert.
        verify(fakeRecipeManager, times(1)).getRecipeFor(eq(RecipeType.SMELTING), eq(emptyInput), eq(level));
        verify(fakeRecipeManager, times(1)).getRecipeFor(eq(RecipeType.SMELTING), eq(beefInput), eq(level));
    }

    @Test
    public void recheckRecipeIfRecipeManagerChanges(MinecraftServer server) {
        // Arrange.
        var input = new SingleRecipeInput(new ItemStack(Items.BEEF));
        var steakRecipe = server.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level).get();

        // Setup recipe managers
        var realRecipeManager = spy(server.getRecipeManager());
        when(level.recipeAccess()).thenReturn(realRecipeManager);

        var fakeRecipeManager = mock(RecipeManager.class);
        when(fakeRecipeManager.getRecipeFor(eq(RecipeType.SMELTING), eq(input), eq(level), eq(steakRecipe.id()))).thenReturn(Optional.of(steakRecipe));

        when(context.recipeInput()).thenReturn(input);

        when(context.tryProgressCraft(any())).thenReturn(true);
        when(context.getCraftingTicks(any())).thenReturn(5);

        var manager = new MachineCraftingManager<>(RecipeType.SMELTING, context);

        // Act.
        manager.tick();
        when(level.recipeAccess()).thenReturn(fakeRecipeManager);
        manager.tick();

        // Assert.
        Assertions.assertEquals(steakRecipe, manager.currentRecipe());

        verify(realRecipeManager, times(1)).getRecipeFor(eq(RecipeType.SMELTING), eq(input), eq(level));
        verify(fakeRecipeManager, times(1)).getRecipeFor(eq(RecipeType.SMELTING), eq(input), eq(level), eq(steakRecipe.id()));

        // Ensure no progress was lost as the recipe is the same.
        Assertions.assertEquals(2 / 5f, manager.craftingProgress());
    }
}
