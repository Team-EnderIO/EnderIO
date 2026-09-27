package com.enderio.enderio.gametests.machines;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

@ForEachTest(groups = "machines.soul_binder")
public class SoulBinderTests {

    @GameTest
    @TestHolder(description = "Tests that the Soul Binder can process a basic soul binding recipe (Enticing Crystal).")
    public static void testSoulBinderRecipe(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1)
            .set(0, 0, 0, EIOBlocks.SOUL_BINDER.get().defaultBlockState()));

        int energyToAdd = 10000;
        test.onGameTest(EnderGameTestHelper.class, helper -> helper.startSequence()
            .thenExecute(() -> {
                // Insert the capacitor and fill with energy.
                helper.insertIntoContainer(0, 0, 0, EIOItems.OCTADIC_CAPACITOR.get(), 1);
                helper.provideEnergy(0, 0, 0, energyToAdd);
            })
            // Insert recipe ingredients - filled soul vial and cobblestone (test recipe uses cobblestone instead of emerald)
            .thenExecute(() -> {
                var soulVial = SoulVialItem.forSoul(Soul.of(EntityTypes.VILLAGER));
                helper.insertIntoContainer(0, 0, 0, soulVial);
                helper.insertIntoContainer(0, 0, 0, Items.COBBLESTONE, 1);
                helper.fillContainer(0, 0, 0, EIOFluids.XP_JUICE.source().get(), 10000);
            })
            // Wait for the recipe to process (test recipe has energy cost of 100)
            .thenExecuteAfter(20, () -> {
                // Verify inputs have been extracted
                helper.assertContainerHasExactly(0, 0, 0, Items.COBBLESTONE, 0);

                // Verify that we have 1 Enticing Crystal in the output
                helper.assertContainerHasExactly(0, 0, 0, EIOItems.ENTICING_CRYSTAL.get(), 1);

                // Should also return an empty soul vial
                helper.assertContainerHasExactly(0, 0, 0, EIOItems.SOUL_VIAL.get(), 1);

                var input = new SoulBindingRecipe.Input(
                    SoulVialItem.forSoul(Soul.of(EntityTypes.VILLAGER)),
                    new ItemStack(Items.COBBLESTONE, 1),
                    new FluidStack(EIOFluids.XP_JUICE.source(), 10000)
                );

                var recipe = helper.getLevel().recipeAccess().getRecipeFor(EIORecipeTypes.SOUL_BINDING.get(), input, helper.getLevel()).orElseThrow();
                int expectedEnergy = energyToAdd - recipe.value().energy();
                helper.assertEnergyStored(0, 0, 0, expectedEnergy);
            })
            .thenSucceed());
    }
}

