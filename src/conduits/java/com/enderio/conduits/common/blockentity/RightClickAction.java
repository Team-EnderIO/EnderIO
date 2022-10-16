package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.IConduitType;

public sealed interface RightClickAction permits RightClickAction.Upgrade, RightClickAction.Blocked, RightClickAction.Insert{
     record Upgrade(IConduitType<?> notInConduit) implements RightClickAction {
        public IConduitType<?> getNotInConduit() {
            return notInConduit;
        }

         @Override
         public String toString() {
             return "Upgrade[" + "";
         }
     }

    final class Insert implements RightClickAction {
    }

    final class Blocked implements RightClickAction {
    }

    default boolean hasChanged() {
        return !(this instanceof Blocked);
    }
}
