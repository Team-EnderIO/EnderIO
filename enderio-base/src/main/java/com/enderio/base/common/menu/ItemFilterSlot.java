package com.enderio.base.common.menu;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;

public class ItemFilterSlot extends FilterSlot<ItemStack> {

    private final Supplier<ItemStack> item;

    public ItemFilterSlot(Supplier<ItemStack> item, Consumer<ItemStack> consumer, int pSlot, int pX, int pY) {
        super(consumer, pSlot, pX, pY);
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return item.get();
    }

    @Override
    public ItemStack processResource(ItemStack resource) {
        return resource.copyWithCount(1);
    }

    @Override
    public Optional<ItemStack> getResourceFrom(ItemStack itemStack) {
        return Optional.of(itemStack);
    }
}
