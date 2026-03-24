package com.enderio.enderio.foundation.inventory;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.init.EIODataComponents;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.function.UnaryOperator;

public class MachineSlotTemplates {
    public static UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<ItemResource>> capacitor() {
        var storageSlotBuilder = SlotTemplates.<ItemResource>storage();
        return builder -> storageSlotBuilder.apply(builder)
            .filter((_, itemResource) -> itemResource.has(EIODataComponents.CAPACITOR_DATA) ||
                itemResource.toStack().getCapability(EnderIOCapabilities.CAPACITOR_EXTENSION) != null);
    }
}
