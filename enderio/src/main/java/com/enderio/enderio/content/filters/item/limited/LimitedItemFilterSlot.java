package com.enderio.enderio.content.filters.item.limited;

import com.enderio.enderio.content.filters.FilterSlot;
import com.enderio.enderio.content.filters.item.ItemFilterSlot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A filter slot for the limited item filter. Unlike {@link com.enderio.enderio.content.filters.item.ItemFilterSlot},
 * this slot preserves the item stack's count, which encodes the desired stock level (the "limit").
 */
public class LimitedItemFilterSlot extends ItemFilterSlot {

    public LimitedItemFilterSlot(Supplier<ItemStack> getter, Consumer<ItemStack> setter, int slot, int x, int y) {
        super(getter, setter, slot, x, y);
    }

    @Override
    public ItemStack processResource(ItemStack resource) {
        return resource.copy();
    }
}
