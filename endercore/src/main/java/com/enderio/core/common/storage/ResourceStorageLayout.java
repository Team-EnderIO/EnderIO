package com.enderio.core.common.storage;

import com.google.common.primitives.Ints;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public record ResourceStorageLayout<TResource extends Resource, TContext>(List<SlotConfig<TResource, TContext>> slots) {

    public int size() {
        return slots.size();
    }

    public SlotConfig<TResource, TContext> get(int index) {
        return slots.get(index);
    }

    public static class Builder<T extends Resource, TContext> {
        
        private final ArrayList<SlotConfig<T, TContext>> slots = new ArrayList<>();

        public SingleResourceSlot<T> slot(UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            slots.add(slotBuilder.apply(new SlotBuilder<>()).build());
            return new SingleResourceSlot<>(slots.size() - 1);
        }

        private MultiResourceSlot<T> slots(int count, Supplier<SingleResourceSlot<T>> slotFactory) {
            var slots = new ArrayList<SingleResourceSlot<T>>();
            for (int i = 0; i < count; i++) {
                slots.add(slotFactory.get());
            }

            return new MultiResourceSlot<>(List.copyOf(slots));
        }

        public MultiResourceSlot<T> slots(int count, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(count, () -> slot(slotBuilder));
        }
        
        // region Quick Slot Presets

        public SingleResourceSlot<T> inputSlot() {
            return inputSlot(slot -> slot);
        }

        public SingleResourceSlot<T> inputSlot(UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slot(slot -> slotBuilder.apply(slot.canInsert().canManualInsert().canManualExtract()));
        }

        public MultiResourceSlot<T> inputSlots(int count) {
            return inputSlots(count, slot -> slot);
        }

        public MultiResourceSlot<T> inputSlots(int count, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(count, () -> inputSlot(slotBuilder));
        }

        public SingleResourceSlot<T> outputSlot() {
            return outputSlot(slot -> slot);
        }

        public SingleResourceSlot<T> outputSlot(UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slot(slot -> slotBuilder.apply(slot.canExtract().canManualExtract()));
        }

        public MultiResourceSlot<T> outputSlots(int count) {
            return outputSlots(count, slot -> slot);
        }

        public MultiResourceSlot<T> outputSlots(int count, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(count, () -> outputSlot(slotBuilder));
        }
        
        // endregion

        public ResourceStorageLayout<T, TContext> build() {
            return new ResourceStorageLayout<>(slots);
        }

        public static class SlotBuilder<T extends Resource, TContext> {
            private boolean canInsert;
            private boolean canExtract;
            private boolean canManualInsert;
            private boolean canManualExtract;
            @Nullable
            private BiFunction<T, TContext, Long> capacityFunc;
            @Nullable
            private TriPredicate<Integer, T, TContext> filter;
            
            public SlotBuilder<T, TContext> canInsert() {
                return canInsert(true);
            }
            
            public SlotBuilder<T, TContext> canInsert(boolean canInsert) {
                this.canInsert = canInsert;
                return this;
            }
            
            public SlotBuilder<T, TContext> canExtract() {
                return canExtract(true);
            }
            
            public SlotBuilder<T, TContext> canExtract(boolean canExtract) {
                this.canExtract = canExtract;
                return this;
            }

            public SlotBuilder<T, TContext> canManualInsert() {
                return canManualInsert(true);
            }

            public SlotBuilder<T, TContext> canManualInsert(boolean canManualInsert) {
                this.canManualInsert = canManualInsert;
                return this;
            }

            public SlotBuilder<T, TContext> canManualExtract() {
                return canManualExtract(true);
            }

            public SlotBuilder<T, TContext> canManualExtract(boolean canManualExtract) {
                this.canManualExtract = canManualExtract;
                return this;
            }
            
            public SlotBuilder<T, TContext> capacity(BiFunction<T, TContext, Long> capacityFunc) {
                this.capacityFunc = capacityFunc;
                return this;
            }
            
            public SlotBuilder<T, TContext> filter(TriPredicate<Integer, T, TContext> filter) {
                this.filter = filter;
                return this;
            }

            public SlotConfig<T, TContext> build() {
                return new SlotConfig<>(canInsert, canExtract, canManualInsert, canManualExtract, capacityFunc, filter);
            }
        }
    }

    public record SlotConfig<T extends Resource, TContext>(
        boolean canInsert,
        boolean canExtract,
        boolean canManualInsert,
        boolean canManualExtract,
        @Nullable
        BiFunction<T, TContext, Long> capacityFunc,
        @Nullable
        TriPredicate<Integer, T, TContext> filter) {

        @Nullable
        public Long getCapacityAsLong(T resource, TContext context) {
            if (capacityFunc == null) {
                return null;
            }

            return capacityFunc.apply(resource, context);
        }

        @Nullable
        public Integer getCapacityAsInt(T resource, TContext context) {
            Long capacity = getCapacityAsLong(resource, context);
            if (capacity == null) {
                return null;
            }

            return Ints.saturatedCast(capacity);
        }

        public boolean isValid(T resource, TContext context) {
            if (filter == null) {
                return true;
            }

            return filter.test(resource, context);
        }
    }
}
