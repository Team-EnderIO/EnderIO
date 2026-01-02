package com.enderio.core.storage;

import com.enderio.core.common.storage.ItemStorageLayout;
import com.enderio.core.common.storage.MultiResourceSlotKey;
import com.enderio.core.common.storage.SingleResourceSlotKey;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ItemStorageLayoutTests {
    @Test
    public void ensureUseDefaultItemCapacityOnSingleSlot() {
        var key = new SingleResourceSlotKey<ItemResource>();
        var layout = Assertions.assertDoesNotThrow(() -> ItemStorageLayout.<Void>builder().inputSlot(key).build());

        Assertions.assertEquals(64, layout.get(0).getCapacityAsInt(ItemResource.of(Items.STONE), null));
        Assertions.assertEquals(1, layout.get(0).getCapacityAsInt(ItemResource.of(Items.IRON_SWORD), null));
    }

    @Test
    public void ensureUseDefaultItemCapacityOnMultipleSlots() {
        var key = new MultiResourceSlotKey<ItemResource>(3);
        var layout = Assertions.assertDoesNotThrow(() -> ItemStorageLayout.<Void>builder().inputSlots(key).build());

        for (int i = 0; i < layout.size(); i++) {
            Assertions.assertEquals(64, layout.get(i).getCapacityAsInt(ItemResource.of(Items.STONE), null));
            Assertions.assertEquals(1, layout.get(i).getCapacityAsInt(ItemResource.of(Items.IRON_SWORD), null));
        }
    }
}
