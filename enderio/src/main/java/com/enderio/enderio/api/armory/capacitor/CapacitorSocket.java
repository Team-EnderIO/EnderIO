package com.enderio.enderio.api.armory.capacitor;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface CapacitorSocket {
    ItemStack getCapacitor();

    void setCapacitor(ItemStack capacitor);

    @ApiStatus.NonExtendable
    default CapacitorData getCapacitorData() {
        ItemStack capacitor = getCapacitor();
        if (capacitor.isEmpty()) {
            return null;
        }

        var capacitorExtension = capacitor.getCapability(EnderIOCapabilities.CAPACITOR_EXTENSION);
        if (capacitorExtension != null) {
            var data = capacitorExtension.getCapacitorData(capacitor, null);
            if (data != null) {
                return data;
            }
        }

        return capacitor.getOrDefault(EIODataComponents.CAPACITOR_DATA, CapacitorData.NONE);
    }
}
