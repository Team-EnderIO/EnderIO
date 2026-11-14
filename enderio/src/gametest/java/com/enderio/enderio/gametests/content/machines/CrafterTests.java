package com.enderio.enderio.gametests.content.machines;

import com.enderio.enderio.content.storage.crafter.CrafterBlockEntity;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.List;

@ForEachTest(groups = "content.machines.crafter")
public class CrafterTests {
    @GameTest
    @TestHolder(description = "The crafter's output must not exceed the item's maximum stack size.")
    public static void crafterOutputRespectsMaxStackSize(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 1, 1).set(0, 0, 0, EIOBlocks.CRAFTER.get().defaultBlockState()));

        // Oak signs: 3 per craft, stack to 16.
        // @formatter:off
        List<Item> signs = List.of(
            Items.OAK_PLANKS, Items.OAK_PLANKS, Items.OAK_PLANKS,
            Items.OAK_PLANKS, Items.OAK_PLANKS, Items.OAK_PLANKS,
            Items.AIR, Items.STICK, Items.AIR);
        // @formatter:on

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            var crafter = helper.requireBlockEntity(0, 1, 0, CrafterBlockEntity.class);

            helper.startSequence().thenExecute(() -> {
                helper.insertIntoContainer(0, 1, 0, EIOItems.OCTADIC_CAPACITOR.get(), 1);
                helper.provideEnergy(0, 1, 0, 10000);

                // Set max result already
                CrafterBlockEntity.OUTPUT.setStackInSlot(crafter.getInventory(), new ItemStack(Items.OAK_SIGN, 16));

                // Setup recipe ghost *and* inputs
                setCrafterRecipe(crafter, signs);
            }).thenExecuteAfter(45, () -> {
                // Ensure no more signs were crafted as the slot is already full
                var output = CrafterBlockEntity.OUTPUT.getItemStack(crafter);
                if (output.getCount() > output.getMaxStackSize()) {
                    throw new GameTestAssertException("Output holds " + output.getCount() + " oak signs, which only stack to " + output.getMaxStackSize());
                }
            }).thenSucceed();
        });
    }

    /**
     * Sets the crafter's template and fills its input grid to match.
     */
    private static void setCrafterRecipe(CrafterBlockEntity crafter, List<Item> grid) {
        var inventory = crafter.getInventory();
        for (int i = 0; i < 9; i++) {
            CrafterBlockEntity.GHOST.get(i).setStackInSlot(inventory, new ItemStack(grid.get(i)));
        }
        for (int i = 0; i < 9; i++) {
            CrafterBlockEntity.INPUT.get(i).setStackInSlot(inventory, new ItemStack(grid.get(i)));
        }
    }
}
