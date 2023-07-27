package com.enderio.api.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Automatically combines multiple capabilities together.
 */
public class MultiCapabilityProvider implements ICapabilityProvider {
    private final Map<Capability<?>, LazyOptional<?>> capabilities;

    public MultiCapabilityProvider() {
        capabilities = new HashMap<>();
    }

    public <T> void add(Capability<T> cap, LazyOptional<?> optional) {
        capabilities.putIfAbsent(cap, optional);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return capabilities
            .getOrDefault(cap, LazyOptional.empty())
            .cast();
    }
}
