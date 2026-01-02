package com.enderio.core.common.storage;

import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public abstract class ResourceStorageLayout<TResource extends Resource, TContext> {
    private final List<SlotConfig<TResource, TContext>> slots;
    private final Map<ResourceSlotKey, List<Integer>> keyMap;

    protected ResourceStorageLayout(List<SlotConfig<TResource, TContext>> slots, Map<ResourceSlotKey, List<Integer>> keyMap) {
        this.slots = slots;
        this.keyMap = keyMap;
    }

    public final int size() {
        return slots.size();
    }

    public final SlotConfig<TResource, TContext> get(int index) {
        Objects.checkIndex(index, size());
        return slots.get(index);
    }

    public final int indexOf(SingleResourceSlotKey<TResource> key) {
        List<Integer> indices = keyMap.get(key);
        if (indices == null || indices.size() != 1) {
            // This should never happen
            throw new IllegalArgumentException("Key does not map to a single slot: " + key);
        }

        return indices.getFirst();
    }

    public final int indexOf(MultiResourceSlotKey<TResource> key, int index) {
        List<Integer> indices = keyMap.get(key);
        if (indices == null) {
            throw new IllegalArgumentException("Key does not map to any slots: " + key);
        }

        if (index < 0 || index >= indices.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for key with " + indices.size() + " slots: " + key);
        }

        return indices.get(index);
    }

    public final Iterator<Integer> relativeIndicesOf(MultiResourceSlotKey<TResource> key) {
        List<Integer> indices = keyMap.get(key);
        if (indices == null) {
            throw new IllegalArgumentException("Key does not map to any slots: " + key);
        }

        return IntStream.range(0, indices.size()).iterator();
    }

    public final Iterator<Integer> absoluteIndicesOf(MultiResourceSlotKey<TResource> key) {
        List<Integer> indices = keyMap.get(key);
        if (indices == null) {
            throw new IllegalArgumentException("Key does not map to any slots: " + key);
        }

        return indices.iterator();
    }

    public static abstract class Builder<TBuilder extends Builder<? extends TBuilder, T, TContext>, T extends Resource, TContext> {
        
        protected final ArrayList<SlotConfig<T, TContext>> slots = new ArrayList<>();
        protected final Map<ResourceSlotKey, List<Integer>> keyMap = new HashMap<>();

        protected Builder() {
        }

        protected TBuilder self() {
            return (TBuilder) this;
        }

        protected SlotBuilder<T, TContext> createSlotBuilder() {
            return new SlotBuilder<>();
        }

        public final TBuilder slot(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            slots.add(slotBuilder.apply(createSlotBuilder()).build());
            keyMap.put(key, List.of(slots.size() - 1));
            return self();
        }

        protected final TBuilder slots(MultiResourceSlotKey<T> key, Runnable slotCreator) {
            List<Integer> indices = new ArrayList<>(key.count());
            for (int i = 0; i < key.count(); i++) {
                slotCreator.run();
                indices.add(slots.size() - 1);
            }

            keyMap.put(key, indices);

            return self();
        }

        public final TBuilder slots(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(key, () -> slots.add(slotBuilder.apply(createSlotBuilder()).build()));
        }
        
        // region Quick Slot Presets

        public TBuilder storageSlot(SingleResourceSlotKey<T> key) {
            return inputSlot(key, slot -> slot);
        }

        public TBuilder storageSlot(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slot(key, slot -> slotBuilder.apply(setupStorageSlot(slot)));
        }

        public TBuilder storageSlots(MultiResourceSlotKey<T> key) {
            return inputSlots(key, slot -> slot);
        }

        public TBuilder storageSlots(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(key, slot -> slotBuilder.apply(setupStorageSlot(slot)));
        }

        private SlotBuilder<T, TContext> setupStorageSlot(SlotBuilder<T, TContext> slotBuilder) {
            return slotBuilder.canInsert().canExtract().canManualInsert().canManualExtract();
        }

        public TBuilder inputSlot(SingleResourceSlotKey<T> key) {
            return inputSlot(key, slot -> slot);
        }

        public TBuilder inputSlot(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slot(key, slot -> slotBuilder.apply(setupInputSlot(slot)));
        }

        public TBuilder inputSlots(MultiResourceSlotKey<T> key) {
            return inputSlots(key, slot -> slot);
        }

        public TBuilder inputSlots(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(key, slot -> slotBuilder.apply(setupInputSlot(slot)));
        }

        private SlotBuilder<T, TContext> setupInputSlot(SlotBuilder<T, TContext> slotBuilder) {
            return slotBuilder.canInsert().canManualInsert().canManualExtract();
        }

        public TBuilder outputSlot(SingleResourceSlotKey<T> key) {
            return outputSlot(key, slot -> slot);
        }

        public TBuilder outputSlot(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slot(key, slot -> slotBuilder.apply(setupOutputSlot(slot)));
        }

        public TBuilder outputSlots(MultiResourceSlotKey<T> key) {
            return outputSlots(key, slot -> slot);
        }

        public TBuilder outputSlots(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(key, slot -> slotBuilder.apply(setupOutputSlot(slot)));
        }

        private SlotBuilder<T, TContext> setupOutputSlot(SlotBuilder<T, TContext> slotBuilder) {
            return slotBuilder.canExtract().canManualExtract();
        }
        
        // endregion

        public static final class SlotBuilder<T extends Resource, TContext> {
            private boolean canInsert;
            private boolean canExtract;
            private boolean canManualInsert;
            private boolean canManualExtract;
            @Nullable
            private BiFunction<T, TContext, Integer> capacityFunc;
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

            public SlotBuilder<T, TContext> capacity(int capacity) {
                this.capacityFunc = (resource, context) -> capacity;
                return this;
            }
            
            public SlotBuilder<T, TContext> capacity(Function<T, Integer> capacityFunc) {
                this.capacityFunc = (resource, context) -> capacityFunc.apply(resource);
                return this;
            }

            public SlotBuilder<T, TContext> capacity(BiFunction<T, TContext, Integer> capacityFunc) {
                this.capacityFunc = capacityFunc;
                return this;
            }

            public SlotBuilder<T, TContext> filter(TriPredicate<Integer, T, TContext> filter) {
                this.filter = filter;
                return this;
            }

            public SlotConfig<T, TContext> build() {
                // We can easily default this on items, but fluid must be explicitly set.
                if (capacityFunc == null) {
                    throw new IllegalStateException("Capacity must be set!");
                }

                return new SlotConfig<>(canInsert, canExtract, canManualInsert, canManualExtract, capacityFunc, filter);
            }
        }
    }

    public record SlotConfig<T extends Resource, TContext>(
        boolean canInsert,
        boolean canExtract,
        boolean canManualInsert,
        boolean canManualExtract,
        BiFunction<T, TContext, Integer> capacityFunc,
        @Nullable
        TriPredicate<Integer, T, TContext> filter) {

        public int getCapacityAsInt(T resource, TContext context) {
            return capacityFunc.apply(resource, context);
        }

        public boolean isValid(int index, T resource, TContext context) {
            if (filter == null) {
                return true;
            }

            return filter.test(index, resource, context);
        }
    }
}
