package com.enderio.api.travel;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class TravelTargetAPI {
    public static Optional<TravelTarget> get(Level level, BlockPos pos) {
        ensureIsInitialized();
        return getInternal.apply(level, pos);
    }

    public static <T extends TravelTarget> void set(Level level, T travelTarget) {
        ensureIsInitialized();
        setInternal.accept(level, travelTarget);
    }

    public static void removeAt(Level level, BlockPos pos) {
        ensureIsInitialized();
        removeAtInternal.accept(level, pos);
    }

    public static Collection<TravelTarget> getAll(Level level) {
        ensureIsInitialized();
        return getAllInternal.apply(level);
    }

    public static Stream<TravelTarget> getInItemRange(Level level, BlockPos center) {
        ensureIsInitialized();
        return getInItemRangeInternal.apply(level, center);
    }

    private static void ensureIsInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException("TravelTargetAPI is not initialized");
        }
    }

    private static boolean isInitialized;
    private static BiFunction<Level, BlockPos, Optional<TravelTarget>> getInternal;
    private static BiConsumer<Level, TravelTarget> setInternal;
    private static BiConsumer<Level, BlockPos> removeAtInternal;
    private static Function<Level, Collection<TravelTarget>> getAllInternal;
    private static BiFunction<Level, BlockPos, Stream<TravelTarget>> getInItemRangeInternal;

    @ApiStatus.Internal
    public static void init(
        BiFunction<Level, BlockPos, Optional<TravelTarget>> get,
        BiConsumer<Level, TravelTarget> set,
        BiConsumer<Level, BlockPos> removeAt,
        Function<Level, Collection<TravelTarget>> getAll,
        BiFunction<Level, BlockPos, Stream<TravelTarget>> getInItemRange
    ) {
        if (isInitialized) {
            throw new IllegalStateException("Travel Target API already initialized");
        }

        getInternal = get;
        setInternal = set;
        removeAtInternal = removeAt;
        getAllInternal = getAll;
        getInItemRangeInternal = getInItemRange;
        isInitialized = true;
    }
}
