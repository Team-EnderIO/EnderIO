package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class RegistrySyncSlot<T> implements SyncSlot {

    public static <T> RegistrySyncSlot<T> standalone(ResourceKey<Registry<T>> registryKey) {
        return new RegistrySyncSlot<>(registryKey) {
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

    public static <T> RegistrySyncSlot<T> simple(ResourceKey<Registry<T>> registryKey, Supplier<T> getter,
            Consumer<T> setter) {
        return new RegistrySyncSlot<>(registryKey) {

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

    public static <T> RegistrySyncSlot<T> readOnly(ResourceKey<Registry<T>> registryKey, Supplier<T> getter) {
        return new RegistrySyncSlot<>(registryKey) {

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

    private final ResourceKey<Registry<T>> registryKey;
    private T lastValue;

    protected RegistrySyncSlot(ResourceKey<Registry<T>> registryKey) {
        this.registryKey = registryKey;
    }

    @Nullable
    public abstract T get();

    public abstract void set(@Nullable T value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();
        var changeType = Objects.equals(currentValue, lastValue) ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        return new IntSlotPayload(level.registryAccess().registryOrThrow(registryKey).getId(get()));
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof IntSlotPayload intSlotPayload) {
            set(level.registryAccess().registryOrThrow(registryKey).byId(intSlotPayload.value()));
        }
    }
}
