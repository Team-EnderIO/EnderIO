package com.enderio.enderio.foundation.task.new_host;

import com.enderio.enderio.foundation.task.MachineTask;
import com.enderio.enderio.foundation.task.MachineTaskContext;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.function.Supplier;

public class MachineTaskHost<T extends MachineTask, C extends MachineTaskContext> implements ValueIOSerializable {

    @Override
    public void serialize(ValueOutput output) {

    }

    @Override
    public void deserialize(ValueInput input) {

    }
}
