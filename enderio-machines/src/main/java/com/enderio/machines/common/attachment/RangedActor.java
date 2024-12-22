package com.enderio.machines.common.attachment;

import com.enderio.base.api.UseOnly;
import net.neoforged.fml.LogicalSide;

public interface RangedActor {

    int getMaxRange();

    ActionRange getActionRange();

    @UseOnly(LogicalSide.SERVER)
    void setActionRange(ActionRange actionRange);

    default int getRange() {
        return getActionRange().range();
    }

    default boolean isRangeVisible() {
        return getActionRange().isVisible();
    }

    @UseOnly(LogicalSide.SERVER)
    default void setRangeVisible(boolean isRangeVisible) {
        if (isRangeVisible) {
            setActionRange(getActionRange().visible());
        } else {
            setActionRange(getActionRange().invisible());
        }
    }

    @UseOnly(LogicalSide.SERVER)
    default void increaseRange() {
        var actionRange = getActionRange();
        if (actionRange.range() < getMaxRange()) {
            setActionRange(actionRange.increment());
        }
    }

    @UseOnly(LogicalSide.SERVER)
    default void decreaseRange() {
        var actionRange = getActionRange();
        if (actionRange.range() > 0) {
            setActionRange(actionRange.decrement());
        }
    }
}
