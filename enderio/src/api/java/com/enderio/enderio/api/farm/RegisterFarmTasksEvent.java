package com.enderio.enderio.api.farm;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.Pair;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.List;

public class RegisterFarmTasksEvent extends Event implements IModBusEvent {
    private final List<Pair<FarmTaskType, FarmTask>> tasks = Lists.newArrayList();

    public void register(FarmTaskType type, FarmTask task) {
        tasks.add(Pair.of(type, task));
    }

    public List<Pair<FarmTaskType, FarmTask>> getTasks() {
        return List.copyOf(tasks);
    }
}
