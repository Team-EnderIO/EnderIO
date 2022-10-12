package com.enderio.api.conduit;


public interface IConduitMenuData {

    //region gui control
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
