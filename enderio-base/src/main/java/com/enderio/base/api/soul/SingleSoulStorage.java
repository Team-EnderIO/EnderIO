package com.enderio.base.api.soul;

import com.enderio.base.api.attachment.StoredEntityData;

public interface SingleSoulStorage {
    StoredEntityData getSoul();

    void setSoul(StoredEntityData soul);

    default boolean hasSoul() {
        return getSoul().hasEntity();
    }
}
