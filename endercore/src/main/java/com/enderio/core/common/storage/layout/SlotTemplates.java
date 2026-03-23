package com.enderio.core.common.storage.layout;

import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.function.UnaryOperator;

public class SlotTemplates {
    public static <T extends Resource, TContext> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T, TContext>> storage() {
        return builder -> builder.canInsert().canExtract().canManualInsert().canManualExtract();
    }

    public static <T extends Resource, TContext> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T, TContext>> input() {
        return builder -> builder.canInsert().canManualInsert().canManualExtract();
    }

    public static <T extends Resource, TContext> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T, TContext>> output() {
        return builder -> builder.canExtract().canManualExtract();
    }
}
