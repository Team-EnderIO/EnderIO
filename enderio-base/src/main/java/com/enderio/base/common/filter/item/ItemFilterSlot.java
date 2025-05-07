package com.enderio.base.common.filter.item;

import com.enderio.base.common.filter.FilterSlot;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;

public class ItemFilterSlot extends FilterSlot<ItemStack> {

    public ItemFilterSlot(Supplier<ItemStack> getter, Consumer<ItemStack> setter, int pSlot, int pX, int pY) {
        super(getter, setter, pSlot, pX, pY);
    }

    @Override
    protected ItemStack emptyResource() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem() {
        return getResource();
    }

    @Override
    public ItemStack processResource(ItemStack resource) {
        // TODO: when we add limited, we'll want to do something with this to allow increasing item stacks etc.
        return resource.copyWithCount(1);
    }

    @Override
    public Optional<ItemStack> getResourceFrom(ItemStack itemStack) {
        return Optional.of(itemStack);
    }
}
