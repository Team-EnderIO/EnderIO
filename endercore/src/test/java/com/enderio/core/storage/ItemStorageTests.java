package com.enderio.core.storage;

import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class ItemStorageTests {
    @Test
    public void ensureUseDefaultItemCapacityOnSingleSlot(MinecraftServer server) {
        var slot = new SingleResourceSlotKey<ItemResource>();
        var layout = Assertions.assertDoesNotThrow(() -> ItemStorageLayout.<Void>builder().add(slot, SlotTemplates.input(64)).build());
        var storage = new ItemStorage(layout);

        Assertions.assertEquals(64, storage.getCapacityAsInt(slot, ItemResource.of(Items.STONE)));
        Assertions.assertEquals(1, storage.getCapacityAsInt(slot, ItemResource.of(Items.IRON_SWORD)));
    }

    @Test
    public void ensureUseDefaultItemCapacityOnMultipleSlots(MinecraftServer server) {
        var slots = new MultiResourceSlotKey<ItemResource>(3);
        var layout = Assertions.assertDoesNotThrow(() -> ItemStorageLayout.<Void>builder().add(slots, SlotTemplates.input(64)).build());
        var storage = new ItemStorage(layout);

        for (var slot : slots) {
            Assertions.assertEquals(64, storage.getCapacityAsInt(slot, ItemResource.of(Items.STONE)));
            Assertions.assertEquals(1, storage.getCapacityAsInt(slot, ItemResource.of(Items.IRON_SWORD)));
        }
    }
}
