package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.IConduitScreenData;

public enum SlotType {
    FILTER_EXTRACT,
    FILTER_INSERT,
    UPGRADE_EXTRACT;

    public int getX() {
        return switch (this) {
                case FILTER_EXTRACT -> 113;
                case FILTER_INSERT -> 23;
                case UPGRADE_EXTRACT -> 131;
        };
    }

    public int getY() {
        return 71;
    }

    public boolean isAvailableFor(IConduitScreenData data) {
        return switch (this) {
            case FILTER_INSERT -> data.hasFilterInsert();
            case FILTER_EXTRACT -> data.hasFilterExtract();
            case UPGRADE_EXTRACT -> data.hasUpgrade();
        };
    }
}
