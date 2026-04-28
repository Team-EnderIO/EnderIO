package com.enderio.enderio.foundation.task;

import net.neoforged.neoforge.common.util.ValueIOSerializable;

public abstract class MachineTask<C extends MachineTaskContext> implements ValueIOSerializable {

    protected final C context;

    protected MachineTask(C context) {
        this.context = context;
    }

    public abstract void tick();

    public abstract float getProgress();

    public abstract boolean isCompleted();
}
