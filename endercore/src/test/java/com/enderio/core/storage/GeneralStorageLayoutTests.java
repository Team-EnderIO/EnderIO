package com.enderio.core.storage;

import com.enderio.core.common.storage.EnderItemHandler;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeneralStorageLayoutTests {
    @Test
    public void singleSlotIdWorks() {
        var key = new SingleResourceSlotKey<ItemResource>();

        var layout = ItemStorageLayout.<Void>builder().inputSlot(key).build();
        var storage = new EnderItemHandler<>(layout, null);

        Assertions.assertDoesNotThrow(() -> storage.getAmountAsInt(key));
    }

    @Test
    public void multiSlotIdWorks() {
        var key = new MultiResourceSlotKey<ItemResource>(2);

        var layout = ItemStorageLayout.<Void>builder().inputSlots(key).build();
        var storage = new EnderItemHandler<>(layout, null);

        Assertions.assertDoesNotThrow(() -> storage.getAmountAsInt(key.slot(1)));
    }

    @Test
    public void multiSlotIterator() {
        var key = new MultiResourceSlotKey<ItemResource>(4);

        var layout = ItemStorageLayout.<Void>builder().inputSlots(key).build();
        var storage = new EnderItemHandler<>(layout, null);

        Assertions.assertDoesNotThrow(() -> {
            for (var slotId : key) {
                storage.getAmountAsInt(slotId);
            }
        });
    }

    @Test
    public void ensureUnknownSingleSlotKeyThrows() {
        var key = new SingleResourceSlotKey<ItemResource>();
        var layout = ItemStorageLayout.<Void>builder().inputSlot(key).build();

        var unknownKey = new SingleResourceSlotKey<ItemResource>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> layout.indexOf(unknownKey));
    }

    @Test
    public void ensureUnknownMultiSlotKeyThrows() {
        var key = new MultiResourceSlotKey<ItemResource>(3);
        var layout = ItemStorageLayout.<Void>builder().inputSlots(key).build();

        var unknownKey = new MultiResourceSlotKey<ItemResource>(2);
        Assertions.assertThrows(IllegalArgumentException.class, () -> layout.indexOf(unknownKey, 1));
    }

    // Due to the way slot keys are used as map keys, we need to ensure that different instances are not considered equal
    @Test
    public void ensureReferenceEqualityForSlotKeys() {
        var singleKey1 = new SingleResourceSlotKey<ItemResource>();
        var singleKey2 = new SingleResourceSlotKey<ItemResource>();
        Assertions.assertNotEquals(singleKey1, singleKey2);

        var multiKey1 = new MultiResourceSlotKey<ItemResource>(3);
        var multiKey2 = new MultiResourceSlotKey<ItemResource>(3);
        Assertions.assertNotEquals(multiKey1, multiKey2);
    }
}
