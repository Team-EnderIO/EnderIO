package com.enderio.base.common.item.capacitors;

import com.enderio.api.capacitor.ICapacitorData;
import com.enderio.base.common.capacitor.LootCapacitorData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class LootCapacitorItem extends BaseCapacitorItem {

    public static final ICapabilityProvider<ItemStack, Void, ICapacitorData> CAPACITOR_DATA_PROVIDER
        = (stack, ctx) -> new LootCapacitorData(stack);

    public LootCapacitorItem(Properties pProperties) {
        super(pProperties);
    }
}
