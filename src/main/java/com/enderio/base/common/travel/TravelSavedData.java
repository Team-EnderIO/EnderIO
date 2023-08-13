package com.enderio.base.common.travel;

import com.enderio.EnderIO;
import com.enderio.api.travel.ITravelTarget;
import com.enderio.api.travel.TravelRegistry;
import com.enderio.base.common.network.SyncTravelDataPacket;
import com.enderio.core.common.network.CoreNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TravelSavedData extends SavedData {

    private static final TravelSavedData CLIENT_INSTANCE = new TravelSavedData();
    private final Map<BlockPos, ITravelTarget> travelTargets = new HashMap<>();

    public TravelSavedData() {

    }

    public TravelSavedData(CompoundTag nbt) {
        this();
        this.loadNBT(nbt);
    }

    public static TravelSavedData getTravelData(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(TravelSavedData::new, TravelSavedData::new, "enderio_traveldata");
        } else {
            return CLIENT_INSTANCE;
        }
    }

    public void loadNBT(CompoundTag nbt){
        this.travelTargets.clear();
        ListTag targets = nbt.getList("targets", Tag.TAG_COMPOUND);
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
        if(level.isClientSide)
            return;
        if (TravelRegistry.isRegistered(target)) {
            travelTargets.put(target.getPos(), target);
            syncData(level);
        } else {
            EnderIO.LOGGER.warn("Tried to add a not registered TravelTarget to the TravelSavedData with name " + target);
        }
    }

    public void updateTravelTarget(Level level, ITravelTarget target){
        if(level.isClientSide)
            return;
        if(travelTargets.containsKey(target.getPos())){
            travelTargets.replace(target.getPos(), target);
            syncData(level);
        }
    }

    public void removeTravelTargetAt(Level level, BlockPos pos) {
        if(level.isClientSide)
            return;
        travelTargets.remove(pos);
        syncData(level);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag tag = new ListTag();
        tag.addAll(travelTargets.values().stream().map(TravelRegistry::serialize).toList());
        nbt.put("targets", tag);
        return nbt;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    private void syncData(Level level){
        CoreNetwork.sendToDimension(level.dimension(), new SyncTravelDataPacket(save(new CompoundTag())));
    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.getCommandSenderWorld().isClientSide && player instanceof ServerPlayer serverPlayer) {
            CoreNetwork.sendToPlayer(serverPlayer, new SyncTravelDataPacket(TravelSavedData.getTravelData(player.getCommandSenderWorld()).save(new CompoundTag())));
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (!player.getCommandSenderWorld().isClientSide && player instanceof ServerPlayer serverPlayer) {
            CoreNetwork.sendToPlayer(serverPlayer, new SyncTravelDataPacket(TravelSavedData.getTravelData(player.getCommandSenderWorld()).save(new CompoundTag())));
        }
    }
}
