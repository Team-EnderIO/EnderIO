package com.enderio.api.capability;

import com.enderio.base.common.blockentity.IOConfig;

public interface ISideConfig {
    IOConfig.State getState();
    void setState(IOConfig.State state);
}
