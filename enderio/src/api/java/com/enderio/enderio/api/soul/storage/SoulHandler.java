package com.enderio.enderio.api.soul.storage;

import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.api.soul.Soul;
import org.jetbrains.annotations.ApiStatus;

/**
 * Intended for items or blocks which support storage of many souls.
 * Can also be implemented by the soul vial for storage interop.
 * @see SoulBindable for items or blocks that can be bound to a single soul.
 */
@ApiStatus.Experimental
@ApiStatus.AvailableSince("8.0")
public interface SoulHandler {
    int getSlots();

    Soul getSoulInSlot(int slot);

    boolean tryInsertSoul(Soul soul, boolean isSimulate);

    Soul tryExtractSoul(boolean isSimulate);

    boolean isSoulValid(int slot, Soul soul);
}
