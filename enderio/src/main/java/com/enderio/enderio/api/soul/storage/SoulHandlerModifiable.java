package com.enderio.enderio.api.soul.storage;

import com.enderio.enderio.api.soul.Soul;

public interface SoulHandlerModifiable extends SoulHandler {
    void setSoulInSlot(int slot, Soul soul);
}
