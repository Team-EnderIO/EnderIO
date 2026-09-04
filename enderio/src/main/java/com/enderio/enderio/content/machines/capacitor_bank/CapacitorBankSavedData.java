package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.enderio.foundation.network.packets.ClientBoundRemoveCapacitorBankPacket;
import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber
public class CapacitorBankSavedData extends SavedData {

    public static final Codec<CapacitorBankSavedData> CODEC = CapacitorBankNetwork.CODEC.listOf()
        .xmap(CapacitorBankSavedData::new, capacitorBankSavedData -> capacitorBankSavedData.getNetworks().stream().toList());

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String KEY_DATA = "Networks";

    private final Set<CapacitorBankNetwork> networks = new HashSet<>();
    private final Map<BlockPos, CapacitorBankNode> unclaimedNodes = new HashMap<>();

    public static CapacitorBankSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(CapacitorBankSavedData::new, CapacitorBankSavedData::load),
            "enderio_capacitor_bank_network");
    }

    public CapacitorBankSavedData() {}

    private CapacitorBankSavedData(List<CapacitorBankNetwork> networks) {
        this.networks.addAll(networks);
        for (CapacitorBankNetwork network : networks) {
            for (CapacitorBankNode node : network.nodes()) {
                unclaimedNodes.put(node.getPos(), node);
            }
        }
    }

    private Set<CapacitorBankNetwork> getNetworks() {
        return networks;
    }

    @Nullable
    public CapacitorBankNode claimNode(BlockPos pos) {
        if (unclaimedNodes.containsKey(pos)) {
            return unclaimedNodes.remove(pos);
        }

        return null;
    }

    public static void onNetworkCreated(ServerLevel level, CapacitorBankNetwork network) {
        get(level).onNetworkCreated(network);
    }

    private void onNetworkCreated(CapacitorBankNetwork network) {
        Preconditions.checkArgument(network.isValid(), "New network is not valid!");
        if (!networks.contains(network)) {
            networks.add(network);
        }
    }

    public static void onNetworkDiscarded(ServerLevel level, CapacitorBankNetwork network) {
        get(level).onNetworkDiscarded(network, level);
    }

    private void onNetworkDiscarded(CapacitorBankNetwork network, ServerLevel serverLevel) {
        // Allow empty or discarded networks here
        networks.remove(network);
        PacketDistributor.sendToPlayersInDimension(serverLevel,
            new ClientBoundRemoveCapacitorBankPacket(network.getUuid()));
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            get(serverLevel).tick(serverLevel);
        }
    }

    private void tick(ServerLevel serverLevel) {
        networks.stream()
            .filter(n -> !n.isValid() || n.isEmpty())
            .toList() // avoid CME
            .forEach(n -> onNetworkDiscarded(serverLevel, n));

        for (var network : networks) {
            network.tick(serverLevel);
        }

    }

    private static CapacitorBankSavedData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var result = CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), compoundTag.get(KEY_DATA));
        result.error().ifPresent(e -> LOGGER.error("Errors loading capacitor bank network data, some networks may be missing: {}", e.message()));
        return result.getPartialOrThrow();
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.put(KEY_DATA, CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow());
        return compoundTag;
    }
}
