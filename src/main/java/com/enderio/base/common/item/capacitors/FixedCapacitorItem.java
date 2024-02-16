package com.enderio.base.common.item.capacitors;

import com.enderio.api.capacitor.ICapacitorData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

/**
 * A capacitor item that has fixed capacitor data attached.
 */
public class FixedCapacitorItem extends BaseCapacitorItem {

    public static final ICapabilityProvider<ItemStack, Void, ICapacitorData> CAPACITOR_DATA_PROVIDER
        = (stack, ctx) -> ((FixedCapacitorItem)stack.getItem()).data;

    private final ICapacitorData data;

    public FixedCapacitorItem(ICapacitorData data, Properties properties) {
        super(properties);
        this.data = data;
    }
}
