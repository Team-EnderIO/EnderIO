package com.enderio.enderio.foundation.inventory;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.layout.SimpleSlotAccessRules;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.init.EIODataComponents;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.function.UnaryOperator;

public class MachineSlotTemplates {
    public static UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<ItemResource>> capacitor() {
        return builder -> builder
            .guiRules(new SimpleSlotAccessRules<>(true, true))
            .externalRules(new SimpleSlotAccessRules<>(true, false))
            .capacity(1)
            .filter((_, itemResource) -> itemResource.has(EIODataComponents.CAPACITOR_DATA) ||
                itemResource.toStack().getCapability(EnderIOCapabilities.CAPACITOR_EXTENSION) != null);
    }
}
