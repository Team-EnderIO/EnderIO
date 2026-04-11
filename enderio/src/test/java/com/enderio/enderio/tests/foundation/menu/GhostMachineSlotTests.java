package com.enderio.enderio.tests.foundation.menu;

import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.foundation.menu.GhostMachineSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class GhostMachineSlotTests {

    private static final SingleResourceSlotKey<ItemResource> GHOST_SLOT = new SingleResourceSlotKey<>();

    private static final ItemStorageLayout GHOST_LAYOUT = ItemStorageLayout.builder()
        .add(GHOST_SLOT, SlotTemplates.ghost())
        .build();;

    @Test
    void testConstructorValidation(MinecraftServer server) {
        // Valid ghost slot layout
        ItemStorage validInventory = new ItemStorage(GHOST_LAYOUT);
        Assertions.assertDoesNotThrow(() -> new GhostMachineSlot(validInventory, 0, 0, 0));

        // Invalid: can insert externally
        ItemStorageLayout invalidInsertLayout = ItemStorageLayout.builder()
            .add(GHOST_SLOT, SlotTemplates.input())
            .build();
        ItemStorage invalidInsertInventory = new ItemStorage(invalidInsertLayout);
        Assertions.assertThrows(RuntimeException.class, () -> new GhostMachineSlot(invalidInsertInventory, 0, 0, 0));

        // Invalid: can extract externally
        ItemStorageLayout invalidExtractLayout = ItemStorageLayout.builder()
            .add(GHOST_SLOT, SlotTemplates.output())
            .build();
        ItemStorage invalidExtractInventory = new ItemStorage(invalidExtractLayout);
        Assertions.assertThrows(RuntimeException.class, () -> new GhostMachineSlot(invalidExtractInventory, 0, 0, 0));
    }

    @Test
    void testSafeInsert_SetsSlot_DoesNotConsumeItem(MinecraftServer server) {
        ItemStorage inventory = new ItemStorage(GHOST_LAYOUT);
        GhostMachineSlot ghostSlot = new GhostMachineSlot(inventory, 0, 0, 0);

        ItemStack stack = new ItemStack(Items.EGG, 64);
        ItemStack result = ghostSlot.safeInsert(stack);

        // Ensure original stack is NOT modified (count remains 64)
        Assertions.assertEquals(64, stack.getCount(), "Original stack count should not change");
        Assertions.assertSame(stack, result, "safeInsert should return the original stack");

        // Ensure ghost slot has a copy of the item but is limited by getMaxStackSize
        Assertions.assertEquals(Items.EGG, ghostSlot.getItem().getItem());
        Assertions.assertEquals(Math.min(stack.getCount(), ghostSlot.getMaxStackSize()), ghostSlot.getItem().getCount());
    }

    @Test
    void testRemove_ClearsSlot_ReturnsEmpty(MinecraftServer server) {
        ItemStorage inventory = new ItemStorage(GHOST_LAYOUT);
        GhostMachineSlot ghostSlot = new GhostMachineSlot(inventory, 0, 0, 0);

        ghostSlot.set(new ItemStack(Items.DIAMOND));
        Assertions.assertFalse(ghostSlot.getItem().isEmpty());

        // Attempt to remove
        ItemStack extracted = ghostSlot.remove(1);

        // Ensure extracted is empty
        Assertions.assertTrue(extracted.isEmpty(), "Extracted stack from ghost slot should be empty");

        // Ensure slot is cleared
        Assertions.assertTrue(ghostSlot.getItem().isEmpty(), "Ghost slot should be empty after removal attempt");
    }

    @Test
    void testTryRemove_ClearsSlot_ReturnsEmpty(MinecraftServer server) {
        ItemStorage inventory = new ItemStorage(GHOST_LAYOUT);
        GhostMachineSlot ghostSlot = new GhostMachineSlot(inventory, 0, 0, 0);

        ghostSlot.set(new ItemStack(Items.DIAMOND));
        Assertions.assertFalse(ghostSlot.getItem().isEmpty());

        // tryRemove should return empty Optional and clear the slot
        var result = ghostSlot.tryRemove(1, 1, null);
        Assertions.assertTrue(result.isEmpty(), "tryRemove should return an empty Optional for ghost slots");
        Assertions.assertTrue(ghostSlot.getItem().isEmpty(), "Ghost slot should be empty after tryRemove");
    }
}
