package com.enderio.enderio.api.capacitor;

import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A capacitor item can implement this capability to change how it behaves dynamically.
 */
@ApiStatus.AvailableSince("8.1.0")
@ApiStatus.Experimental
public interface ICapacitorExtension {
    /**
     * Called whenever a task completes an operation.
     * Only called on the server.
     * @param capacitorStack the capacitor item stack
     * @param level the server level
     */
    void onMachineUsed(ItemStack capacitorStack, ServerLevel level);

    /**
     * Get the capacitor data stored in the given item.
     * This enables dynamic capacitor data that is dependent on other item factors (for example enchantments).
     * @param capacitorStack the capacitor item stack
     * @return the capacitor data, or null if none is present
     */
    @Nullable
    default CapacitorData getCapacitorData(ItemStack capacitorStack, HolderLookup.Provider registries) {
        return capacitorStack.get(EIODataComponents.CAPACITOR_DATA);
    }
}
