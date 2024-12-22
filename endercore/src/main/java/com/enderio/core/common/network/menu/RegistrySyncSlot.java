package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RegistrySyncSlot<T> implements SyncSlot {

    public static <T> RegistrySyncSlot<T> standalone(Registry<T> registry) {
        return new RegistrySyncSlot<>(registry) {
            @Nullable
            private T value;

            @Override
            @Nullable
            public T get() {
                return value;
            }

            @Override
            public void set(@Nullable T value) {
                this.value = value;
            }
        };
    }

    public static <T> RegistrySyncSlot<T> simple(Registry<T> registry, Supplier<T> getter, Consumer<T> setter) {
        return new RegistrySyncSlot<>(registry) {

            @Override
            @Nullable
            public T get() {
                return getter.get();
            }

            @Override
            public void set(@Nullable T value) {
                setter.accept(value);
            }
        };
    }

    public static <T> RegistrySyncSlot<T> readOnly(Registry<T> registry, Supplier<T> getter) {
        return new RegistrySyncSlot<>(registry) {

            @Override
            @Nullable
            public T get() {
                return getter.get();
            }

            @Override
            public void set(@Nullable T value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private final Registry<T> registry;
    private T lastValue;

    protected RegistrySyncSlot(Registry<T> registry) {
        this.registry = registry;
    }

    @Nullable
    public abstract T get();

    public abstract void set(@Nullable T value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();
        var changeType = registry.getId(currentValue) != registry.getId(lastValue) ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(RegistryAccess registryAccess, ChangeType changeType) {
        return new IntSlotPayload(registry.getId(get()));
    }

    @Override
    public void unpackPayload(SlotPayload payload) {
        if (payload instanceof IntSlotPayload intSlotPayload) {
            set(registry.byId(intSlotPayload.value()));
        }
    }
}
