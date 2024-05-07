package com.enderio.api.conduit;

public interface ConduitMenuData {

    ConduitMenuData REDSTONE = new Simple(true, true, false, true, true, false);
    ConduitMenuData ITEM = new Simple(true, true, true, true, true, true);
    ConduitMenuData ENERGY = new Simple(false, false, false, false, false, true);

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

    record Simple(boolean hasFilterInsert, boolean hasFilterExtract, boolean hasUpgrade, boolean showColorInsert, boolean showColorExtract,
                  boolean showRedstoneExtract) implements ConduitMenuData {}
}
