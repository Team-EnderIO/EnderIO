package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.IConduitType;

public abstract sealed class RightClickAction permits RightClickAction.Upgrade, RightClickAction.Blocked, RightClickAction.Insert{

    private RightClickAction() { }

    public static final class Upgrade extends RightClickAction {

        private final IConduitType notInConduit;
        public Upgrade(IConduitType notInConduit) {
            this.notInConduit = notInConduit;
        }

        public IConduitType getNotInConduit() {
            return notInConduit;
        }
    }

    public static final class Insert extends RightClickAction {
    }

    public static final class Blocked extends RightClickAction {
    }

    public boolean hasChanged() {
        return !(this instanceof Blocked);
    }
}
