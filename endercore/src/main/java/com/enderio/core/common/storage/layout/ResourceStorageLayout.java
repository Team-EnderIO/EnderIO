package com.enderio.core.common.storage.layout;

import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.ResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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
            return storageSlot(key, slot -> slot);
        }

        public TBuilder storageSlot(SingleResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slot(key, slot -> slotBuilder.apply(setupStorageSlot(slot)));
        }

        public TBuilder storageSlots(MultiResourceSlotKey<T> key) {
            return storageSlots(key, slot -> slot);
        }

        public TBuilder storageSlots(MultiResourceSlotKey<T> key, UnaryOperator<SlotBuilder<T, TContext>> slotBuilder) {
            return slots(key, slot -> slotBuilder.apply(setupStorageSlot(slot)));
        }

        private SlotBuilder<T, TContext> setupStorageSlot(SlotBuilder<T, TContext> slotBuilder) {
            return slotBuilder.canInsertExternal().canExtractExternal().canPlayerInsert().canPlayerExtract();
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
            return slotBuilder.canInsertExternal().canPlayerInsert().canPlayerExtract();
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
            return slotBuilder.canExtractExternal().canPlayerExtract();
        }
        
        // endregion

        public static final class SlotBuilder<T extends Resource, TContext> {
            private boolean canInsertExternal;
            private boolean canExtractExternal;
            private boolean canPlayerInsert;
            private boolean canPlayerExtract;
            @Nullable
            private BiFunction<T, TContext, Integer> capacityFunc;
            @Nullable
            private TriPredicate<Integer, T, TContext> filter;
            
            public SlotBuilder<T, TContext> canInsertExternal() {
                return canInsertExternal(true);
            }
            
            public SlotBuilder<T, TContext> canInsertExternal(boolean canInsertExternal) {
                this.canInsertExternal = canInsertExternal;
                return this;
            }
            
            public SlotBuilder<T, TContext> canExtractExternal() {
                return canExtractExternal(true);
            }
            
            public SlotBuilder<T, TContext> canExtractExternal(boolean canExtractExternal) {
                this.canExtractExternal = canExtractExternal;
                return this;
            }

            public SlotBuilder<T, TContext> canPlayerInsert() {
                return canPlayerInsert(true);
            }

            public SlotBuilder<T, TContext> canPlayerInsert(boolean canPlayerInsert) {
                this.canPlayerInsert = canPlayerInsert;
                return this;
            }

            public SlotBuilder<T, TContext> canPlayerExtract() {
                return canPlayerExtract(true);
            }

            public SlotBuilder<T, TContext> canPlayerExtract(boolean canPlayerExtract) {
                this.canPlayerExtract = canPlayerExtract;
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

                return new SlotConfig<>(canInsertExternal, canExtractExternal, canPlayerInsert, canPlayerExtract, capacityFunc, filter);
            }
        }
    }
    
    public record SlotConfig<T extends Resource, TContext>(
        boolean canInsertExternal,
        boolean canExtractExternal,
        boolean canPlayerInsert,
        boolean canPlayerExtract,
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
