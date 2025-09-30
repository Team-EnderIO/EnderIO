package com.enderio.enderio.api.soul.storage;

import com.enderio.enderio.api.soul.Soul;

public interface ISoulHandlerModifiable extends ISoulHandler {
    void setSoulInSlot(int slot, Soul soul);
}
