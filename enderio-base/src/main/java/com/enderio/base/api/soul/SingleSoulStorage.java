package com.enderio.base.api.soul;

import com.enderio.base.api.attachment.Soul;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface SingleSoulStorage {
    Soul getSoul();

    void setSoul(Soul soul);

    default boolean hasSoul() {
        return getSoul().hasEntity();
    }
}
