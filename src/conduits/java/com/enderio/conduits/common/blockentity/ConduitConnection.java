package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.conduit.connection.ConnectionState;
import com.enderio.api.conduit.connection.DynamicConnectionState;
import com.enderio.api.conduit.connection.StaticConnectionStates;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.enderio.conduits.common.blockentity.ConduitBundle.MAX_CONDUIT_TYPES;

public final class ConduitConnection {

    public static Codec<ConduitConnection> CODEC =
        ConnectionState.CODEC.listOf(0, MAX_CONDUIT_TYPES)
            .xmap(ConduitConnection::new, i -> Arrays.stream(i.connectionStates).toList());

    public static StreamCodec<RegistryFriendlyByteBuf, ConduitConnection> STREAM_CODEC =
        ConnectionState.STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(ConduitConnection::new, i -> Arrays.stream(i.connectionStates).toList());

    // TODO: Change to Map<ConduitType<?>, ConnectionState> to lower dependency on ConduitBundle.
    private final ConnectionState[] connectionStates = Util.make(() -> {
        var states = new ConnectionState[MAX_CONDUIT_TYPES];
        Arrays.fill(states, StaticConnectionStates.DISCONNECTED);
        return states;
    });

    public ConduitConnection() {
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

    public void connectTo(Level level, BlockPos pos, NodeIdentifier<?> nodeIdentifier, Direction direction, ConduitType<?> type, int typeIndex, boolean end) {
        if (end) {
            var state = DynamicConnectionState.defaultConnection(level, pos, direction, type);
            connectionStates[typeIndex] = state;
            ConduitBlockEntity.pushIOState(direction, nodeIdentifier, state);
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
        for (int i = 0; i < connectionStates.length; i++) {
            if (connectionStates[i].isConnection()) {
                connected.add(bundle.getTypes().get(i));
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

    public ConnectionState getConnectionState(ConduitBundle bundle, ConduitType<?> type) {
        return connectionStates[bundle.getTypeIndex(type)];
    }

    public void setConnectionState(ConduitBundle bundle, ConduitType<?> type, ConnectionState state) {
        setConnectionState(bundle.getTypeIndex(type), state);
    }

    private void setConnectionState(int i, ConnectionState state) {
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
}
