package com.enderio.core.client.gui.screen;

public interface StateRestoringWidget {
    Object getValueForRestore();

    void restoreValue(Object value);
}
