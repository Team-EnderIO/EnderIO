package com.enderio.core.common.storage.layout;

import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.ResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jspecify.annotations.Nullable;

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

    public final SlotConfig<TResource, TContext> slotConfig(int index) {
        Objects.checkIndex(index, size());
        return slots.get(index);
    }

    public final SlotConfig<TResource, TContext> slotConfig(ResourceSlotId<TResource> slotId) {
        return slotConfig(slotId.index(this));
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

    public static abstract class Builder<TBuilder extends Builder<? extends TBuilder, T, TContext>, T extends Resource, TContext> {
        
        protected final ArrayList<SlotConfig<T, TContext>> slots = new ArrayList<>();
        protected final Map<ResourceSlotKey, List<Integer>> keyMap = new HashMap<>();

        protected Builder() {
        }

        protected TBuilder self() {
            //noinspection unchecked
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

        // Support use of a 'template' and override
        public final TBuilder slot(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> template, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slot(key, b -> slotBuilder.apply(template.apply(b)));
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

        // Support use of a 'template' and override
        public final TBuilder slots(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> template, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(key, b -> slotBuilder.apply(template.apply(b)));
        }

        public static final class SlotBuilder<T extends Resource, TContext> {
            private boolean canInsert;
            private boolean canExtract;
            private boolean canManualInsert;
            private boolean canManualExtract;
            @Nullable
            private BiFunction<T, TContext, Integer> capacityFunc;
            @Nullable
            private TriPredicate<TContext, Integer, T> filter;
            
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

            public SlotBuilder<T, TContext> filter(TriPredicate<TContext, Integer, T> filter) {
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
        TriPredicate<TContext, Integer, T> filter) {

        public int getCapacityAsInt(T resource, TContext context) {
            return capacityFunc.apply(resource, context);
        }

        public boolean isValid(TContext context, int index, T resource) {
            if (filter == null) {
                return true;
            }

            return filter.test(context, index, resource);
        }
    }
}
