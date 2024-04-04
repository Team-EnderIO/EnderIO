package com.enderio.conduits.common.integrations.refinedstorage;

import com.enderio.api.conduit.IConduitMenuData;

public class RSMenuData implements IConduitMenuData {

    public static final RSMenuData INSTANCE = new RSMenuData();

    @Override
    public boolean hasFilterInsert() {
        return false;
    }

    @Override
    public boolean hasFilterExtract() {
        return false;
    }

    @Override
    public boolean hasUpgrade() {
        return false;
    }

    @Override
    public boolean showBarSeperator() {
        return false;
    }

    @Override
    public boolean showBothEnable() {
        return false;
    }

    @Override
    public boolean showColorInsert() {
        return false;
    }

    @Override
    public boolean showColorExtract() {
        return false;
    }

    @Override
    public boolean showRedstoneExtract() {
        return false;
    }


}
