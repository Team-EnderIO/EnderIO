package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;

public sealed interface RightClickAction permits RightClickAction.Upgrade, RightClickAction.Blocked, RightClickAction.Insert{
     record Upgrade(ConduitType<?> notInConduit) implements RightClickAction {
        public ConduitType<?> getNotInConduit() {
            return notInConduit;
        }

         @Override
         public String toString() {
             return "Upgrade[" + EnderIORegistries.CONDUIT_TYPES.getKey(notInConduit) + "]";
         }
     }

    final class Insert implements RightClickAction {
        @Override
        public String toString() {
            return "Insert";
        }
    }

    final class Blocked implements RightClickAction {
        @Override
        public String toString() {
            return "Blocked";
        }
    }

    default boolean hasChanged() {
        return !(this instanceof Blocked);
    }
}
