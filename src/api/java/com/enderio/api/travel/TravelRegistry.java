package com.enderio.api.travel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

// TODO: Maybe this should be a forge registry?
@ApiStatus.Internal
public class TravelRegistry {

    private static final Map<ResourceLocation, TravelEntry<?>> REGISTRY = new HashMap<>();

    public static <T extends ITravelTarget> void addTravelEntry(ResourceLocation serializationName, Function<CompoundTag, T> constructor,
        Supplier<Lazy<TravelRenderer<T>>> renderer) {
        REGISTRY.put(serializationName, new TravelEntry<>(serializationName, constructor, renderer));
    }

    public static <T extends ITravelTarget> void addTravelEntry(TravelEntry<?> travelEntry) {
        REGISTRY.put(travelEntry.serializationName(), travelEntry);
    }

    public static <T extends ITravelTarget> TravelRenderer<T> getRenderer(T entry) {
        return (TravelRenderer<T>) REGISTRY.get(entry.getSerializationName()).renderer().get().get();
    }

    public static Optional<ITravelTarget> deserialize(CompoundTag nbt) {
        return Optional.ofNullable(REGISTRY.get(new ResourceLocation(nbt.getString("name")))).map(entry -> entry.constructor().apply(nbt.getCompound("data")));
    }

    public static boolean isRegistered(@Nullable ITravelTarget target) {
        if (target == null) { //TODO why null?
            return false;
        }
        return REGISTRY.containsKey(target.getSerializationName());
    }

    public static CompoundTag serialize(ITravelTarget travelData) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("name", travelData.getSerializationName().toString());
        nbt.put("data", travelData.save());
        return nbt;
    }
}
