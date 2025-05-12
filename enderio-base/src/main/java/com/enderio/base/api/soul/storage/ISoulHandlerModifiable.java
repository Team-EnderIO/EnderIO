package com.enderio.base.api.soul.storage;

import com.enderio.base.api.soul.Soul;

public interface ISoulHandlerModifiable extends ISoulHandler {
    void setSoulInSlot(int slot, Soul soul);
}
