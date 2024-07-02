package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.Conduit;
import net.minecraft.core.Holder;

public sealed interface RightClickAction permits RightClickAction.Upgrade, RightClickAction.Blocked, RightClickAction.Insert{
     record Upgrade(Holder<Conduit<?, ?, ?>> replacedConduit) implements RightClickAction {
        @Override
        public String toString() {
             return "Upgrade[" + replacedConduit.getRegisteredName() + "]";
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
