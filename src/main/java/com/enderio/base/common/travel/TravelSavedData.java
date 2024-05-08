package com.enderio.base.common.travel;

import com.enderio.api.travel.TravelTarget;
import com.enderio.api.travel.TravelTargetSerializer;
import com.enderio.base.common.network.TravelTargetUpdatedPacket;
import com.enderio.base.common.network.TravelTargetRemovedPacket;
import com.enderio.base.common.network.SyncTravelDataPacket;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class TravelSavedData extends SavedData {

    // TODO: How will the API interact with this to add and remove targets?

    // Even though the client doesn't need to know the data in the old dimensions,
    //  I am more comfortable with each dimension having its own data on the client.
    private static final Map<ResourceKey<Level>, TravelSavedData> CLIENT_DATA = new ConcurrentHashMap<>();

    public static final String TARGETS = "targets";
    private final Map<BlockPos, TravelTarget> travelTargets = new HashMap<>();

    public TravelSavedData() {
    }

    public TravelSavedData(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        this.loadNBT(lookupProvider, nbt);
    }

    public static TravelSavedData getTravelData(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(new Factory<>(TravelSavedData::new, TravelSavedData::new), "enderio_traveldata");
        } else {
            return CLIENT_DATA.computeIfAbsent(level.dimension(), l -> new TravelSavedData());
        }
    }

    public Optional<TravelTarget> getTravelTarget(BlockPos pos) {
        return Optional.ofNullable(travelTargets.get(pos));
    }

    public Collection<TravelTarget> getTravelTargets() {
        return travelTargets.values();
    }

    public Stream<TravelTarget> getTravelTargetsInItemRange(BlockPos center) {
        return travelTargets.entrySet().stream().
                filter(entry -> center.distSqr(entry.getKey()) < entry.getValue().item2BlockRange()*entry.getValue().item2BlockRange())
            .map(Map.Entry::getValue);
    }

    // Adds or updates.
    public void setTravelTarget(Level level, TravelTarget target) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersInDimension(serverLevel, new TravelTargetUpdatedPacket(target));
        }

        travelTargets.put(target.pos(), target);
    }

    public void removeTravelTargetAt(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersInDimension(serverLevel, new TravelTargetRemovedPacket(pos));
        }

        travelTargets.remove(pos);
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        ListTag tag = new ListTag();
        tag.addAll(travelTargets.values().stream().map(target -> saveTarget(lookupProvider, target)).toList());
        nbt.put(TARGETS, tag);
        return nbt;
    }

    private <T extends TravelTarget> Tag saveTarget(HolderLookup.Provider lookupProvider, T target) {
        return TravelTarget.CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), target).getOrThrow();
    }

    public void loadNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        this.travelTargets.clear();
        ListTag targets = nbt.getList(TARGETS, Tag.TAG_COMPOUND);
        targets.stream().map(anchorData -> (CompoundTag)anchorData)
            .map(tag -> loadTarget(lookupProvider, tag))
            .forEach(target -> travelTargets.put(target.pos(), target));
    }

    private TravelTarget loadTarget(HolderLookup.Provider lookupProvider, Tag tag) {
        return TravelTarget.CODEC.decode(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow().getFirst();
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
            PacketDistributor.sendToPlayer(serverPlayer, new SyncTravelDataPacket(serializedData));
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            var savedData = TravelSavedData.getTravelData(serverPlayer.level());
            var serializedData = savedData.save(new CompoundTag(), serverPlayer.level().registryAccess());
            PacketDistributor.sendToPlayer(serverPlayer, new SyncTravelDataPacket(serializedData));
        }
    }
}
