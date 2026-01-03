package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.content.machines.vat.FermentingRecipe;
import com.enderio.enderio.content.machines.vat.VatBlockEntity;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIORecipes;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

/**
 * Test for Issue #1196 - Infinite Fluids from the VAT
 * https://github.com/Team-EnderIO/EnderIO/issues/1196
 */
@ForEachTest(groups = "regression.issue1196")
public class Issue1196 {
    @GameTest
    @TestHolder(description = "Ensures that the VAT doesn't produce infinite outputs.")
    public static void testIssue1196(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 2)
            .set(0, 0, 0, EIOBlocks.VAT.get().defaultBlockState())
            .set(0, 0, 1, EIOBlocks.FLUID_TANK.get().defaultBlockState()));

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            helper.startSequence()
                // Setup Fluid Tank to pull the VAT output
                .thenExecute(() -> helper.changeIoConfig(0, 0, 1, ioConfigurable -> {
                    ioConfigurable.setIOMode(Direction.NORTH, IOMode.PULL);
                }))
                // Insert recipe for Cloud Seed (using test recipe with dirt and cobblestone).
                .thenExecute(() -> {
                    // Set VAT output tank to full
                    var vat = helper.getBlockEntity(0, 0, 0, VatBlockEntity.class);
                    vat.getFluidStorage().set(VatBlockEntity.OUTPUT_TANK, FluidResource.of(EIOFluids.CLOUD_SEED.source().get()), VatBlockEntity.TANK_CAPACITY);

                    helper.fillContainer(0, 0, 0, Fluids.WATER, VatBlockEntity.TANK_CAPACITY);
                    helper.insertIntoContainer(0, 0, 0, Items.DIRT, 1);
                    helper.insertIntoContainer(0, 0, 0, Items.COBBLESTONE, 1);
                })
                // Make sure that after 5 seconds we have exactly 1 bucket of cloud seed, and no more.
                .thenExecuteAfter(20 * 5, () -> {
                    long storedInVat = helper.getAmountInHandler(0, 0, 0, EIOFluids.CLOUD_SEED.source().get());
                    long storedInTank = helper.getAmountInHandler(0, 0, 1, EIOFluids.CLOUD_SEED.source().get());

                    // Determine how much Cloud Seed is created per craft
                    var recipeInput = new FermentingRecipe.Input(new ItemStack(Items.DIRT, 1), new ItemStack(Items.COBBLESTONE, 1), new FluidStack(Fluids.WATER, 1000));
                    var recipe = helper.getLevel().recipeAccess().getRecipeFor(EIORecipes.VAT_FERMENTING.type().get(), recipeInput, helper.getLevel()).orElseThrow();
                    var outputs = recipe.value().craft(recipeInput, helper.getLevel().registryAccess());
                    int amountCreated = outputs.getFirst().getFluid().getAmount();

                    // Ensure no more fluid appeared out of thin air.
                    if (storedInVat + storedInTank > VatBlockEntity.TANK_CAPACITY + amountCreated) {
                        throw helper.assertionException("Too much fluid output by Vat! Total: " + (storedInVat + storedInTank));
                    }
                })
                .thenSucceed();
        });
    }
}
