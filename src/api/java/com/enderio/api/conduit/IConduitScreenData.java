package com.enderio.api.conduit;


public interface IConduitScreenData {

    boolean hasFilterInsert();
    boolean hasFilterExtract();
    boolean hasUpgrade();
    default boolean showBarSeperator() {
        return true;
    }

    default boolean showBothEnable() {
        return true;
    }

    boolean showColorInsert();
    boolean showColorExtract();
    boolean showRedstoneExtract();
}
