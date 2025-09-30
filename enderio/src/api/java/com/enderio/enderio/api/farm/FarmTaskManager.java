package com.enderio.enderio.api.farm;

import com.enderio.enderio.api.conduits.model.RegisterConduitModelModifiersEvent;
import com.enderio.enderio.api.integration.IntegrationManager;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import net.neoforged.fml.ModLoader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FarmTaskManager {
    private static List<FarmTask> TASKS_IMMUTABLE;

    public static final int PLANT = 1;
    public static final int BONEMEAL = 3;
    public static final int HARVEST = 5;

    public static List<FarmTask> getTasks() {
        if (TASKS_IMMUTABLE == null) {
            var event = new RegisterFarmTasksEvent();
            ModLoader.postEvent(event);

            TASKS_IMMUTABLE = event.getTasks().stream()
                .sorted(Comparator.comparingInt(pair -> pair.first().priority()))
                .map(Pair::second)
                .toList();
        }

        return TASKS_IMMUTABLE;
    }
}
