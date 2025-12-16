package com.enderio.enderio.content.poi;

import com.enderio.enderio.api.poi.EnderPOI;
import com.enderio.enderio.api.poi.EnderPOIApi;
import com.enderio.enderio.content.travel.TravelTargetSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class EnderPOIApiImpl implements EnderPOIApi {

    @Override
    public Optional<EnderPOI> get(Level level, BlockPos pos) {
        return TravelTargetSavedData.getTravelData(level).getTravelTarget(pos);
    }

    @Override
    public <T extends EnderPOI> void set(Level level, T travelTarget) {
        TravelTargetSavedData.getTravelData(level).setTravelTarget(level, travelTarget);
    }

    @Override
    public void removeAt(Level level, BlockPos pos) {
        TravelTargetSavedData.getTravelData(level).removeTravelTargetAt(level, pos);
    }

    @Override
    public Collection<EnderPOI> getAll(Level level) {
        return TravelTargetSavedData.getTravelData(level).getTravelTargets();
    }

    @Override
    public Stream<EnderPOI> getInItemRange(Level level, BlockPos center) {
        return TravelTargetSavedData.getTravelData(level).getTravelTargetsInItemRange(center);
    }
}
