package com.enderio.machines.common.attachment;

public interface IRangedActor {

    int getMaxRange();

    ActionRange getActionRange();

    void setActionRange(ActionRange actionRange);

    default int getRange() {
        return getActionRange().range();
    }

    default boolean isRangeVisible() {
        return getActionRange().isVisible();
    }

    default void setRangeVisible(boolean isRangeVisible) {
        if (isRangeVisible) {
            setActionRange(getActionRange().visible());
        } else {
            setActionRange(getActionRange().invisible());
        }
    }

    default void increaseRange() {
        var actionRange = getActionRange();
        if (actionRange.range() < getMaxRange()) {
            setActionRange(actionRange.increment());
        }
    }

    default void decreaseRange() {
        var actionRange = getActionRange();
        if (actionRange.range() > 0) {
            setActionRange(actionRange.decrement());
        }
    }
}
