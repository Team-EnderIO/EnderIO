package com.enderio.base.common.filter.soul;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.filter.FilterSlot;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;

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
        var soulBindable = itemStack.getCapability(EIOCapabilities.SoulBindable.ITEM);
        if (soulBindable != null) {
            return Optional.of(soulBindable.getBoundSoul());
        }

        return Optional.empty();
    }
}
