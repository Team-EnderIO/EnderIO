package com.enderio.base.api.integration;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntegrationWrapper<T extends Integration> {

    private final String modid;

    @Nullable
    private final T value;

    public IntegrationWrapper(String modid, Supplier<Supplier<T>> supplier, IEventBus modEventBus) {
        this.modid = modid;
        value = ModList.get().isLoaded(modid) ? supplier.get().get() : null;
        ifPresent(integration -> {
            IntegrationManager.addIntegration(integration);
            integration.addEventListener(modEventBus, NeoForge.EVENT_BUS);
        });
    }

    public boolean isPresent() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        return value == null;
    }

    /**
     * If non-empty, invoke the specified {@link Consumer} with the object,
     * otherwise do nothing.
     *
     * @param consumer The {@link Consumer} to run if this optional is non-empty.
     * @throws NullPointerException if {@code consumer} is null and this {@link java.util.Optional} is non-empty
     */
    public void ifPresent(Consumer<? super T> consumer) {
        if (isPresent()) {
            consumer.accept(value);
        }
    }

    /**
     * Only call when you are in code that is running if the Integration is Present
     * @return
     */
    public T expectPresent() {
        return value;
    }
}
