package com.enderio.enderio.client.content.filters.soul;

import com.enderio.core.client.gui.tooltip.AbstractClientItemListTooltip;
import com.enderio.enderio.content.filters.soul.EnderSoulFilter;
import com.enderio.enderio.foundation.soul.SoulUtility;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientEnderSoulFilterTooltip extends AbstractClientItemListTooltip {

    private final EnderSoulFilter filter;

    public ClientEnderSoulFilterTooltip(EnderSoulFilter filter) {
        this.filter = filter;
    }

    @Override
    protected List<ItemStack> itemStacksToDisplay() {
        return this.filter.matches().stream()
            .filter(soul -> !soul.isEmpty())
            .map(SoulUtility::getStackForDisplay)
            .toList();
    }
}
