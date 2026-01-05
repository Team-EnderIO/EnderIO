package com.enderio.enderio.content.filters.soul;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.filters.FilterSlot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SoulFilterSlot extends FilterSlot<Soul> {

    public SoulFilterSlot(Supplier<Soul> getter, Consumer<Soul> setter, int slot, int x, int y) {
        super(getter, setter, slot, x, y);
    }

    @Override
    protected Soul emptyResource() {
        return Soul.EMPTY;
    }

    @Override
    public Optional<Soul> getResourceFrom(ItemStack itemStack) {
        var soulBindable = itemStack.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable != null) {
            return Optional.of(soulBindable.getBoundSoul());
        }

        return Optional.empty();
    }
}
