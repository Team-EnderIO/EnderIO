package com.enderio.base.api.farm;

import com.enderio.base.api.integration.IntegrationManager;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class FarmTaskManager {
    private final ArrayList<TaskEntry> tasks = new ArrayList<>();
    private static ImmutableList<FarmTask> TASKS_IMMUTABLE;

    public static final int PLANT = 1;
    public static final int BONEMEAL = 3;
    public static final int HARVEST = 5;

    private FarmTaskManager() {

    }

    public static List<FarmTask> getTasks() {
        if (TASKS_IMMUTABLE == null) {
            FarmTaskManager manager = new FarmTaskManager();
            IntegrationManager.forAll(integration -> integration.registerFarmTasks(manager));
            manager.tasks.sort(TaskEntry::compareTo);
            TASKS_IMMUTABLE = ImmutableList.copyOf(manager.tasks.stream().map(e -> e.task).toList());
        }
        return TASKS_IMMUTABLE;
    }

    public void addTask(int priority, FarmTask task) {
        tasks.add(new TaskEntry(priority, task));
    }

    public record TaskEntry(int priority, FarmTask task) implements Comparable<TaskEntry> {

        @Override
        public int compareTo(@NotNull FarmTaskManager.TaskEntry o) {
            return priority - o.priority;
        }
    }
}
