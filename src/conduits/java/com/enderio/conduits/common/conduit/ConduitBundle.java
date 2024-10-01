package com.enderio.conduits.common.conduit;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.client.ConduitClientSetup;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.conduits.common.conduit.connection.StaticConnectionStates;
import com.enderio.conduits.common.init.EIOConduitTypes;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ConduitBundle implements INBTSerializable<CompoundTag> {

    //Do not change this value unless you fix the OffsetHelper
    public static final int MAX_CONDUIT_TYPES = 9;

    private final Map<Direction, ConduitConnection> connections = new EnumMap<>(Direction.class);

    private final List<ConduitType<?>> types = new ArrayList<>();

    //fill back after world save
    private final Map<ConduitType<?>, ConduitGraphObject<?>> nodes = new HashMap<>();
    private final BlockPos pos;

    private final Map<Direction, BlockState> facadeTextures = new EnumMap<>(Direction.class);

    @Nullable
    private Runnable onChangedRunnable;

    private static final boolean IS_NEO_ENV_AFTER_ON_LOAD_CHANGE;

    static {
        IModInfo forge = ModList.get().getModFileById("forge").getMods().get(0);
        IS_NEO_ENV_AFTER_ON_LOAD_CHANGE = forge.getDisplayName().equals("NeoForge") && forge.getVersion().compareTo(new DefaultArtifactVersion("47.1.77")) >= 0;
    }

    public ConduitBundle(Runnable onChanged, BlockPos pos) {
        this.onChangedRunnable = onChanged;
        for (Direction value : Direction.values()) {
            connections.put(value, new ConduitConnection());
        }
        this.pos = pos;
    }

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
    public <T extends ConduitData<T>> RightClickAction addType(Level level, ConduitType<T> type, Player player) {
        if (types.size() == MAX_CONDUIT_TYPES) {
            return new RightClickAction.Blocked();
        }

        if (types.contains(type)) {
            return new RightClickAction.Blocked();
        }

        //upgrade a conduit
        Optional<? extends ConduitType<?>> first = types.stream().filter(existingConduit -> existingConduit.canBeReplacedBy(type)).findFirst();
        ConduitGraphObject<T> node = new ConduitGraphObject<>(pos, type.createConduitData(level, pos));
        if (first.isPresent()) {
            int index = types.indexOf(first.get());
            types.set(index, type);

            var prevNode = (ConduitGraphObject<T>) nodes.remove(first.get());

            if (prevNode != null) {
                node = new ConduitGraphObject<>(pos, prevNode.getConduitData()); //new node with old data
                prevNode.getConduitData().onRemoved(type, level, pos);
                if (!level.isClientSide() && prevNode.getGraph() != null) {
                    prevNode.getGraph().remove(prevNode);
                }
            }

            nodes.put(type, node);
            node.getConduitData().onCreated(type, level, pos, player);
            onChanged();

            return new RightClickAction.Upgrade(first.get());
        }

        //some conduit says no (like higher energy conduit)
        if (types.stream().anyMatch(existingConduit -> !existingConduit.canBeInSameBlock(type) || !type.canBeInSameBlock(existingConduit))) {
            return new RightClickAction.Blocked();
        }

        //sort the list, so order is consistent
        int id = ConduitTypeSorter.getSortIndex(type);
        var addBefore = types.stream().filter(existing -> ConduitTypeSorter.getSortIndex(existing) > id).findFirst();
        if (addBefore.isPresent()) {
            var value = types.indexOf(addBefore.get());
            types.add(value, type);
            nodes.put(type, node);
            node.getConduitData().onCreated(type, level, pos, player);

            for (Direction direction : Direction.values()) {
                connections.get(direction).addType(value);
            }
        } else {
            types.add(type);
            nodes.put(type, node);
            if (types.size() != 1 || !IS_NEO_ENV_AFTER_ON_LOAD_CHANGE) {
                //NeoForge contains a patch that calls onLoad after the conduit has been placed if it's the first one, so onCreated would be called twice. it's easier to detect here
                //Forge does not have this patch
                node.getConduitData().onCreated(type, level, pos, player);
            }
        }

        onChanged();
        return new RightClickAction.Insert();
    }

    public void onLoad(Level level, BlockPos pos) {
        types.forEach(type -> onLoad(type, level, pos));
    }

    private <T extends ConduitData<T>> void onLoad(ConduitType<T> conduitType, Level level, BlockPos pos) {
        getNodeFor(conduitType).getConduitData().onCreated(conduitType, level, pos, null);
    }

    /**
     * @param type
     * @return if this bundle is empty and the block has to be removed
     * @throws IllegalArgumentException if this type is not in the conduitbundle and we are in dev env
     */
    public boolean removeType(Level level, ConduitType<?> type) {
        int index = types.indexOf(type);
        if (index == -1) {
            if (!FMLLoader.isProduction()) {
                throw new IllegalArgumentException(
                    "Conduit: " + EIOConduitTypes.REGISTRY.get().getKey(type) + " is not present in conduit bundle " + Arrays.toString(
                        types.stream().map(EIOConduitTypes.REGISTRY.get()::getKey).toArray()));
            }

            return types.isEmpty();
        }

        for (Direction direction : Direction.values()) {
            connections.get(direction).removeType(index);
        }

        if (EffectiveSide.get().isServer()) {
            removeNodeFor(level, type);
        }

        types.remove(index);
        onChanged();
        return types.isEmpty();
    }

    // endregion

    // region Connections

    public List<ConduitType<?>> getConnectedTypes(Direction direction) {
        return connections.get(direction).getConnectedTypes(this);
    }

    // Not a fan of this.
    @Deprecated(forRemoval = true)
    public ConnectionState getConnectionState(Direction direction, int index) {
        return connections.get(direction).getConnectionState(index);
    }

    public ConnectionState getConnectionState(Direction direction, ConduitType<?> conduitType) {
        return connections.get(direction).getConnectionState(getTypeIndex(conduitType));
    }

    public void setConnectionState(Direction direction, ConduitType<?> conduitType, ConnectionState state) {
        connections.get(direction).setConnectionState(getTypeIndex(conduitType), state);
        onChanged();
    }

    public boolean isConnectionEnd(Direction direction) {
        return connections.get(direction).isEnd();
    }

    public void removeType(Direction direction, ConduitType<?> conduitType) {
        connections.get(direction).removeType(getTypeIndex(conduitType));
        onChanged();
    }

    // Not a fan of this.
    @Deprecated(forRemoval = true)
    public void disableType(Direction direction, int index) {
        connections.get(direction).disableType(index);
        onChanged();
    }

    public void disableType(Direction direction, ConduitType<?> conduitType) {
        disableType(direction, getTypeIndex(conduitType));
    }

    public ItemStack getConnectionItem(Direction direction, int conduitIndex, SlotType slotType) {
        return connections.get(direction).getItem(slotType, conduitIndex);
    }

    public ItemStack getConnectionItem(Direction direction, ConduitType<?> conduitType, SlotType slotType) {
        return getConnectionItem(direction, getTypeIndex(conduitType), slotType);
    }

    public void setConnectionItem(Direction direction, int conduitIndex, SlotType slotType, ItemStack itemStack) {
        connections.get(direction).setItem(slotType, conduitIndex, itemStack);
        onChanged();
    }

    public void setConnectionItem(Direction direction, ConduitType<?> conduitType, SlotType slotType, ItemStack itemStack) {
        setConnectionItem(direction, getTypeIndex(conduitType), slotType, itemStack);
    }

    // endregion

    public List<ConduitType<?>> getTypes() {
        return types;
    }

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

    public void connectTo(Level level, BlockPos pos, Direction direction, ConduitType<?> type, boolean end) {
        connections.get(direction).connectTo(level, pos, getNodeFor(type), direction, type, getTypeIndex(type), end);
        onChanged();
    }

    public boolean disconnectFrom(Direction direction, ConduitType<?> type) {
        for (int i = 0; i < types.size(); i++) {
            if (type.getTicker().canConnectTo(type, types.get(i))) {
                connections.get(direction).tryDisconnect(i);
                onChanged();
                return true;
            }
        }
        return false;
    }

    @Nullable
    public ConduitGraphObject<?> getNodeForTypeExact(ConduitType<?> type) {
        return nodes.get(type);
    }

    public <T extends ConduitData<T>> ConduitGraphObject<T> getNodeFor(ConduitType<T> type) {
        for (var entry : nodes.entrySet()) {
            if (entry.getKey().getTicker().canConnectTo(entry.getKey(), type)) {
                //noinspection unchecked
                return (ConduitGraphObject<T>) nodes.get(entry.getKey());
            }
        }

        throw new IllegalStateException("no node matching original type");
    }

    public void setNodeFor(ConduitType<?> type, ConduitGraphObject<?> node) {
        nodes.put(type, node);
        for (var direction : Direction.values()) {
            ConduitConnection connection = connections.get(direction);
            int index = types.indexOf(type);
            if (index >= 0) {
                var state = connection.getConnectionState(index);
                if (state instanceof DynamicConnectionState dynamicState) {
                    ConduitBlockEntity.pushIOState(direction, node, dynamicState);
                }
            }
        }
    }

    public <T extends ConduitData<T>> void removeNodeFor(Level level, ConduitType<T> type) {
        var node = (ConduitGraphObject<T>) nodes.get(type);
        node.getConduitData().onRemoved(type, level, pos);
        if (node.getGraph() != null) {
            node.getGraph().remove(node);
        }

        nodes.remove(type);
    }

    public boolean hasType(ConduitType<?> type) {
        for (ConduitType<?> conduitType : types) {
            if (conduitType.getTicker().canConnectTo(conduitType, type)) {
                return true;
            }
        }
        return false;
    }

    public int getTypeIndex(ConduitType<?> type) {
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).getTicker().canConnectTo(types.get(i), type)) {
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

    @UseOnly(LogicalSide.CLIENT)
    public ConduitBundle deepCopy() {
        var bundle = new ConduitBundle(() -> {}, pos);
        bundle.types.addAll(types);
        connections.forEach((dir, connection) -> bundle.connections.put(dir, connection.deepCopy()));
        bundle.facadeTextures.putAll(facadeTextures);
        nodes.forEach((type, node) -> bundle.setNodeFor(type, node.deepCopy()));
        return bundle;
    }

    // region Serialization

    private static final String KEY_TYPES = "Types";
    private static final String KEY_CONNECTIONS = "Connections";
    private static final String KEY_FACADES = "Facades";
    private static final String KEY_NODE_TYPE = "NodeType";
    private static final String KEY_NODE_DATA = "NodeData";
    private static final String KEY_NODES = "Nodes";
    private static final String KEY_DATA = "ExtendedDataBackup";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (ConduitType<?> type : types) {
            listTag.add(StringTag.valueOf(EIOConduitTypes.REGISTRY.get().getKey(type).toString()));
        }
        tag.put(KEY_TYPES, listTag);
        CompoundTag connectionsTag = new CompoundTag();
        for (Direction dir : Direction.values()) {
            connectionsTag.put(dir.getName(), connections.get(dir).serializeNBT());
        }
        tag.put(KEY_CONNECTIONS, connectionsTag);
        CompoundTag facades = new CompoundTag();
        for (Map.Entry<Direction, BlockState> entry : facadeTextures.entrySet()) {
            Tag blockStateTag = BlockState.CODEC.encode(entry.getValue(), NbtOps.INSTANCE, new CompoundTag()).get().left().orElse(new CompoundTag());
            facades.put(entry.getKey().getName(), blockStateTag);
        }
        tag.put(KEY_FACADES, facades);
        if (EffectiveSide.get().isServer()) {
            ListTag nodeTag = new ListTag();
            for (var entry : nodes.entrySet()) {
                var data = entry.getValue().getConduitData().serializeNBT();
                if (!data.isEmpty()) {
                    CompoundTag dataTag = new CompoundTag();
                    dataTag.putString(KEY_NODE_TYPE, EIOConduitTypes.REGISTRY.get().getKey(entry.getKey()).toString());
                    dataTag.put(KEY_NODE_DATA, data);
                    nodeTag.add(dataTag);
                }
            }
            if (!nodeTag.isEmpty()) {
                tag.put(KEY_NODES, nodeTag);
            }
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        types.clear();
        ListTag typesTag = nbt.getList(KEY_TYPES, Tag.TAG_STRING);
        //this is used to shift connections back if a ConduitType was removed from
        List<Integer> invalidTypes = new ArrayList<>();
        for (int i = 0; i < typesTag.size(); i++) {
            StringTag stringTag = (StringTag) typesTag.get(i);
            ConduitType<?> type = EIOConduitTypes.REGISTRY.get().getValue(ResourceLocation.tryParse(stringTag.getAsString()));
            if (type == null) {
                invalidTypes.add(i);
                continue;
            }
            types.add(type);
        }
        CompoundTag connectionsTag = nbt.getCompound(KEY_CONNECTIONS);
        for (Direction dir : Direction.values()) {
            connections.get(dir).deserializeNBT(connectionsTag.getCompound(dir.getName()));
            for (Integer invalidType : invalidTypes) {
                connections.get(dir).removeType(invalidType);
            }
            //remove backwards to not shift list further
            for (int i = invalidTypes.size() - 1; i >= 0; i--) {
                connections.get(dir).removeType(invalidTypes.get(i));
            }
        }
        facadeTextures.clear();
        CompoundTag facades = nbt.getCompound(KEY_FACADES);
        for (Direction direction : Direction.values()) {
            if (facades.contains(direction.getName())) {
                facadeTextures.put(direction, BlockState.CODEC.decode(NbtOps.INSTANCE, facades.getCompound(direction.getName())).get().left().get().getFirst());
            }
        }
        for (Map.Entry<Direction, BlockState> entry : facadeTextures.entrySet()) {
            Tag blockStateTag = BlockState.CODEC.encode(entry.getValue(), NbtOps.INSTANCE, new CompoundTag()).get().left().orElse(new CompoundTag());
            facades.put(entry.getKey().getName(), blockStateTag);
        }
        nodes.entrySet().removeIf(entry -> !types.contains(entry.getKey()));
        if (EffectiveSide.get().isServer()) {
            for (ConduitType<?> type : types) {
                if (nodes.containsKey(type)) {
                    for (Direction direction : Direction.values()) {
                        if (connections.get(direction).getConnectionState(getTypeIndex(type)) instanceof DynamicConnectionState dyn) {
                            ConduitBlockEntity.pushIOState(direction, nodes.get(type), dyn);
                        }
                    }
                }
            }
        } else {
            types.forEach(type -> {
                createClientConduitGraphObject(pos, type);
            });
            if (nbt.contains(KEY_NODES)) {
                ListTag nodesTag = nbt.getList(KEY_NODES, Tag.TAG_COMPOUND);
                for (Tag tag : nodesTag) {
                    CompoundTag cmp = (CompoundTag) tag;
                    nodes
                        .get(EIOConduitTypes.REGISTRY.get().getValue(new ResourceLocation(cmp.getString(KEY_NODE_TYPE))))
                        .getConduitData()
                        .deserializeNBT(cmp.getCompound(KEY_NODE_DATA));
                }
            }
        }
    }

    private <T extends ConduitData<T>> void createClientConduitGraphObject(BlockPos pos, ConduitType<T> conduitType) {
        if (!nodes.containsKey(conduitType)) {
            nodes.put(conduitType, new ConduitGraphObject<>(pos, conduitType.createConduitData(ConduitClientSetup.getClientLevel(), pos)));
        }
    }

    // endregion

    // TODO: Clean this up
    private static final class ConduitConnection implements INBTSerializable<CompoundTag> {

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

        public void connectTo(Level level, BlockPos pos, ConduitGraphObject<?> conduitGraphObject, Direction direction, ConduitType<?> type, int typeIndex, boolean end) {
            if (end) {
                var state = DynamicConnectionState.defaultConnection(level, pos, direction, type);
                connectionStates[typeIndex] = state;
                ConduitBlockEntity.pushIOState(direction, conduitGraphObject, state);
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

        public List<ConduitType<?>> getConnectedTypes(ConduitBundle bundle) {
            List<ConduitType<?>> connected = new ArrayList<>();
            var types = bundle.getTypes();
            
            for (int i = 0; i < connectionStates.length; i++) {
                if (connectionStates[i].isConnection() && types.size() > i) {
                    connected.add(types.get(i));
                }
            }

            return connected;
        }

        public ConduitConnection deepCopy() {
            ConduitConnection connection = new ConduitConnection();
            //connectionstates are not mutable (enum/record), so reference is fine
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
            return Objects.hash((Object[]) connectionStates);
        }

        // region Serialization

        private static final String KEY_STATIC = "Static";
        private static final String KEY_INDEX = "Index";
        private static final String KEY_IS_EXTRACT = "IsExtract";
        private static final String KEY_EXTRACT = "Extract";
        private static final String KEY_IS_INSERT = "IsInsert";
        private static final String KEY_INSERT = "Insert";
        private static final String KEY_REDSTONE_CONTROL = "RedstoneControl";
        private static final String KEY_REDSTONE_CHANNEL = "Channel";
        private static final String KEY_INSERT_FILTER = "InsertFilter";
        private static final String KEY_EXTRACT_UPGRADE = "ExtractUpgrade";
        private static final String KEY_EXTRACT_FILTER = "ExtractFilter";

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            for (int i = 0; i < MAX_CONDUIT_TYPES; i++) {
                CompoundTag element = new CompoundTag();
                ConnectionState state = connectionStates[i];
                element.putBoolean(KEY_STATIC, state instanceof StaticConnectionStates);
                if (state instanceof StaticConnectionStates staticState) {
                    element.putInt(KEY_INDEX, staticState.ordinal());
                } else if (state instanceof DynamicConnectionState dynamicState) {
                    element.putBoolean(KEY_IS_EXTRACT, dynamicState.isExtract());
                    element.putInt(KEY_EXTRACT, dynamicState.extractChannel().ordinal());
                    element.putBoolean(KEY_IS_INSERT, dynamicState.isInsert());
                    element.putInt(KEY_INSERT, dynamicState.insertChannel().ordinal());
                    element.putInt(KEY_REDSTONE_CONTROL, dynamicState.control().ordinal());
                    element.putInt(KEY_REDSTONE_CHANNEL, dynamicState.redstoneChannel().ordinal());
                    element.put(KEY_INSERT_FILTER, dynamicState.filterInsert().serializeNBT());
                    element.put(KEY_EXTRACT_FILTER, dynamicState.filterExtract().serializeNBT());
                    element.put(KEY_EXTRACT_UPGRADE, dynamicState.upgradeExtract().serializeNBT());
                }
                tag.put(String.valueOf(i), element);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            for (int i = 0; i < MAX_CONDUIT_TYPES; i++) {
                CompoundTag nbt = tag.getCompound(String.valueOf(i));
                if (nbt.getBoolean(KEY_STATIC)) {
                    connectionStates[i] = StaticConnectionStates.values()[nbt.getInt(KEY_INDEX)];
                } else {
                    var isExtract = nbt.getBoolean(KEY_IS_EXTRACT);
                    var extractIndex = nbt.getInt(KEY_EXTRACT);
                    var isInsert = nbt.getBoolean(KEY_IS_INSERT);
                    var insertIndex = nbt.getInt(KEY_INSERT);
                    var redControl = nbt.getInt(KEY_REDSTONE_CONTROL);
                    var redChannel = nbt.getInt(KEY_REDSTONE_CHANNEL);

                    ItemStack insertFilter = nbt.contains(KEY_INSERT_FILTER, CompoundTag.TAG_COMPOUND)
                        ? ItemStack.of(nbt.getCompound(KEY_INSERT_FILTER))
                        : ItemStack.EMPTY;

                    ItemStack extractFilter = nbt.contains(KEY_EXTRACT_FILTER, CompoundTag.TAG_COMPOUND)
                        ? ItemStack.of(nbt.getCompound(KEY_EXTRACT_FILTER))
                        : ItemStack.EMPTY;

                    ItemStack extractUpgrade = nbt.contains(KEY_EXTRACT_UPGRADE, CompoundTag.TAG_COMPOUND)
                        ? ItemStack.of(nbt.getCompound(KEY_EXTRACT_UPGRADE))
                        : ItemStack.EMPTY;

                    connectionStates[i] = new DynamicConnectionState(
                        isInsert,
                        ColorControl.values()[insertIndex],
                        isExtract,
                        ColorControl.values()[extractIndex],
                        RedstoneControl.values()[redControl],
                        ColorControl.values()[redChannel],
                        insertFilter,
                        extractFilter,
                        extractUpgrade
                    );
                }
            }
        }

        // endregion
    }
}
