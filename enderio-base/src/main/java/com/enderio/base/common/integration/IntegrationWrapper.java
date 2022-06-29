package com.enderio.base.common.integration;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import javax.annotation.Nullable;
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
            integration.setModid(modid);
            IntegrationManager.ALL_INTEGRATIONS.add(integration);
            integration.addEventListener(FMLJavaModLoadingContext.get().getModEventBus(), MinecraftForge.EVENT_BUS);
        });
    }

    public boolean isPresent() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        return value == null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (isPresent())
            consumer.accept(value);
    }
}