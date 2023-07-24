package com.enderio.conduits.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.conduits.client.ConduitClientSetup;
import com.enderio.conduits.common.blockentity.connection.DynamicConnectionState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ConduitBundle implements INBTSerializable<CompoundTag> {

    //Do not change this value unless you fix the OffsetHelper
    public static final int MAX_CONDUIT_TYPES = 9;

    private final Map<Direction, ConduitConnection> connections = new EnumMap<>(Direction.class);

    private final List<IConduitType<?>> types = new ArrayList<>();

    //fill back after world save
    private final Map<IConduitType<?>, NodeIdentifier<?>> nodes = new HashMap<>();
    private final Runnable scheduleSync;
    private final BlockPos pos;

    private final Map<Direction, BlockState> facadeTextures = new EnumMap<>(Direction.class);

    public ConduitBundle(Runnable scheduleSync, BlockPos pos) {
        this.scheduleSync = scheduleSync;
        for (Direction value : Direction.values()) {
            connections.put(value, new ConduitConnection(this));
        }
        this.pos = pos;
    }

    /**
     * @param type
     * @return an action containing the type that is now not in this bundle
     */
    public RightClickAction addType(Level level, IConduitType<?> type, Player player) {
        if (types.size() == MAX_CONDUIT_TYPES)
            return new RightClickAction.Blocked();
        if (types.contains(type))
            return new RightClickAction.Blocked();
        //upgrade a conduit
        Optional<? extends IConduitType<?>> first = types.stream().filter(existingConduit -> existingConduit.canBeReplacedBy(type)).findFirst();
        NodeIdentifier<?> node = new NodeIdentifier<>(pos, type.createExtendedConduitData(level, pos));
        if (first.isPresent()) {
            int index = types.indexOf(first.get());
            types.set(index, type);
            var prevNode = nodes.remove(first.get());
            nodes.put(type, node);
            if (prevNode != null) {
                prevNode.getExtendedConduitData().onRemoved(type, level, pos);
                if (!level.isClientSide() && prevNode.getGraph() != null) {
                    prevNode.getGraph().remove(prevNode);
                }
            }
            node.getExtendedConduitData().onCreated(type, level, pos, player);
            connections.values().forEach(connection -> connection.disconnectType(index));
            scheduleSync.run();
            return new RightClickAction.Upgrade(first.get());
        }
        //some conduit says no (like higher energy conduit)
        if (types.stream().anyMatch(existingConduit -> !existingConduit.canBeInSameBlock(type)))
            return new RightClickAction.Blocked();
        //sort the list, so order is consistent
        int id = ConduitTypeSorter.getSortIndex(type);
        var addBefore = types.stream().filter(existing -> ConduitTypeSorter.getSortIndex(existing) > id).findFirst();
        if (addBefore.isPresent()) {
            var value = types.indexOf(addBefore.get());
            types.add(value, type);
            nodes.put(type, node);
            node.getExtendedConduitData().onCreated(type, level, pos, player);
            for (Direction direction : Direction.values()) {
                connections.get(direction).addType(value);
            }
        } else {
            types.add(type);
            nodes.put(type, node);
            node.getExtendedConduitData().onCreated(type, level, pos, player);
        }
        scheduleSync.run();
        return new RightClickAction.Insert();
    }

    void onLoad(Level level, BlockPos pos) {
        for (IConduitType<?> type : types) {
            getNodeFor(type).getExtendedConduitData().onCreated(type, level, pos, null);
        }
    }

    /**
     * @param type
     * @return if this bundle is empty and the block has to be removed
     * @throws IllegalArgumentException if this type is not in the conduitbundle and we are in dev env
     */
    public boolean removeType(Level level, IConduitType<?> type) {
        int index = types.indexOf(type);
        if (index == -1) {
            if (!FMLLoader.isProduction()) {
                throw new IllegalArgumentException(
                    "Conduit: " + ConduitTypes.REGISTRY.get().getKey(type) + " is not present in conduit bundle " + Arrays.toString(
                        types.stream().map(existingType -> ConduitTypes.REGISTRY.get().getKey(existingType)).toArray()));
            }
            return types.isEmpty();
        }
        for (Direction direction : Direction.values()) {
            connections.get(direction).removeType(index);
        }
        if (EffectiveSide.get().isServer())
            removeNodeFor(level, type);
        types.remove(index);
        scheduleSync.run();
        return types.isEmpty();
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
        for (IConduitType<?> type : types) {
            listTag.add(StringTag.valueOf(ConduitTypes.getRegistry().getKey(type).toString()));
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
                var data = entry.getValue().getExtendedConduitData().serializeRenderNBT();
                if (!data.isEmpty()) {
                    CompoundTag dataTag = new CompoundTag();
                    dataTag.putString(KEY_NODE_TYPE, ConduitTypes.getRegistry().getKey(entry.getKey()).toString());
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

    public CompoundTag serializeGuiNBT() {
        CompoundTag nbt = new CompoundTag();
        for (IConduitType<?> type : getTypes()) {
            CompoundTag compoundTag = nodes.get(type).getExtendedConduitData().serializeGuiNBT();
            if (!compoundTag.isEmpty()) {
                nbt.put(ConduitTypes.getRegistry().getKey(type).toString(), compoundTag);
            }
        }
        return nbt;
    }

    public void deserializeGuiNBT(CompoundTag nbt) {
        for (IConduitType<?> type : getTypes()) {
            if (nbt.contains(ConduitTypes.getRegistry().getKey(type).toString())) {
                nodes.get(type).getExtendedConduitData().deserializeNBT(nbt.getCompound(ConduitTypes.getRegistry().getKey(type).toString()));
            }
        }
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        types.clear();
        ListTag typesTag = nbt.getList(KEY_TYPES, Tag.TAG_STRING);
        //this is used to shift connections back if a ConduitType was removed from
        List<Integer> invalidTypes = new ArrayList<>();
        for (int i = 0; i < typesTag.size(); i++) {
            StringTag stringTag = (StringTag) typesTag.get(i);
            IConduitType<?> type = ConduitTypes.getRegistry().getValue(ResourceLocation.tryParse(stringTag.getAsString()));
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
            for (IConduitType<?> type : types) {
                if (nodes.containsKey(type)) {
                    for (Direction direction : Direction.values()) {
                        if (getConnection(direction).getConnectionState(type) instanceof DynamicConnectionState dyn) {
                            ConduitBlockEntity.pushIOState(direction, nodes.get(type), dyn);
                        }
                    }
                }
            }
        } else {
            types.forEach(type -> {
                if (!nodes.containsKey(type))
                    nodes.put(type, new NodeIdentifier<>(pos, type.createExtendedConduitData(ConduitClientSetup.getClientLevel(), pos)));
            });
            if (nbt.contains(KEY_NODES)) {
                ListTag nodesTag = nbt.getList(KEY_NODES, Tag.TAG_COMPOUND);
                for (Tag tag : nodesTag) {
                    CompoundTag cmp = (CompoundTag) tag;
                    nodes
                        .get(ConduitTypes.getRegistry().getValue(new ResourceLocation(cmp.getString(KEY_NODE_TYPE))))
                        .getExtendedConduitData()
                        .deserializeNBT(cmp.getCompound(KEY_NODE_DATA));
                }
            }
        }
    }

    // endregion

    public ConduitConnection getConnection(Direction direction) {
        return connections.get(direction);
    }

    public List<IConduitType<?>> getTypes() {
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
    }

    public void connectTo(Direction direction, IConduitType<?> type, boolean end) {
        getConnection(direction).connectTo(getNodeFor(type), direction, type, getTypeIndex(type), end);
        scheduleSync.run();
    }

    public boolean disconnectFrom(Direction direction, IConduitType<?> type) {
        for (int i = 0; i < types.size(); i++) {
            if (type.getTicker().canConnectTo(type, types.get(i))) {
                getConnection(direction).tryDisconnect(i);
                scheduleSync.run();
                return true;
            }
        }
        return false;
    }

    @Nullable
    public NodeIdentifier<?> getNodeForTypeExact(IConduitType<?> type) {
        return nodes.get(type);
    }

    public NodeIdentifier<?> getNodeFor(IConduitType<?> type) {
        for (var entry : nodes.entrySet()) {
            if (entry.getKey().getTicker().canConnectTo(entry.getKey(), type))
                return nodes.get(entry.getKey());
        }
        throw new IllegalStateException("no node matching original type");
    }

    public void setNodeFor(IConduitType<?> type, NodeIdentifier<?> node) {
        nodes.put(type, node);
        for (var direction : Direction.values()) {
            ConduitConnection connection = connections.get(direction);
            int index = connection.getConnectedTypes().indexOf(type);
            if (index >= 0) {
                var state = connection.getConnectionState(index);
                if (state instanceof DynamicConnectionState dynamicState) {
                    ConduitBlockEntity.pushIOState(direction, node, dynamicState);
                }
            }
        }
    }

    public void removeNodeFor(Level level, IConduitType<?> type) {
        NodeIdentifier<?> node = nodes.get(type);
        node.getExtendedConduitData().onRemoved(type, level, pos);
        if (node.getGraph() != null) {
            node.getGraph().remove(node);
        }
        nodes.remove(type);
    }

    public boolean hasType(IConduitType<?> type) {
        for (IConduitType<?> iConduitType : types) {
            if (iConduitType.getTicker().canConnectTo(iConduitType, type)) {
                return true;
            }
        }
        return false;
    }

    public int getTypeIndex(IConduitType<?> type) {
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).getTicker().canConnectTo(types.get(i), type)) {
                return i;
            }
        }
        throw new IllegalStateException("no conduit matching type in bundle");
    }

    @UseOnly(LogicalSide.CLIENT)
    public ConduitBundle deepCopy() {
        var bundle = new ConduitBundle(() -> {}, pos);
        bundle.types.addAll(types);
        connections.forEach((dir, connection) -> bundle.connections.put(dir, connection.deepCopy(bundle)));
        bundle.facadeTextures.putAll(facadeTextures);
        nodes.forEach((type, node) -> bundle.setNodeFor(type, new NodeIdentifier<>(node.getPos(), node.getExtendedConduitData().deepCopy())));
        return bundle;
    }
}
