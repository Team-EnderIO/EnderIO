package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.SlotType;
import com.enderio.conduits.common.conduit.ConduitGraphObject;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.conduit.connection.StaticConnectionStates;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.enderio.conduits.common.blockentity.ConduitBundle.MAX_CONDUIT_TYPES;

public class ConduitConnection implements INBTSerializable<CompoundTag> {

    private final ConnectionState[] connectionStates = Util.make(() -> {
        var states = new ConnectionState[MAX_CONDUIT_TYPES];
        Arrays.fill(states, StaticConnectionStates.DISCONNECTED);
        return states;
    });

    private final ConduitBundle on;

    public ConduitConnection(ConduitBundle on) {
        this.on = on;
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
        on.incrementDataVersion();
    }

    public boolean isEnd() {
        return Arrays.stream(connectionStates).anyMatch(DynamicConnectionState.class::isInstance);
    }

    public List<ConduitType<?>> getConnectedTypes() {
        List<ConduitType<?>> connected = new ArrayList<>();
        for (int i = 0; i < connectionStates.length; i++) {
            if (connectionStates[i].isConnection()) {
                connected.add(on.getTypes().get(i));
            }
        }
        return connected;
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
                element.putInt(KEY_EXTRACT, dynamicState.extract().ordinal());
                element.putBoolean(KEY_IS_INSERT, dynamicState.isInsert());
                element.putInt(KEY_INSERT, dynamicState.insert().ordinal());
                element.putInt(KEY_REDSTONE_CONTROL, dynamicState.control().ordinal());
                element.putInt(KEY_REDSTONE_CHANNEL, dynamicState.redstoneChannel().ordinal());
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
                ConnectionState prev = connectionStates[i];
                Optional<DynamicConnectionState> dyn = Optional.ofNullable(prev instanceof DynamicConnectionState dynState ? dynState : null);
                connectionStates[i] = new DynamicConnectionState(
                    isInsert,
                     ColorControl.values()[insertIndex],
                    isExtract,
                    ColorControl.values()[extractIndex],
                    RedstoneControl.values()[redControl],
                    ColorControl.values()[redChannel],
                    dyn.map(DynamicConnectionState::filterInsert).orElse(ItemStack.EMPTY),
                    dyn.map(DynamicConnectionState::filterExtract).orElse(ItemStack.EMPTY),
                    dyn.map(DynamicConnectionState::upgradeExtract).orElse(ItemStack.EMPTY)
                );
            }
        }
    }

    // endregion

    public ConduitConnection deepCopy(ConduitBundle on) {
        ConduitConnection connection = new ConduitConnection(on);
        //connectionstates are not mutable (enum/record), so reference is fine
        System.arraycopy(connectionStates, 0, connection.connectionStates, 0, MAX_CONDUIT_TYPES);
        return connection;
    }

    public ConnectionState getConnectionState(int index) {
        return connectionStates[index];
    }
    public ConnectionState getConnectionState(ConduitType<?> type) {
        return connectionStates[on.getTypeIndex(type)];
    }
    public void setConnectionState(ConduitType<?> type, ConnectionState state) {
        setConnectionState(on.getTypeIndex(type),state);
        on.incrementDataVersion();
    }
    private void setConnectionState(int i, ConnectionState state) {
        connectionStates[i] = state;
        on.incrementDataVersion();
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
}
