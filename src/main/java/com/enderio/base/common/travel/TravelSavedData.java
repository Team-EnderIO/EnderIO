package com.enderio.base.common.travel;

import com.enderio.EnderIO;
import com.enderio.api.travel.ITravelTarget;
import com.enderio.api.travel.TravelRegistry;
import com.enderio.base.common.network.AddTravelTargetPacket;
import com.enderio.base.common.network.RemoveTravelTargetPacket;
import com.enderio.base.common.network.SyncTravelDataPacket;
import com.enderio.core.common.network.CoreNetwork;
import com.enderio.core.common.network.NetworkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class TravelSavedData extends SavedData {

    private static final TravelSavedData CLIENT_INSTANCE = new TravelSavedData();
    public static final String TARGETS = "targets";
    private final Map<BlockPos, ITravelTarget> travelTargets = new HashMap<>();

    public TravelSavedData() {
    }

    public TravelSavedData(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        this.loadNBT(lookupProvider, nbt);
    }

    public static TravelSavedData getTravelData(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(new Factory<>(TravelSavedData::new, TravelSavedData::new), "enderio_traveldata");
        } else {
            return CLIENT_INSTANCE;
        }
    }

    public void loadNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt){
        this.travelTargets.clear();
        ListTag targets = nbt.getList(TARGETS, Tag.TAG_COMPOUND);
        targets.stream().map(anchorData -> (CompoundTag)anchorData)
            .map(TravelRegistry::deserialize)
            .flatMap(Optional::stream)
            .forEach(target -> travelTargets.put(target.getPos(), target));
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

    public void addTravelTarget(Level level, ITravelTarget target) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersInDimension(serverLevel, new AddTravelTargetPacket(target));
        }

        if (TravelRegistry.isRegistered(target)) {
            travelTargets.put(target.getPos(), target);
        } else {
            EnderIO.LOGGER.warn("Tried to add a not registered TravelTarget to the TravelSavedData with name " + target);
        }
    }

    public void removeTravelTargetAt(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersInDimension(serverLevel, new RemoveTravelTargetPacket(pos));
        }

        travelTargets.remove(pos);
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        ListTag tag = new ListTag();
        tag.addAll(travelTargets.values().stream().map(TravelRegistry::serialize).toList());
        nbt.put(TARGETS, tag);
        return nbt;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            var savedData = TravelSavedData.getTravelData(serverPlayer.level());
            var serializedData = savedData.save(new CompoundTag(), serverPlayer.level().registryAccess());
            NetworkUtil.sendTo(new SyncTravelDataPacket(serializedData), serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            var savedData = TravelSavedData.getTravelData(serverPlayer.level());
            var serializedData = savedData.save(new CompoundTag(), serverPlayer.level().registryAccess());
            NetworkUtil.sendTo(new SyncTravelDataPacket(serializedData), serverPlayer);
        }
    }
}
