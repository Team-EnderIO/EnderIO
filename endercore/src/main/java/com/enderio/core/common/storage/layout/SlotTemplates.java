package com.enderio.core.common.storage.layout;

import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.function.UnaryOperator;

public class SlotTemplates {
    public static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> storage(int capacity) {
        return builder -> builder
            .externalRules(new SimpleSlotAccessRules<>(true, true))
            .guiRules(new SimpleSlotAccessRules<>(true, true))
            .capacity(capacity);
    }

    public static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> input(int capacity) {
        return builder -> builder
            .externalRules(new SimpleSlotAccessRules<>(true, false))
            .guiRules(new SimpleSlotAccessRules<>(true, true))
            .capacity(capacity);
    }

    public static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> output(int capacity) {
        return builder -> builder
            .externalRules(new SimpleSlotAccessRules<>(false, true))
            .guiRules(new SimpleSlotAccessRules<>(false, true))
            .capacity(capacity);
    }

    public static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> ghost(int capacity) {
        return builder -> builder
            .guiRules(new SimpleSlotAccessRules<>(true, false))
            .externalRules(new SimpleSlotAccessRules<>(false, false))
            .capacity(capacity);
    }

    public static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> inaccessible(int capacity) {
        return builder -> builder
            .guiRules(new SimpleSlotAccessRules<>(false, false))
            .externalRules(new SimpleSlotAccessRules<>(false, false))
            .capacity(capacity);
    }
}
