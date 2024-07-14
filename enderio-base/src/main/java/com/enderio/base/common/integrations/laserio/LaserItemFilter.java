package com.enderio.base.common.integrations.laserio;

import com.direwolf20.laserio.common.containers.customhandler.FilterCountHandler;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.setup.LaserIODataComponents;
import com.enderio.base.api.filter.ItemStackFilter;
import com.enderio.base.common.capability.IFilterCapability;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LaserItemFilter implements IFilterCapability<ItemStack>, ItemStackFilter {

    private final ItemStack container;

    public LaserItemFilter(ItemStack cardItem) {
        this.container = BaseCard.getFilter(cardItem);
    }

    @Override
    public void setNbt(Boolean nbt) {
        if (!nbt) {
            container.remove(LaserIODataComponents.FILTER_COMPARE);
        } else {
            container.set(LaserIODataComponents.FILTER_COMPARE, nbt);
        }
    }

    @Override
    public boolean isNbt() {
        return container.getOrDefault(LaserIODataComponents.FILTER_COMPARE, false);
    }

    @Override
    public void setInverted(Boolean inverted) {
        if (!inverted) {
            container.remove(LaserIODataComponents.FILTER_ALLOW);
        } else {
            container.set(LaserIODataComponents.FILTER_ALLOW, false);
        }
    }

    @Override
    public boolean isInvert() {
        return !container.getOrDefault(LaserIODataComponents.FILTER_ALLOW, true);
    }

    @Override
    public List<ItemStack> getEntries() {
        FilterCountHandler handler = new FilterCountHandler(15, container);
        List<ItemStack> list = NonNullList.withSize(15, ItemStack.EMPTY);
        for(int i = 0; i < 15; ++i) {
            ItemStack itemStack = handler.getStackInSlot(i);
            handler.setStackInSlot(i, itemStack);
            list.set(i, itemStack);
        }
        return list;
    }

    @Override
    public void setEntry(int index, ItemStack entry) {
        //Not needed for working filters, however could be good for in gui changes
    }

    @Override
    public boolean test(ItemStack stack) {
        for (ItemStack testStack : getEntries()) {
            boolean test = isNbt() ? ItemStack.isSameItemSameComponents(testStack, stack) : ItemStack.isSameItem(testStack, stack);
            if (test) {
                return !isInvert();
            }
        }
        return isInvert();
    }
}
