package com.enderio.conduits.common.conduit.new_graph;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID)
public class ConduitNetworkSavedData extends SavedData {

    public static Codec<ConduitNetworkSavedData> CODEC = NewConduitNetwork.CODEC.listOf().xmap(ConduitNetworkSavedData::new, ConduitNetworkSavedData::getNetworks);

    private final Multimap<Holder<Conduit<?, ?>>, NewConduitNetwork> networks = HashMultimap.create();

    public static ConduitNetworkSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(ConduitNetworkSavedData::new, ConduitNetworkSavedData::load), "enderio_conduit_network");
    }

    public ConduitNetworkSavedData() {
    }

    private ConduitNetworkSavedData(List<NewConduitNetwork> networks) {
        for (NewConduitNetwork network : networks) {
            this.networks.put(network.conduit(), network);
        }
    }

    private static ConduitNetworkSavedData load(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        // TODO: 1.22 - remove support for the legacy graph format.
        if (nbt.contains(KEY_GRAPHS)) {
            return loadLegacy(nbt, lookupProvider);
        }

        // TODO: Handle partials? getPartialOrThrow probably works here?
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), nbt).getOrThrow();
    }

    private List<NewConduitNetwork> getNetworks() {
        return List.copyOf(networks.values());
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this);
        return compoundTag;
    }

    public static void onNetworkCreated(ServerLevel level, NewConduitNetwork network) {
        Preconditions.checkArgument(network.isValid(), "New network is not valid!");
        get(level).networks.put(network.conduit(), network);
    }

    public static void onNetworkDiscarded(ServerLevel level, NewConduitNetwork network) {
        Preconditions.checkArgument(network.isDiscarded(), "Network is not discarded!");
        get(level).networks.remove(network.conduit(), network);
    }

    @Override
    public boolean isDirty() {
        // Always save networks when the opportunity arises
        return true;
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            get(serverLevel).tick(serverLevel);
        }
    }

    private void tick(ServerLevel serverLevel) {
        // TODO: Remove any empty or invalid graphs.

        Registry<Conduit<?, ?>> conduitRegistry = serverLevel.registryAccess().registryOrThrow(EnderIOConduitsRegistries.Keys.CONDUIT);

        for (var conduit : networks.keySet()) {
            // Skip non-ticking graphs.
            var ticker = conduit.value().ticker();
            if (ticker == null) {
                continue;
            }

            int conduitId = conduitRegistry.getId(conduit.value());
            for (var network : networks.get(conduit)) {
                tickNetwork(serverLevel, conduit, conduitId, ticker, network);
            }
        }
    }

    private <T extends Conduit<T, ?>> void tickNetwork(ServerLevel serverLevel, Holder<Conduit<?, ?>> conduit, int conduitId, ConduitTicker<T> ticker,
        NewConduitNetwork network) {

        int conduitTickRate = conduit.value().graphTickRate();

        // TODO: Offsets for networks so they don't all tick on the same tick.
        if (serverLevel.getGameTime() % conduitTickRate == conduitId % conduitTickRate) {
            network.beforeTicking();
            // TODO: Use the ticker.
        }
    }

    // region Legacy Serialization

    // TODO: 1.22 - Remove

    private static final String KEY_GRAPHS = "Graphs";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_GRAPH_OBJECTS = "GraphObjects";
    private static final String KEY_GRAPH_CONNECTIONS = "GraphConnections";
    private static final String KEY_GRAPH_CONTEXT = "GraphContext";

    // TODO: Write it.

    private static ConduitNetworkSavedData loadLegacy(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        throw new NotImplementedException();
    }

    // endregion
}
