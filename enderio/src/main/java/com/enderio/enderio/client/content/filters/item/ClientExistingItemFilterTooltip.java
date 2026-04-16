package com.enderio.enderio.client.content.filters.item;

import com.enderio.core.client.gui.tooltip.AbstractClientItemListTooltip;
import com.enderio.enderio.content.filters.item.existing.ExistingItemFilter;
import com.enderio.enderio.content.filters.item.general.EnderItemFilter;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientExistingItemFilterTooltip extends AbstractClientItemListTooltip {
    private final ExistingItemFilter filter;

    public ClientExistingItemFilterTooltip(ExistingItemFilter filter) {
        this.filter = filter;
    }

    @Override
    protected List<ItemStack> itemStacksToDisplay() {
        return this.filter.snapshot().stream().filter(stack -> !stack.isEmpty()).toList();
    }
}
