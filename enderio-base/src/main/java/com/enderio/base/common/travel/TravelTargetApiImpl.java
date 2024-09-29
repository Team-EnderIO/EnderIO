package com.enderio.base.common.travel;

import com.enderio.base.api.travel.TravelTarget;
import com.enderio.base.api.travel.TravelTargetApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class TravelTargetApiImpl implements TravelTargetApi {

    @Override
    public Optional<TravelTarget> get(Level level, BlockPos pos) {
        return TravelTargetSavedData.getTravelData(level).getTravelTarget(pos);
    }

    @Override
    public <T extends TravelTarget> void set(Level level, T travelTarget) {
        TravelTargetSavedData.getTravelData(level).setTravelTarget(level, travelTarget);
    }

    @Override
    public void removeAt(Level level, BlockPos pos) {
        TravelTargetSavedData.getTravelData(level).removeTravelTargetAt(level, pos);
    }

    @Override
    public Collection<TravelTarget> getAll(Level level) {
        return TravelTargetSavedData.getTravelData(level).getTravelTargets();
    }

    @Override
    public Stream<TravelTarget> getInItemRange(Level level, BlockPos center) {
        return TravelTargetSavedData.getTravelData(level).getTravelTargetsInItemRange(center);
    }
}
