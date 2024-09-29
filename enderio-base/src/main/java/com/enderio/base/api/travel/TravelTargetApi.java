package com.enderio.base.api.travel;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public interface TravelTargetApi {
    TravelTargetApi INSTANCE = ServiceLoader.load(TravelTargetApi.class).findFirst().orElseThrow();

    Optional<TravelTarget> get(Level level, BlockPos pos);
    <T extends TravelTarget> void set(Level level, T travelTarget);
    void removeAt(Level level, BlockPos pos);
    Collection<TravelTarget> getAll(Level level);
    Stream<TravelTarget> getInItemRange(Level level, BlockPos center);
}
