package com.enderio.core.common.storage.layout;

import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.ResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class ResourceStorageLayout<T extends Resource> {
    private final List<SlotConfig<T>> slots;
    private final Map<ResourceSlotKey<T>, List<Integer>> keyMap;

    protected ResourceStorageLayout(List<SlotConfig<T>> slots, Map<ResourceSlotKey<T>, List<Integer>> keyMap) {
        this.slots = slots;
        this.keyMap = keyMap;
    }

    public final int size() {
        return slots.size();
    }

    public final SlotConfig<T> slotConfig(int index) {
        Objects.checkIndex(index, size());
        return slots.get(index);
    }

    public final SlotConfig<T> slotConfig(ResourceSlotId<T> slotId) {
        return slotConfig(slotId.index(this));
    }

    public final int indexOf(SingleResourceSlotKey<T> key) {
        List<Integer> indices = keyMap.get(key);
        if (indices == null || indices.size() != 1) {
            // This should never happen
            throw new IllegalArgumentException("Key does not map to a single slot: " + key);
        }

        return indices.getFirst();
    }

    public final int indexOf(MultiResourceSlotKey<T> key, int index) {
        List<Integer> indices = keyMap.get(key);
        if (indices == null) {
            throw new IllegalArgumentException("Key does not map to any slots: " + key);
        }

        if (index < 0 || index >= indices.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for key with " + indices.size() + " slots: " + key);
        }

        return indices.get(index);
    }

    public static abstract class Builder<TBuilder extends Builder<? extends TBuilder, T>, T extends Resource> {
        
        protected final ArrayList<SlotConfig<T>> slots = new ArrayList<>();
        protected final Map<ResourceSlotKey<T>, List<Integer>> keyMap = new HashMap<>();

        protected Builder() {
        }

        protected TBuilder self() {
            //noinspection unchecked
            return (TBuilder) this;
        }

        protected SlotBuilder<T> createSlotBuilder() {
            return new SlotBuilder<>();
        }

        public final TBuilder add(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T>> slotBuilder) {
            slots.add(slotBuilder.apply(createSlotBuilder()).build());
            keyMap.put(key, List.of(slots.size() - 1));
            return self();
        }

        // Support use of a 'template' and override
        public final TBuilder add(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T>> template, UnaryOperator<SlotBuilder<T>> slotBuilder) {
            return add(key, b -> slotBuilder.apply(template.apply(b)));
        }

        protected final TBuilder add(MultiResourceSlotKey<T> key, Runnable slotCreator) {
            List<Integer> indices = new ArrayList<>(key.count());
            for (int i = 0; i < key.count(); i++) {
                slotCreator.run();
                indices.add(slots.size() - 1);
            }

            keyMap.put(key, indices);

            return self();
        }

        public final TBuilder add(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T>> slotBuilder) {
            return add(key, () -> slots.add(slotBuilder.apply(createSlotBuilder()).build()));
        }

        // Support use of a 'template' and override
        public final TBuilder add(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T>> template, UnaryOperator<SlotBuilder<T>> slotBuilder) {
            return add(key, b -> slotBuilder.apply(template.apply(b)));
        }

        public static final class SlotBuilder<T extends Resource> {
            private SlotAccessRules<T> externalRules = new SimpleSlotAccessRules<>(true, true);
            private SlotAccessRules<T> guiRules = new SimpleSlotAccessRules<>(true, true);
            @Nullable
            private Function<T, Integer> capacityFunc;
            @Nullable
            private BiPredicate<Integer, T> filter;

            public SlotBuilder<T> externalRules(SlotAccessRules<T> mode) {
                externalRules = mode;
                return this;
            }

            public SlotBuilder<T> guiRules(SlotAccessRules<T> mode) {
                guiRules = mode;
                return this;
            }

            public SlotBuilder<T> capacity(int capacity) {
                this.capacityFunc = _ -> capacity;
                return this;
            }
            
            public SlotBuilder<T> capacity(Supplier<Integer> capacityGetter) {
                this.capacityFunc = _ -> capacityGetter.get();
                return this;
            }

            public SlotBuilder<T> capacity(Function<T, Integer> capacityFunc) {
                this.capacityFunc = capacityFunc;
                return this;
            }

            public SlotBuilder<T> filter(BiPredicate<Integer, T> filter) {
                this.filter = filter;
                return this;
            }

            public SlotConfig<T> build() {
                // We can easily default this on items, but fluid must be explicitly set.
                if (capacityFunc == null) {
                    throw new IllegalStateException("Capacity must be set!");
                }

                return new SlotConfig<>(externalRules, guiRules, capacityFunc, filter);
            }
        }
    }

    public record SlotConfig<T extends Resource>(
        SlotAccessRules<T> externalRules,
        SlotAccessRules<T> guiRules,
        Function<T, Integer> capacityFunc,
        @Nullable BiPredicate<Integer, T> filter) {

        public int getCapacityAsInt(T resource) {
            return capacityFunc.apply(resource);
        }

        public boolean isValid(int index, T resource) {
            if (filter == null) {
                return true;
            }

            return filter.test(index, resource);
        }
    }
}
