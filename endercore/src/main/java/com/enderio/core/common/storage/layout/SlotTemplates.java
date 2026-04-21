package com.enderio.core.common.storage.layout;

import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.function.UnaryOperator;

public class SlotTemplates {
    public static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> storage() {
        return builder -> builder
            .externalRules(new SimpleSlotAccessRules<>(true, true))
            .guiRules(new SimpleSlotAccessRules<>(true, true));
    }

    public static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> input() {
        return builder -> builder
            .externalRules(new SimpleSlotAccessRules<>(true, false))
            .guiRules(new SimpleSlotAccessRules<>(true, true));
    }

    public static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> output() {
        return builder -> builder
            .externalRules(new SimpleSlotAccessRules<>(false, true))
            .guiRules(new SimpleSlotAccessRules<>(false, true));
    }

    public static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> ghost() {
        return builder -> builder
            .guiRules(new SimpleSlotAccessRules<>(true, false));
    }

    public static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> inaccessible() {
        return builder -> builder;
    }
}
