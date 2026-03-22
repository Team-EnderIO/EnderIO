package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.ConduitNetworkContextType;
import com.enderio.enderio.api.conduits.connection.path.PriorityConnectionPathComparator;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import com.enderio.enderio.content.conduits.type.energy.EnergyConduit;
import com.enderio.enderio.content.conduits.type.energy.EnergyConduitConnectionConfig;
import com.enderio.enderio.content.conduits.type.fluid.FluidConduit;
import com.enderio.enderio.content.conduits.type.fluid.FluidConduitConnectionConfig;
import com.enderio.enderio.content.conduits.type.fluid.FluidConduitTicker;
import com.enderio.enderio.content.conduits.type.item.ItemConduit;
import com.enderio.enderio.content.conduits.type.item.ItemConduitConnectionConfig;
import com.enderio.enderio.content.conduits.type.item.ItemConduitNodeData;
import com.enderio.enderio.content.conduits.type.item.ItemConduitTicker;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduitTicker;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduit;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduitConnectionConfig;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduitNetworkContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIOConduitTypes {
    private static final DeferredRegister<ConduitType<?, ?>> CONDUIT_TYPES = DeferredRegister
        .create(EnderIORegistries.CONDUIT_TYPE, EnderIO.MOD_ID);

    public static final Supplier<ConduitType<EnergyConduit, EnergyConduitConnectionConfig>> ENERGY = CONDUIT_TYPES.register("energy",
        () -> ConduitType
            .builder(EnergyConduit.CODEC, EnergyConduitConnectionConfig.TYPE)
            .exposeCapability(Capabilities.Energy.BLOCK)
            .doesRequireNetworkCaches()
            .connectionComparator((a, b) -> Integer.compare(
                b.connectionConfig(EnergyConduitConnectionConfig.TYPE).priority(),
                a.connectionConfig(EnergyConduitConnectionConfig.TYPE).priority()))
            .connectionComparerFromReference(new PriorityConnectionPathComparator(conn ->
                conn.node().getConnectionConfig(conn.connectionSide(), EnergyConduitConnectionConfig.TYPE).priority()))
            .build());

    public static final Supplier<ConduitType<RedstoneConduit, RedstoneConduitConnectionConfig>> REDSTONE = CONDUIT_TYPES.register("redstone",
        () -> ConduitType
            .builder(RedstoneConduit.CODEC, RedstoneConduitConnectionConfig.TYPE)
            .doesRequireNetworkCaches()
            .ticker(RedstoneConduitTicker.INSTANCE, 2)
            .build());

    public static final Supplier<ConduitType<FluidConduit, FluidConduitConnectionConfig>> FLUID = CONDUIT_TYPES.register("fluid",
        () -> ConduitType
            .builder(FluidConduit.CODEC, FluidConduitConnectionConfig.TYPE)
            .doesRequireNetworkCaches()
            .ticker(FluidConduitTicker.INSTANCE, 5)
            .connectionComparerFromReference(new PriorityConnectionPathComparator(conn ->
                conn.node().getConnectionConfig(conn.connectionSide(), FluidConduitConnectionConfig.TYPE).insertPriority()))
            .build());

    public static final Supplier<ConduitType<ItemConduit, ItemConduitConnectionConfig>> ITEM = CONDUIT_TYPES.register("item",
        () -> ConduitType
            .builder(ItemConduit.CODEC, ItemConduitConnectionConfig.TYPE)
            .doesRequireNetworkCaches()
            .ticker(ItemConduitTicker.INSTANCE, ItemConduit::networkTickRate)
            .connectionComparerFromReference(new PriorityConnectionPathComparator(conn ->
                conn.node().getConnectionConfig(conn.connectionSide(), ItemConduitConnectionConfig.TYPE).priority()))
            .build());

    public static class ConnectionTypes {
        private static final DeferredRegister<ConnectionConfigType<?>> CONNECTION_TYPES = DeferredRegister
                .create(EnderIORegistries.CONDUIT_CONNECTION_CONFIG_TYPE, EnderIO.MOD_ID);

        public static final Supplier<ConnectionConfigType<ItemConduitConnectionConfig>> ITEM = CONNECTION_TYPES
                .register("item", () -> ItemConduitConnectionConfig.TYPE);

        public static final Supplier<ConnectionConfigType<EnergyConduitConnectionConfig>> ENERGY = CONNECTION_TYPES
                .register("energy", () -> EnergyConduitConnectionConfig.TYPE);

        public static final Supplier<ConnectionConfigType<RedstoneConduitConnectionConfig>> REDSTONE = CONNECTION_TYPES
                .register("redstone", () -> RedstoneConduitConnectionConfig.TYPE);

        public static final Supplier<ConnectionConfigType<FluidConduitConnectionConfig>> FLUID = CONNECTION_TYPES
                .register("fluid", () -> FluidConduitConnectionConfig.TYPE);
    }

    public static class NodeData {
        private static final DeferredRegister<NodeDataType<?>> NODE_DATA_TYPES = DeferredRegister
                .create(EnderIORegistries.CONDUIT_NODE_DATA_TYPE, EnderIO.MOD_ID);

        public static final Supplier<NodeDataType<ItemConduitNodeData>> ITEM = NODE_DATA_TYPES.register("item",
                () -> ItemConduitNodeData.TYPE);
    }

    public static class ContextTypes {
        public static final DeferredRegister<ConduitNetworkContextType<?>> CONDUIT_NETWORK_CONTEXT_TYPES = DeferredRegister
                .create(EnderIORegistries.CONDUIT_NETWORK_CONTEXT_TYPE, EnderIO.MOD_ID);

        public static final Supplier<ConduitNetworkContextType<RedstoneConduitNetworkContext>> REDSTONE = CONDUIT_NETWORK_CONTEXT_TYPES
                .register("redstone", () -> RedstoneConduitNetworkContext.TYPE);
    }

    public static void register(IEventBus bus) {
        CONDUIT_TYPES.register(bus);
        ConnectionTypes.CONNECTION_TYPES.register(bus);
        NodeData.NODE_DATA_TYPES.register(bus);
        ContextTypes.CONDUIT_NETWORK_CONTEXT_TYPES.register(bus);
    }
}
