package com.enderio.base.common.item.filter;

import com.enderio.base.common.init.EIOAttachments;
import com.enderio.core.common.capability.ItemFilterCapability;
import com.enderio.core.common.item.IEnderFilter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class ItemFilter extends Item implements IEnderFilter {

    public static ICapabilityProvider<ItemStack, Void, ItemFilterCapability> FILTER_PROVIDER =
        (stack, v) -> stack.getData(EIOAttachments.ITEM_FILTER);

    private final int size;
    private final boolean advanced;
    private final boolean invert;

    public ItemFilter(Properties pProperties, int size, boolean advanced, boolean inverted) {
        super(pProperties);
        this.size = size;
        this.advanced = advanced;
        this.invert = inverted;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isAdvanced() {
        return advanced;
    }

    @Override
    public boolean isInverted() {
        return invert;
    }
}
