package com.enderio.enderio.common.filter.soul;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.common.filter.FilterSlot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SoulFilterSlot extends FilterSlot<Soul> {

    public SoulFilterSlot(Supplier<Soul> getter, Consumer<Soul> setter, int pSlot, int pX, int pY) {
        super(getter, setter, pSlot, pX, pY);
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
