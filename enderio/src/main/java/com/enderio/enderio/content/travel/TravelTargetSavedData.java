package com.enderio.enderio.content.travel;

import com.enderio.enderio.api.travel.TravelTarget;
import com.enderio.enderio.foundation.network.packets.ClientboundSyncTravelDataPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundTravelTargetRemovedPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundTravelTargetUpdatedPacket;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Mod.EventBusSubscriber
public class TravelTargetSavedData extends SavedData {

    // Even though the client doesn't need to know the data in the old dimensions,
    // I am more comfortable with each dimension having its own data on the client.
    private static final Map<ResourceKey<Level>, TravelTargetSavedData> CLIENT_DATA = new ConcurrentHashMap<>();

    public static final String TARGETS = "targets";
    private final Map<BlockPos, TravelTarget> travelTargets = new HashMap<>();

    public TravelTargetSavedData() {
    }

    public TravelTargetSavedData(CompoundTag nbt) {
        this.loadNBT(nbt);
    }

    public static TravelTargetSavedData getTravelData(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(TravelTargetSavedData::new, TravelTargetSavedData::new, "enderio_traveldata");
        } else {
            return CLIENT_DATA.computeIfAbsent(level.dimension(), l -> new TravelTargetSavedData());
        }
    }

    public Optional<TravelTarget> getTravelTarget(BlockPos pos) {
        return Optional.ofNullable(travelTargets.get(pos));
    }

    public Collection<TravelTarget> getTravelTargets() {
        return travelTargets.values();
    }

    public Stream<TravelTarget> getTravelTargetsInItemRange(BlockPos center) {
        return travelTargets.entrySet()
                .stream()
                .filter(entry -> entry.getValue().item2BlockRange() == Integer.MAX_VALUE
                        || center.distSqr(entry.getKey()) < entry.getValue().item2BlockRange()
                                * entry.getValue().item2BlockRange())
                .map(Map.Entry::getValue);
    }

    // Adds or updates.
    public void setTravelTarget(Level level, TravelTarget target) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersInDimension(serverLevel, new ClientboundTravelTargetUpdatedPacket(target));
        }

        travelTargets.put(target.pos(), target);
    }

    public void removeTravelTargetAt(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersInDimension(serverLevel, new ClientboundTravelTargetRemovedPacket(pos));
        }

        travelTargets.remove(pos);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag tag = new ListTag();
        tag.addAll(travelTargets.values().stream().map(target -> saveTarget(target)).toList());
        nbt.put(TARGETS, tag);
        return nbt;
    }

    private <T extends TravelTarget> Tag saveTarget(T target) {
        return TravelTarget.CODEC.encodeStart(NbtOps.INSTANCE, target)
                .getOrThrow();
    }

    public void loadNBT(CompoundTag nbt) {
        this.travelTargets.clear();
        ListTag targets = nbt.getList(TARGETS, Tag.TAG_COMPOUND);
        targets.stream()
                .map(anchorData -> (CompoundTag) anchorData)
                .map(tag -> loadTarget(tag))
                .forEach(target -> travelTargets.put(target.pos(), target));
    }

    private TravelTarget loadTarget(Tag tag) {
        return TravelTarget.CODEC.decode(NbtOps.INSTANCE, tag)
                .getOrThrow()
                .getFirst();
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            var savedData = TravelTargetSavedData.getTravelData(serverPlayer.level());
            var serializedData = savedData.save(new CompoundTag());
            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundSyncTravelDataPacket(serializedData));
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            var savedData = TravelTargetSavedData.getTravelData(serverPlayer.level());
            var serializedData = savedData.save(new CompoundTag());
            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundSyncTravelDataPacket(serializedData));
        }
    }
}
