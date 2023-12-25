package com.enderio.api.travel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;

import java.util.function.Function;
import java.util.function.Supplier;

public record TravelEntry<T extends ITravelTarget>(ResourceLocation serializationName, Function<CompoundTag, T> constructor,
                                                   Supplier<Lazy<TravelRenderer<T>>> renderer) {}
