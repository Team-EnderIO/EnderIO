package com.enderio.api.capability;

import com.enderio.api.io.IOMode;

public interface ISideConfig {
    IOMode getMode();
    void setMode(IOMode mode);
    void cycleMode();
}
