package com.enderio.enderio.client.content.filters.item;

import com.enderio.core.client.gui.tooltip.AbstractClientItemListTooltip;
import com.enderio.enderio.content.filters.item.general.EnderItemFilter;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientEnderItemFilterTooltip extends AbstractClientItemListTooltip {
    private final EnderItemFilter filter;

    public ClientEnderItemFilterTooltip(EnderItemFilter filter) {
        this.filter = filter;
    }

    @Override
    protected List<ItemStack> itemStacksToDisplay() {
        return this.filter.matches().stream().filter(stack -> !stack.isEmpty()).toList();
    }
}
