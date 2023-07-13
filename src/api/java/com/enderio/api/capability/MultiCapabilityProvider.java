package com.enderio.api.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Automatically combines multiple capabilities together; handling serialization too.
 */
public class MultiCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    private final Map<Capability<?>, LazyOptional<?>> capabilities;
    private final Map<String, LazyOptional<? extends INBTSerializable<Tag>>> serializedCaps;

    public MultiCapabilityProvider() {
        capabilities = new HashMap<>();
        serializedCaps = new HashMap<>();
    }

    public <T> void addSimple(Capability<T> cap, LazyOptional<?> optional) {
        capabilities.putIfAbsent(cap, optional);
    }

    public <T> void addSerialized(String serializedName, Capability<T> cap, LazyOptional<? extends INBTSerializable<Tag>> optional) {
        capabilities.putIfAbsent(cap, optional);
        serializedCaps.putIfAbsent(serializedName, optional);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return capabilities
            .getOrDefault(cap, LazyOptional.empty())
            .cast();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        for (var entry : serializedCaps.entrySet()) {
            entry.getValue().ifPresent(
                capability -> tag.put(entry.getKey(), capability.serializeNBT())
            );
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (var entry : serializedCaps.entrySet()) {
            entry
                .getValue()
                .ifPresent(capability -> {
                    if (nbt.contains(entry.getKey())) {
                        capability.deserializeNBT(nbt.get(entry.getKey()));
                    }
                });
        }
    }
}
