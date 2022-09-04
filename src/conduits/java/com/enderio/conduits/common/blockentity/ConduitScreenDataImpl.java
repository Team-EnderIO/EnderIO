package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.IConduitScreenData;

public record ConduitScreenDataImpl(boolean hasFilterInsert, boolean hasFilterExtract, boolean hasUpgrade, boolean showColorInsert, boolean showColorExtract, boolean showRedstoneExtract) implements IConduitScreenData {
}
