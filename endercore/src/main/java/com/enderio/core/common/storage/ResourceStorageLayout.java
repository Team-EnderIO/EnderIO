package com.enderio.core.common.storage;

import com.google.common.primitives.Ints;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class ResourceStorageLayout<TResource extends Resource, TContext> {

    private final List<SlotConfig<TResource, TContext>> slots;
    private final Map<ResourceSlotKey, List<Integer>> keyMap;

    private ResourceStorageLayout(List<SlotConfig<TResource, TContext>> slots, Map<ResourceSlotKey, List<Integer>> keyMap) {
        this.slots = slots;
        this.keyMap = keyMap;
    }

    public static <T extends Resource, TContext> Builder<T, TContext> builder() {
        return new Builder<>();
    }

    public int size() {
        return slots.size();
    }

    // region Single Slot Access

    public TResource getResource(ResourceHandler<TResource> handler, SingleResourceSlotKey<TResource> key) {
        return handler.getResource(getSingleIndex(key));
    }

    public int getAmountAsInt(ResourceHandler<TResource> handler, SingleResourceSlotKey<TResource> key) {
        return handler.getAmountAsInt(getSingleIndex(key));
    }

    public long getAmountAsLong(ResourceHandler<TResource> handler, SingleResourceSlotKey<TResource> key) {
        return handler.getAmountAsLong(getSingleIndex(key));
    }

    public int getCapacityAsInt(ResourceHandler<TResource> handler, SingleResourceSlotKey<TResource> key, TResource resource) {
        return handler.getCapacityAsInt(getSingleIndex(key), resource);
    }

    public long getCapacityAsLong(ResourceHandler<TResource> handler, SingleResourceSlotKey<TResource> key, TResource resource) {
        return handler.getCapacityAsLong(getSingleIndex(key), resource);
    }

    public void setWithInt(ResourceStorage<TResource> storage, SingleResourceSlotKey<TResource> key, TResource resource, int amount) {
        storage.set(getSingleIndex(key), resource, amount);
    }

    public void setWithLong(ResourceStorage<TResource> storage, SingleResourceSlotKey<TResource> key, TResource resource, long amount) {
        storage.set(getSingleIndex(key), resource, amount);
    }

    public int insert(ResourceHandler<TResource> handler, SingleResourceSlotKey<TResource> key, TResource resource, int amount, TransactionContext transaction) {
        return handler.insert(getSingleIndex(key), resource, amount, transaction);
    }

    public int extract(ResourceHandler<TResource> handler, SingleResourceSlotKey<TResource> key, TResource resource, int amount, TransactionContext transaction) {
        return handler.extract(getSingleIndex(key), resource, amount, transaction);
    }

    private int getSingleIndex(SingleResourceSlotKey<TResource> key) {
        List<Integer> indices = keyMap.get(key);
        if (indices == null || indices.size() != 1) {
            // This should never happen
            throw new IllegalArgumentException("Key does not map to a single slot: " + key);
        }

        return indices.getFirst();
    }

    // endregion

    // region Multi-Slot Access

    public TResource getResource(ResourceHandler<TResource> handler, MultiResourceSlotKey<TResource> key, int index) {
        return handler.getResource(getMultiIndex(key, index));
    }

    public int getAmountAsInt(ResourceHandler<TResource> handler, MultiResourceSlotKey<TResource> key, int index) {
        return handler.getAmountAsInt(getMultiIndex(key, index));
    }

    public long getAmountAsLong(ResourceHandler<TResource> handler, MultiResourceSlotKey<TResource> key, int index) {
        return handler.getAmountAsLong(getMultiIndex(key, index));
    }

    public int getCapacityAsInt(ResourceHandler<TResource> handler, MultiResourceSlotKey<TResource> key, int index, TResource resource) {
        return handler.getCapacityAsInt(getMultiIndex(key, index), resource);
    }

    public long getCapacityAsLong(ResourceHandler<TResource> handler, MultiResourceSlotKey<TResource> key, int index, TResource resource) {
        return handler.getCapacityAsLong(getMultiIndex(key, index), resource);
    }

    public void setWithInt(ResourceStorage<TResource> storage, MultiResourceSlotKey<TResource> key, int index, TResource resource, int amount) {
        storage.set(getMultiIndex(key, index), resource, amount);
    }

    public void setWithLong(ResourceStorage<TResource> storage, MultiResourceSlotKey<TResource> key, int index, TResource resource, long amount) {
        storage.set(getMultiIndex(key, index), resource, amount);
    }

    private int getMultiIndex(MultiResourceSlotKey<TResource> key, int index) {
        List<Integer> indices = keyMap.get(key);
        if (indices == null) {
            throw new IllegalArgumentException("Key does not map to any slots: " + key);
        }

        if (index < 0 || index >= indices.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for key with " + indices.size() + " slots: " + key);
        }

        return indices.get(index);
    }

    // endregion

    public SlotConfig<TResource, TContext> get(int index) {
        return slots.get(index);
    }

    public static class Builder<T extends Resource, TContext> {
        
        private final ArrayList<SlotConfig<T, TContext>> slots = new ArrayList<>();
        private final Map<ResourceSlotKey, List<Integer>> keyMap = new HashMap<>();

        private Builder() {
        }

        public Builder<T, TContext> slot(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            slots.add(slotBuilder.apply(new SlotBuilder<>()).build());
            keyMap.put(key, List.of(slots.size() - 1));
            return this;
        }

        private Builder<T, TContext> slots(MultiResourceSlotKey<T> key, Runnable slotCreator) {
            List<Integer> indices = new ArrayList<>(key.count());
            for (int i = 0; i < key.count(); i++) {
                slotCreator.run();
                indices.add(slots.size() - 1);
            }

            keyMap.put(key, indices);

            return this;
        }

        public Builder<T, TContext> slots(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(key, () -> slots.add(slotBuilder.apply(new SlotBuilder<>()).build()));
        }
        
        // region Quick Slot Presets

        public Builder<T, TContext> inputSlot(SingleResourceSlotKey<T> key) {
            return inputSlot(key, slot -> slot);
        }

        public Builder<T, TContext> inputSlot(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slot(key, this::setupInputSlot);
        }

        public Builder<T, TContext> inputSlots(MultiResourceSlotKey<T> key) {
            return inputSlots(key, slot -> slot);
        }

        public Builder<T, TContext> inputSlots(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(key, slot -> slotBuilder.apply(setupInputSlot(slot)));
        }

        private SlotBuilder<T, TContext> setupInputSlot(SlotBuilder<T, TContext> slotBuilder) {
            return slotBuilder.canInsert().canManualInsert().canManualExtract();
        }

        public Builder<T, TContext> outputSlot(SingleResourceSlotKey<T> key) {
            return outputSlot(key, slot -> slot);
        }

        public Builder<T, TContext> outputSlot(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slot(key, slot -> slotBuilder.apply(setupOutputSlot(slot)));
        }

        public Builder<T, TContext> outputSlots(MultiResourceSlotKey<T> key) {
            return outputSlots(key, slot -> slot);
        }

        public Builder<T, TContext> outputSlots(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(key, slot -> slotBuilder.apply(setupOutputSlot(slot)));
        }

        private SlotBuilder<T, TContext> setupOutputSlot(SlotBuilder<T, TContext> slotBuilder) {
            return slotBuilder.canExtract().canManualExtract();
        }
        
        // endregion

        public ResourceStorageLayout<T, TContext> build() {
            return new ResourceStorageLayout<>(slots, keyMap);
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

        public boolean isValid(int index, T resource, TContext context) {
            if (filter == null) {
                return true;
            }

            return filter.test(index, resource, context);
        }
    }
}
