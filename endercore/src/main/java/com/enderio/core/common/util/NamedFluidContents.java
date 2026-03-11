package com.enderio.core.common.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record NamedFluidContents(ImmutableMap<String, FluidStack> fluidMap) {
    public static NamedFluidContents of(CompoundTag tag) {
        var map = new HashMap<String, FluidStack>();
        for (String key : tag.getAllKeys()) {
            map.put(key, FluidStack.loadFluidStackFromNBT(tag.getCompound(key)));
        }

        return new NamedFluidContents(ImmutableMap.copyOf(map));
    }

    public static NamedFluidContents readFromNetwork(FriendlyByteBuf buf) {
        var map = buf.readMap(HashMap::new, FriendlyByteBuf::readUtf, FluidStack::readFromPacket);
        return new NamedFluidContents(ImmutableMap.copyOf(map));
    }

    public static NamedFluidContents copyOf(Map<String, FluidStack> fluidMap) {
        var copies = fluidMap.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, i -> i.getValue().copy()));

        return new NamedFluidContents(ImmutableMap.copyOf(copies));
    }

    public FluidStack copy(String key) {
        return fluidMap.getOrDefault(key, FluidStack.EMPTY).copy();
    }

    public CompoundTag save(CompoundTag tag) {
        for (Map.Entry<String, FluidStack> entry : fluidMap.entrySet()) {
            tag.put(entry.getKey(), entry.getValue().writeToNBT(new CompoundTag()));
        }

        return tag;
    }

    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeMap(fluidMap, FriendlyByteBuf::writeUtf, (b, v) -> v.writeToPacket(b));
    }
}
