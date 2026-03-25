package com.enderio.enderio.gametests.regressions.issues;

import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.enderio.enderio.gametests.util.EnderGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

/**
 * Regression test for issue #1033 - EnderIO XP Juice in Tank Deletes Mending Items.
 * https://github.com/Team-EnderIO/EnderIO/issues/1033
 * Bug: When placing a damaged mending item in a fluid tank with XP Juice, it gets repaired
 * and moved to the output slot. If another mending item is then placed in the input slot
 * while the first item is still in the output slot, the first item gets deleted entirely.
 */
@ForEachTest(groups = "regression.issue1033")
public class Issue1033 {
    
    /**
     * Creates a damaged golden axe with mending enchantment.
     */
    private static ItemStack createDamagedMendingAxe(Holder<Enchantment> mendingEnchantment, int damage) {
        ItemStack axe = new ItemStack(Items.GOLDEN_AXE);
        axe.setDamageValue(damage);
        axe.enchant(mendingEnchantment, 1);
        return axe;
    }
    
    @GameTest
    @TestHolder(description = "Ensures that items in output slot are not deleted when a second item is placed in input")
    public static void testSecondItemDoesNotDeleteFirstItem(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 2, 1)
            .set(0, 0, 0, EIOBlocks.FLUID_TANK.get().defaultBlockState()));

        test.onGameTest(EnderGameTestHelper.class, helper -> {
            var enchantmentRegistry = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            var mendingEnchantment = enchantmentRegistry.getOrThrow(Enchantments.MENDING);
            
            helper.startSequence()
                // Fill tank with XP Juice (1000 mB = 50 XP, enough to repair golden axes)
                .thenExecute(() -> helper.fillContainer(0, 0, 0, EIOFluids.XP_JUICE.source().get(), 1000))
                
                // Create first damaged golden axe with mending enchantment
                .thenExecute(() -> {
                    ItemStack firstAxe = createDamagedMendingAxe(mendingEnchantment, 20);
                    helper.insertItemIntoSlot(0, 0, 0, FluidTankBlockEntity.FLUID_DRAIN_INPUT, firstAxe);
                })
                
                // Wait for repair to complete (tank ticks every 5 ticks, give it 2 seconds)
                .thenExecuteAfter(40, () -> {
                    helper.assertSlotHasItem(0, 0, 0, FluidTankBlockEntity.FLUID_DRAIN_OUTPUT, Items.GOLDEN_AXE);
                })
                
                // Insert second damaged axe while first is still in output
                .thenExecute(() -> {
                    ItemStack secondAxe = createDamagedMendingAxe(mendingEnchantment, 30);
                    helper.insertItemIntoSlot(0, 0, 0, FluidTankBlockEntity.FLUID_DRAIN_INPUT, secondAxe);
                })
                
                // Wait a few more ticks to ensure the tank has processed
                .thenExecuteAfter(20, () -> {
                    ItemStack outputItem = helper.getItemInSlot(0, 0, 0, FluidTankBlockEntity.FLUID_DRAIN_OUTPUT);
                    ItemStack inputItem = helper.getItemInSlot(0, 0, 0, FluidTankBlockEntity.FLUID_DRAIN_INPUT);
                    
                    if (outputItem.isEmpty()) {
                        throw helper.assertionException(
                            "First golden axe was deleted from output slot when second axe was added! " +
                            "Output slot is now empty."
                        );
                    }
                    
                    if (!outputItem.is(Items.GOLDEN_AXE)) {
                        throw helper.assertionException(
                            "Expected first golden axe in output slot, but found: " + outputItem.getItem()
                        );
                    }
                    
                    if (!inputItem.is(Items.GOLDEN_AXE)) {
                        throw helper.assertionException(
                            "Expected second golden axe to remain in input slot, but found: " + 
                            (inputItem.isEmpty() ? "empty" : inputItem.getItem())
                        );
                    }
                })
                .thenSucceed();
        });
    }
}
