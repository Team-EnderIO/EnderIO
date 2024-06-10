package com.enderio.api.conduit;

public interface ConduitMenuData {

    // TODO: I don't want this to live here.
    ConduitMenuData ITEM = new Simple(true, true, true, true, true, true);

    boolean hasFilterInsert();

    boolean hasFilterExtract();

    boolean hasUpgrade();

    default boolean showBarSeparator() {
        return true;
    }

    default boolean showBothEnable() {
        return true;
    }

    boolean showColorInsert();

    boolean showColorExtract();

    boolean showRedstoneExtract();

    record Simple(boolean hasFilterInsert, boolean hasFilterExtract, boolean hasUpgrade, boolean showColorInsert, boolean showColorExtract,
                  boolean showRedstoneExtract) implements ConduitMenuData {}
}
