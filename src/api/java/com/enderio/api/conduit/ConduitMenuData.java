package com.enderio.api.conduit;

public interface ConduitMenuData {
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
