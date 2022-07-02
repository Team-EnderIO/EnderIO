package com.enderio.api.integration;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class IntegrationManager {

    private static final List<Integration> ALL_INTEGRATIONS = new ArrayList<>();

    private static <T extends Integration> IntegrationWrapper<T> wrapper(String modid, Supplier<T> integration) {
        return new IntegrationWrapper<>(modid, integration);
    }

    public static void addIntegration(Integration integration) {
        ALL_INTEGRATIONS.add(integration);
    }

    public static boolean noneMatch(Predicate<Integration> predicate) {
        return ALL_INTEGRATIONS.stream().noneMatch(predicate);
    }

    public static boolean allMatch(Predicate<Integration> predicate) {
        return ALL_INTEGRATIONS.stream().allMatch(predicate);
    }

    public static boolean anyMatch(Predicate<Integration> predicate) {
        return ALL_INTEGRATIONS.stream().anyMatch(predicate);
    }

    public static void forAll(Consumer<Integration> consumer) {
        ALL_INTEGRATIONS.forEach(consumer);
    }
}
