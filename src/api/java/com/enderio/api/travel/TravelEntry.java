package com.enderio.api.travel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.function.Function;
import java.util.function.Supplier;

public record TravelEntry<T extends TravelTarget>(ResourceLocation serializationName, Function<CompoundTag, T> constructor,
                                                  Supplier<Lazy<TravelRenderer<T>>> renderer) {}
