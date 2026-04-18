package com.enderio.core.common.storage;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class ItemStorage extends StacksResourceStorage<ItemResource, ItemStack> implements ValueIOSerializable {
    public ItemStorage(ResourceStorageLayout<ItemResource> layout) {
        super(layout, ItemStack.EMPTY, ItemStack.OPTIONAL_CODEC);
    }

    @Override
    protected ItemResource getResourceFrom(ItemStack stack) {
        return ItemResource.of(stack);
    }

    @Override
    protected int getAmountFrom(ItemStack stack) {
        return stack.getCount();
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        int slotCapacity = super.getCapacity(index, resource);
        return resource.isEmpty() ? slotCapacity : Math.min(resource.getMaxStackSize(), slotCapacity);
    }

    @Override
    protected ItemStack getStackFrom(ItemResource resource, int amount) {
        return resource.toStack(amount);
    }

    @Override
    protected ItemStack copyOf(ItemStack stack) {
        return stack.copy();
    }

    @Override
    protected boolean matches(ItemStack stack, ItemResource resource) {
        return resource.matches(stack);
    }

    public void copyFromItem(ItemContainerContents contents) {
        contents.copyInto(this.stacks);
    }

    public ItemContainerContents toItemContents() {
        return ItemContainerContents.fromItems(this.stacks);
    }
}
