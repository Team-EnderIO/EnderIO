package com.enderio.base.api.soul;

import com.enderio.base.api.attachment.StoredEntityData;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface SingleSoulStorage {
    StoredEntityData getSoul();

    void setSoul(StoredEntityData soul);

    default boolean hasSoul() {
        return getSoul().hasEntity();
    }
}
