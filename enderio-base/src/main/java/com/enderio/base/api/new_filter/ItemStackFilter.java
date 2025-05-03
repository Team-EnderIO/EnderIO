package com.enderio.base.api.new_filter;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface ItemStackFilter {
    ItemStack test(@Nullable IItemHandler target, ItemStack stack);
}
