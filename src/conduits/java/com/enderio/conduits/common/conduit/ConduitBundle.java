package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.SlotType;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.conduit.connection.StaticConnectionStates;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.core.common.network.NetworkDataSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ConduitBundle {

    //Do not change this value unless you fix the OffsetHelper
    public static final int MAX_CONDUIT_TYPES = 9;

    public static Codec<ConduitBundle> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(i -> i.pos),
            Conduit.CODEC.listOf()
                .fieldOf("types").forGetter(i -> i.types),
            Codec.unboundedMap(Direction.CODEC, ConduitConnection.CODEC)
                .fieldOf("connections").forGetter(i -> i.connections),
            Codec.unboundedMap(Direction.CODEC, BlockState.CODEC)
                .fieldOf("facades").forGetter(i -> i.facadeTextures),
            Codec.unboundedMap(Conduit.CODEC, ConduitGraphObject.CODEC)
                .fieldOf("nodes").forGetter(i -> i.nodes)
        ).apply(instance, ConduitBundle::new)
    );

    // TODO: Facades.
    public static StreamCodec<RegistryFriendlyByteBuf, ConduitBundle> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        i -> i.pos,
        Conduit.STREAM_CODEC.apply(ByteBufCodecs.list()),
        i -> i.types,
        ByteBufCodecs.map(HashMap::new, Direction.STREAM_CODEC, ConduitConnection.STREAM_CODEC),
        i -> i.connections,
        //ByteBufCodecs.map(HashMap::new, Direction.STREAM_CODEC, BlockState.STREAM_CODEC),
        //i -> i.facadeTextures,
        ByteBufCodecs.map(HashMap::new, Conduit.STREAM_CODEC, ConduitGraphObject.STREAM_CODEC),
        i -> i.nodes,
        ConduitBundle::new
    );

    public static NetworkDataSlot.CodecType<ConduitBundle> DATA_SLOT_TYPE = new NetworkDataSlot.CodecType<>(CODEC, STREAM_CODEC);

    private final Map<Direction, ConduitConnection> connections = new EnumMap<>(Direction.class);
    private final List<Holder<Conduit<?, ?, ?>>> types = new ArrayList<>();

    //fill back after world save
    private final Map<Holder<Conduit<?, ?, ?>>, ConduitGraphObject<?, ?>> nodes = new HashMap<>();
    private final BlockPos pos;

    private final Map<Direction, BlockState> facadeTextures = new EnumMap<>(Direction.class);

    @Nullable
    private Runnable onChangedRunnable;

    public ConduitBundle(Runnable onChanged, BlockPos pos) {
        this.onChangedRunnable = onChanged;
        for (Direction value : Direction.values()) {
            connections.put(value, new ConduitConnection());
        }
        this.pos = pos;
    }

    private ConduitBundle(BlockPos pos, List<Holder<Conduit<?, ?, ?>>> types, Map<Direction, ConduitConnection> connections,
        Map<Holder<Conduit<?, ?, ?>>, ConduitGraphObject<?, ?>> nodes) {
        this(pos, types, connections, Map.of(), nodes);
    }

    private ConduitBundle(
        BlockPos pos,
        List<Holder<Conduit<?, ?, ?>>> types,
        Map<Direction, ConduitConnection> connections,
        Map<Direction, BlockState> facadeTextures,
        Map<Holder<Conduit<?, ?, ?>>, ConduitGraphObject<?, ?>> nodes) {

        this.pos = pos;
        this.types.addAll(types);
        this.connections.putAll(connections);
        this.facadeTextures.putAll(facadeTextures);
        this.nodes.putAll(nodes);
    }

    // TODO: I kind of want to get rid of this.
    public void setOnChangedRunnable(Runnable onChangedRunnable) {
        this.onChangedRunnable = onChangedRunnable;
    }

    public void onChanged() {
        if (onChangedRunnable != null) {
            onChangedRunnable.run();
        }
    }

    /**
     * @param type
     * @return an action containing the type that is now not in this bundle
     */
    public RightClickAction addType(Level level, Holder<Conduit<?, ?, ?>> type, Player player) {
        if (types.size() == MAX_CONDUIT_TYPES) {
            return new RightClickAction.Blocked();
        }

        if (types.contains(type)) {
            return new RightClickAction.Blocked();
        }

       return addTypeInternal(level, type, type.value(), player);
    }

    public <T extends ConduitNetworkContext<T>, U extends ConduitData<U>> RightClickAction addTypeInternal(Level level, Holder<Conduit<?, ?, ?>> typeHolder,
        Conduit<?, T, U> type, Player player) {

        // New node
        ConduitGraphObject<T, U> node = new ConduitGraphObject<>(pos, type.createConduitData(level, pos));

        //upgrade a conduit
        Optional<? extends Holder<Conduit<?, ?, ?>>> first = types.stream().filter(existingConduit -> existingConduit.value().canBeReplacedBy(typeHolder)).findFirst();
        if (first.isPresent()) {
            int index = types.indexOf(first.get());
            types.set(index, typeHolder);

            //noinspection unchecked
            var prevNode = (ConduitGraphObject<T, U>) nodes.remove(first.get());
            nodes.put(typeHolder, node);

            if (prevNode != null) {
                type.onRemoved(prevNode.getConduitData(), level, pos);
                if (!level.isClientSide() && prevNode.getGraph() != null) {
                    prevNode.getGraph().remove(prevNode);
                }
            }

            type.onCreated(node.getConduitData(), level, pos, player);
            connections.values().forEach(connection -> connection.disconnectType(index));
            onChanged();

            return new RightClickAction.Upgrade(first.get());
        }

        //some conduit says no (like higher energy conduit)
        if (types.stream().anyMatch(existingConduit -> !existingConduit.value().canBeInSameBundle(typeHolder) || !type.canBeInSameBundle(existingConduit))) {
            return new RightClickAction.Blocked();
        }

        //sort the list, so order is consistent
        int id = ConduitTypeSorter.getSortIndex(typeHolder);
        var addBefore = types.stream().filter(existing -> ConduitTypeSorter.getSortIndex(existing) > id).findFirst();
        if (addBefore.isPresent()) {
            var value = types.indexOf(addBefore.get());
            types.add(value, typeHolder);
            nodes.put(typeHolder, node);

            type.onCreated(node.getConduitData(), level, pos, player);

            for (Direction direction : Direction.values()) {
                connections.get(direction).addType(value);
            }
        } else {
            types.add(typeHolder);
            nodes.put(typeHolder, node);
            if (types.size() != 1) {
                //NeoForge contains a patch that calls onLoad after the conduit has been placed if it's the first one, so onCreated would be called twice. it's easier to detect here
                type.onCreated(node.getConduitData(), level, pos, player);
            }
        }

        onChanged();
        return new RightClickAction.Insert();
    }

    public void onLoad(Level level, BlockPos pos) {
        for (Holder<Conduit<?, ?, ?>> conduitType : types) {
            var node = getNodeFor(conduitType);
            conduitType.value().onCreated(node.getConduitData().cast(), level, pos, null);
        }
    }

    /**
     * @param type
     * @return if this bundle is empty and the block has to be removed
     * @throws IllegalArgumentException if this type is not in the conduit bundle and we are in dev env
     */
    public boolean removeType(Level level, Holder<Conduit<?, ?, ?>> type) {
        int index = types.indexOf(type);
        if (index == -1) {
            if (!FMLLoader.isProduction()) {
                throw new IllegalArgumentException(
                    "Conduit: " + type.getRegisteredName() + " is not present in conduit bundle " + Arrays.toString(
                        types.stream().map(Holder::getRegisteredName).toArray()));
            }

            return types.isEmpty();
        }

        for (Direction direction : Direction.values()) {
            connections.get(direction).removeType(index);
        }

        if (EffectiveSide.get().isServer()) {
            var node = getNodeForTypeExact(type);
            if (node != null) {
                removeNode(level, type, node);
            }
        }

        types.remove(index);
        onChanged();
        return types.isEmpty();
    }

    // endregion

    public List<Holder<Conduit<?, ?, ?>>> getTypes() {
        return types;
    }

    // region Connections

    public List<Holder<Conduit<?, ?, ?>>> getConnectedTypes(Direction direction) {
        return connections.get(direction).getConnectedTypes(this);
    }

    // Not a fan of this.
    @Deprecated(forRemoval = true)
    public ConnectionState getConnectionState(Direction direction, int index) {
        return connections.get(direction).getConnectionState(index);
    }

    public ConnectionState getConnectionState(Direction direction, Holder<Conduit<?, ?, ?>> conduitType) {
        return connections.get(direction).getConnectionState(getTypeIndex(conduitType));
    }

    public void setConnectionState(Direction direction, Holder<Conduit<?, ?, ?>> conduitType, ConnectionState state) {
        connections.get(direction).setConnectionState(getTypeIndex(conduitType), state);
        onChanged();
    }

    public boolean isConnectionEnd(Direction direction) {
        return connections.get(direction).isEnd();
    }

    // Not a fan of this.
    @Deprecated(forRemoval = true)
    public void disableType(Direction direction, int index) {
        connections.get(direction).disableType(index);
        onChanged();
    }

    public void disableType(Direction direction, Holder<Conduit<?, ?, ?>> conduitType) {
        disableType(direction, getTypeIndex(conduitType));
    }

    public ItemStack getConnectionItem(Direction direction, int conduitIndex, SlotType slotType) {
        return connections.get(direction).getItem(slotType, conduitIndex);
    }

    public ItemStack getConnectionItem(Direction direction, Holder<Conduit<?, ?, ?>> conduitType, SlotType slotType) {
        return getConnectionItem(direction, getTypeIndex(conduitType), slotType);
    }

    public void setConnectionItem(Direction direction, int conduitIndex, SlotType slotType, ItemStack itemStack) {
        connections.get(direction).setItem(slotType, conduitIndex, itemStack);
        onChanged();
    }

    public void setConnectionItem(Direction direction, Holder<Conduit<?, ?, ?>> conduitType, SlotType slotType, ItemStack itemStack) {
        setConnectionItem(direction, getTypeIndex(conduitType), slotType, itemStack);
    }

    // endregion

    // region Facades

    public boolean hasFacade(Direction direction) {
        return facadeTextures.containsKey(direction);
    }

    public Optional<BlockState> getFacade(Direction direction) {
        return Optional.ofNullable(facadeTextures.get(direction));
    }

    public void setFacade(BlockState facade, Direction direction) {
        facadeTextures.put(direction, facade);
        onChanged();
    }

    // endregion

    public void connectTo(Level level, BlockPos pos, Direction direction, Holder<Conduit<?, ?, ?>> type, boolean end) {
        connections.get(direction).connectTo(level, pos, getNodeFor(type), direction, type, getTypeIndex(type), end);
        onChanged();
    }

    public boolean disconnectFrom(Direction direction, Holder<Conduit<?, ?, ?>> type) {
        for (int i = 0; i < types.size(); i++) {
            if (type.value().getTicker().canConnectTo(type, types.get(i))) {
                connections.get(direction).tryDisconnect(i);
                onChanged();
                return true;
            }
        }
        return false;
    }

    @Nullable
    public ConduitGraphObject<?, ?> getNodeForTypeExact(Holder<Conduit<?, ?, ?>> type) {
        return nodes.get(type);
    }

    public <T extends ConduitNetworkContext<T>, U extends ConduitData<U>> ConduitGraphObject<T, U> getTypedNodeFor(Holder<Conduit<?, ?, ?>> type) {
        for (var entry : nodes.entrySet()) {
            if (entry.getKey().value().getTicker().canConnectTo(entry.getKey(), type)) {
                //noinspection unchecked
                return (ConduitGraphObject<T, U>) nodes.get(entry.getKey());
            }
        }

        throw new IllegalStateException("no node matching original type");
    }

    public ConduitGraphObject<?, ?> getNodeFor(Holder<Conduit<?, ?, ?>> type) {
        for (var entry : nodes.entrySet()) {
            if (entry.getKey().value().getTicker().canConnectTo(entry.getKey(), type)) {
                return nodes.get(entry.getKey());
            }
        }

        throw new IllegalStateException("no node matching original type");
    }

    public void setNodeFor(Holder<Conduit<?, ?, ?>> type, ConduitGraphObject<?, ?> node) {
        nodes.put(type, node);
        for (var direction : Direction.values()) {
            ConduitConnection connection = connections.get(direction);
            int index = types.indexOf(type);
            if (index >= 0) {
                var state = connection.getConnectionState(index);
                if (state instanceof DynamicConnectionState dynamicState) {
                    node.pushState(direction, dynamicState);
                }
            }
        }
    }

    private <T extends ConduitNetworkContext<T>, U extends ConduitData<U>> void removeNode(Level level, Holder<Conduit<?, ?, ?>> conduitType,
        ConduitGraphObject<T, U> node) {
        conduitType.value().onRemoved(node.getConduitData().cast(), level, pos);
        if (node.getGraph() != null) {
            node.getGraph().remove(node);
        }

        nodes.remove(conduitType);
    }

    public boolean hasType(Holder<Conduit<?, ?, ?>> type) {
        for (var conduitType : types) {
            if (conduitType.value().getTicker().canConnectTo(conduitType, type)) {
                return true;
            }
        }
        return false;
    }

    public int getTypeIndex(Holder<Conduit<?, ?, ?>> type) {
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).value().getTicker().canConnectTo(types.get(i), type)) {
                return i;
            }
        }
        throw new IllegalStateException("no conduit matching type in bundle");
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(connections, types, facadeTextures);

        // Manually hash the map, using hashContents instead of hashCode to avoid breaking the graph.
        for (var entry : nodes.entrySet()) {
            hash = 31 * hash + entry.getKey().hashCode();
            hash = 31 * hash + entry.getValue().hashContents();
        }

        return hash;
    }

    public Tag save(HolderLookup.Provider lookupProvider) {
        return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    public static ConduitBundle parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.decode(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow().getFirst();
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public ConduitBundle deepCopy() {
        var bundle = new ConduitBundle(() -> {}, pos);
        bundle.types.addAll(types);
        connections.forEach((dir, connection) -> bundle.connections.put(dir, connection.deepCopy()));
        bundle.facadeTextures.putAll(facadeTextures);
        nodes.forEach((type, node) -> bundle.setNodeFor(type, node.deepCopy()));
        return bundle;
    }

    // TODO: Clean this up
    private static final class ConduitConnection {

        public static Codec<ConduitConnection> CODEC =
            ConnectionState.CODEC.listOf(0, MAX_CONDUIT_TYPES)
                .xmap(ConduitConnection::new, i -> Arrays.stream(i.connectionStates).toList());

        public static StreamCodec<RegistryFriendlyByteBuf, ConduitConnection> STREAM_CODEC =
            ConnectionState.STREAM_CODEC.apply(ByteBufCodecs.list())
                .map(ConduitConnection::new, i -> Arrays.stream(i.connectionStates).toList());

        private final ConnectionState[] connectionStates = Util.make(() -> {
            var states = new ConnectionState[MAX_CONDUIT_TYPES];
            Arrays.fill(states, StaticConnectionStates.DISCONNECTED);
            return states;
        });

        ConduitConnection() {
        }

        private ConduitConnection(List<ConnectionState> connectionStates) {
            if (connectionStates.size() > MAX_CONDUIT_TYPES) {
                throw new IllegalArgumentException("Cannot store more than " + MAX_CONDUIT_TYPES + " conduit types per bundle.");
            }

            for (var i = 0; i < connectionStates.size(); i++) {
                this.connectionStates[i] = connectionStates.get(i);
            }
        }

        /**
         * shift all behind that one to the back and set that index to null
         * @param index
         */
        public void addType(int index) {
            for (int i = MAX_CONDUIT_TYPES-1; i > index; i--) {
                connectionStates[i] = connectionStates[i-1];
            }
            connectionStates[index] = StaticConnectionStates.DISCONNECTED;
        }

        public void connectTo(Level level, BlockPos pos, ConduitGraphObject<?, ?> conduitGraphObject, Direction direction, Holder<Conduit<?, ?, ?>> type, int typeIndex, boolean end) {
            if (end) {
                var state = DynamicConnectionState.defaultConnection(level, pos, direction, type);
                connectionStates[typeIndex] = state;
                conduitGraphObject.pushState(direction, state);
            } else {
                connectionStates[typeIndex] = StaticConnectionStates.CONNECTED;
            }
        }

        public void tryDisconnect(int typeIndex) {
            if (connectionStates[typeIndex] != StaticConnectionStates.DISABLED) {
                connectionStates[typeIndex] = StaticConnectionStates.DISCONNECTED;
            }
        }

        /**
         * remove entry and shift all behind one to the front
         * @param index
         */
        public void removeType(int index) {
            connectionStates[index] = StaticConnectionStates.DISCONNECTED;
            for (int i = index+1; i < MAX_CONDUIT_TYPES; i++) {
                connectionStates[i-1] = connectionStates[i];
            }
            connectionStates[MAX_CONDUIT_TYPES-1] = StaticConnectionStates.DISCONNECTED;
        }

        public void disconnectType(int index) {
            connectionStates[index] = StaticConnectionStates.DISCONNECTED;
        }

        public void disableType(int index) {
            connectionStates[index] = StaticConnectionStates.DISABLED;
        }

        public boolean isEnd() {
            return Arrays.stream(connectionStates).anyMatch(DynamicConnectionState.class::isInstance);
        }

        public List<Holder<Conduit<?, ?, ?>>> getConnectedTypes(ConduitBundle bundle) {
            List<Holder<Conduit<?, ?, ?>>> connected = new ArrayList<>();
            for (int i = 0; i < connectionStates.length; i++) {
                if (connectionStates[i].isConnection()) {
                    connected.add(bundle.getTypes().get(i));
                }
            }

            return connected;
        }

        public ConduitConnection deepCopy() {
            ConduitConnection connection = new ConduitConnection();
            //connection states are not mutable (enum/record), so reference is fine
            System.arraycopy(connectionStates, 0, connection.connectionStates, 0, MAX_CONDUIT_TYPES);
            return connection;
        }

        public ConnectionState getConnectionState(int index) {
            return connectionStates[index];
        }

        public void setConnectionState(int i, ConnectionState state) {
            connectionStates[i] = state;
        }

        public ItemStack getItem(SlotType type, int conduitIndex) {
            if (connectionStates[conduitIndex] instanceof DynamicConnectionState dynamicConnectionState) {
                return dynamicConnectionState.getItem(type);
            }

            return ItemStack.EMPTY;
        }

        public void setItem(SlotType type, int conduitIndex, ItemStack stack) {
            if (connectionStates[conduitIndex] instanceof DynamicConnectionState dynamicConnectionState) {
                connectionStates[conduitIndex] = dynamicConnectionState.withItem(type, stack);
            }
        }

        @Override
        public int hashCode() {
            //return i++;
            return Objects.hash((Object[]) connectionStates);
        }
    }
}
