package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.network.packets.ClientBoundRemoveCapacitorBankPacket;
import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
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

    public static final SavedDataType<CapacitorBankSavedData> TYPE = new SavedDataType<>(EnderIO.id("capacitor_bank_network"), CapacitorBankSavedData::new, CODEC);

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Set<CapacitorBankNetwork> networks = new HashSet<>();
    private final Map<BlockPos, CapacitorBankNode> unclaimedNodes = new HashMap<>();

    public static CapacitorBankSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
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
        // avoid CME
        for (CapacitorBankNetwork network : networks.stream().toList()) {
            if (!network.isValid() || network.isEmpty()) {
                onNetworkDiscarded(serverLevel, network);
            } else {
                network.prepareEnergyToDistribute();
            }
        }

        for (var network : networks) {
            network.tick(serverLevel);
        }

    }
}
