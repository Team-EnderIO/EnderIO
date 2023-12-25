package com.enderio.api.integration;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntegrationWrapper<T extends Integration> {

    private final String modid;

    @Nullable
    private final T value;

    public IntegrationWrapper(String modid, Supplier<T> supplier) {
        this.modid = modid;
        value = ModList.get().isLoaded(modid) ? supplier.get() : null;
        ifPresent(integration -> {
            IntegrationManager.addIntegration(integration);
            integration.addEventListener(FMLJavaModLoadingContext.get().getModEventBus(), MinecraftForge.EVENT_BUS);
        });
    }

    public boolean isPresent() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        return value == null;
    }

    /**
     * If non-empty, invoke the specified {@link NonNullConsumer} with the object,
     * otherwise do nothing.
     *
     * @param consumer The {@link NonNullConsumer} to run if this optional is non-empty.
     * @throws NullPointerException if {@code consumer} is null and this {@link LazyOptional} is non-empty
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