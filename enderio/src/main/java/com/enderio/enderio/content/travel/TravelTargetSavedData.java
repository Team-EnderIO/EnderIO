package com.enderio.enderio.content.travel;

import com.enderio.enderio.api.travel.TravelTarget;
import com.enderio.enderio.foundation.network.packets.ClientboundSyncTravelDataPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundTravelTargetRemovedPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundTravelTargetUpdatedPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@EventBusSubscriber
public class TravelTargetSavedData extends SavedData {

    public static final Codec<TravelTargetSavedData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        TravelTarget.CODEC.listOf().fieldOf("Targets").forGetter(i -> new ArrayList<>(i.travelTargets.values()))
    ).apply(inst, TravelTargetSavedData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TravelTargetSavedData> STREAM_CODEC = TravelTarget.STREAM_CODEC.apply(ByteBufCodecs.list())
        .map(TravelTargetSavedData::new, i -> new ArrayList<>(i.travelTargets.values()));

    public static final SavedDataType<TravelTargetSavedData> TYPE = new SavedDataType<>("enderio_traveldata", TravelTargetSavedData::new, CODEC);


    // Even though the client doesn't need to know the data in the old dimensions,
    // I am more comfortable with each dimension having its own data on the client.
    private static final Map<ResourceKey<Level>, TravelTargetSavedData> CLIENT_DATA = new ConcurrentHashMap<>();

    private final Map<BlockPos, TravelTarget> travelTargets = new HashMap<>();

    private TravelTargetSavedData() {
    }

    private TravelTargetSavedData(List<TravelTarget> travelTargets) {
        for (TravelTarget target : travelTargets) {
            this.travelTargets.put(target.pos(), target);
        }
    }

    public static TravelTargetSavedData getTravelData(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage()
                    .computeIfAbsent(TYPE);
        } else {
            return CLIENT_DATA.computeIfAbsent(level.dimension(), l -> new TravelTargetSavedData());
        }
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public static void setTravelData(Level level, TravelTargetSavedData data) {
        CLIENT_DATA.put(level.dimension(), data);
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
    public boolean isDirty() {
        return true;
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            var savedData = TravelTargetSavedData.getTravelData(serverPlayer.level());
            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundSyncTravelDataPacket(savedData));
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            var savedData = TravelTargetSavedData.getTravelData(serverPlayer.level());
            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundSyncTravelDataPacket(savedData));
        }
    }
}
