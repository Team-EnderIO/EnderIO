package com.enderio.base.common.travel;

import com.enderio.EnderIO;
import com.enderio.api.travel.ITravelTarget;
import com.enderio.api.travel.TravelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class TravelSavedData extends SavedData {

    private static TravelSavedData clientInstance;

    private Map<BlockPos, ITravelTarget> travelTargets = new HashMap<>();

    public static TravelSavedData getTravelData(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(TravelSavedData::load, TravelSavedData::new, "enderio_traveldata");
        } else {
            if (clientInstance == null) {
                clientInstance = new TravelSavedData();
            }
            return clientInstance;
        }
    }

    public Optional<ITravelTarget> getTravelTarget(BlockPos pos) {
        return Optional.ofNullable(travelTargets.get(pos));
    }

    public Collection<ITravelTarget> getTravelTargets() {
        return travelTargets.values();
    }

    public Stream<ITravelTarget> getTravelTargetsInItemRange(BlockPos center) {
        return travelTargets.entrySet().stream().
                filter(entry -> center.distSqr(entry.getKey()) < entry.getValue().getItem2BlockRange()*entry.getValue().getItem2BlockRange())
            .map(Map.Entry::getValue);
    }

    public void addTravelTarget(ITravelTarget target) {
        if (TravelRegistry.isRegistered(target)) {
            travelTargets.put(target.getPos(), target);
        } else {
            EnderIO.LOGGER.warn("Tried to add a not registered TravelTarget to the TravelSavedData with name " + target);
        }
    }

    public void removeTravelTargetAt(BlockPos pos) {
        travelTargets.remove(pos);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag tag = new ListTag();
        tag.addAll(travelTargets.values().stream().map(TravelRegistry::serialize).toList());
        nbt.put("targets", tag);
        return nbt;
    }

    private static TravelSavedData load(CompoundTag nbt) {
        TravelSavedData data = new TravelSavedData();
        data.load(nbt.getList("targets", Tag.TAG_COMPOUND));
        return data;
    }

    private void load(ListTag nbt) {
        nbt.stream().map(anchorData -> (CompoundTag)anchorData)
            .map(TravelRegistry::deserialize)
            .flatMap(Optional::stream)
            .forEach(target -> travelTargets.put(target.getPos(), target));
    }

    // Always save this data
    @Override
    public boolean isDirty() {
        return EffectiveSide.get().isServer();
    }

}
