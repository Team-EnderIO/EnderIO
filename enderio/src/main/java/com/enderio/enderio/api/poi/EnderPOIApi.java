package com.enderio.enderio.api.poi;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public interface EnderPOIApi {
    EnderPOIApi INSTANCE = ServiceLoader.load(EnderPOIApi.class).findFirst().orElseThrow();

    Optional<EnderPOI> get(Level level, BlockPos pos);
    <T extends EnderPOI> void set(Level level, T travelTarget);
    void removeAt(Level level, BlockPos pos);
    Collection<EnderPOI> getAll(Level level);
    Stream<EnderPOI> getInItemRange(Level level, BlockPos center);
}
