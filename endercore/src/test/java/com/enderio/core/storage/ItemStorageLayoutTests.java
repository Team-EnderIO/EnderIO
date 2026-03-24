package com.enderio.core.storage;

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
public class ItemStorageLayoutTests {
    @Test
    public void ensureUseDefaultItemCapacityOnSingleSlot(MinecraftServer server) {
        var key = new SingleResourceSlotKey<ItemResource>();
        var layout = Assertions.assertDoesNotThrow(() -> ItemStorageLayout.<Void>builder().slot(key, SlotTemplates.input()).build());

        Assertions.assertEquals(64, layout.slotConfig(0).getCapacityAsInt(ItemResource.of(Items.STONE)));
        Assertions.assertEquals(1, layout.slotConfig(0).getCapacityAsInt(ItemResource.of(Items.IRON_SWORD)));
    }

    @Test
    public void ensureUseDefaultItemCapacityOnMultipleSlots(MinecraftServer server) {
        var key = new MultiResourceSlotKey<ItemResource>(3);
        var layout = Assertions.assertDoesNotThrow(() -> ItemStorageLayout.<Void>builder().slots(key, SlotTemplates.input()).build());

        for (int i = 0; i < layout.size(); i++) {
            Assertions.assertEquals(64, layout.slotConfig(i).getCapacityAsInt(ItemResource.of(Items.STONE)));
            Assertions.assertEquals(1, layout.slotConfig(i).getCapacityAsInt(ItemResource.of(Items.IRON_SWORD)));
        }
    }
}
