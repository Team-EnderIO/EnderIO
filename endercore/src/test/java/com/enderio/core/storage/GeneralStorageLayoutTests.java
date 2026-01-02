package com.enderio.core.storage;

import com.enderio.core.common.storage.ItemStorageLayout;
import com.enderio.core.common.storage.MultiResourceSlotKey;
import com.enderio.core.common.storage.SingleResourceSlotKey;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeneralStorageLayoutTests {
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
        Assertions.assertThrows(IllegalArgumentException.class, () -> layout.absoluteIndicesOf(unknownKey));
        Assertions.assertThrows(IllegalArgumentException.class, () -> layout.relativeIndicesOf(unknownKey));
    }
}
