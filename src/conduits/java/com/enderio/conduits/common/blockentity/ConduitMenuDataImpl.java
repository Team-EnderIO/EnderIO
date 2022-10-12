package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.IConduitMenuData;

public record ConduitMenuDataImpl(boolean hasFilterInsert, boolean hasFilterExtract, boolean hasUpgrade, boolean showColorInsert, boolean showColorExtract, boolean showRedstoneExtract) implements
    IConduitMenuData {
}
