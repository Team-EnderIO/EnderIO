package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.conduits.common.blockentity.connection.DynamicConnectionState;
import com.enderio.conduits.common.blockentity.connection.IConnectionState;
import com.enderio.conduits.common.blockentity.connection.StaticConnectionStates;
import com.enderio.api.misc.ColorControl;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.enderio.conduits.common.blockentity.ConduitBundle.MAX_CONDUIT_TYPES;

public class ConduitConnection implements INBTSerializable<CompoundTag> {

    private final IConnectionState[] connectionStates = Util.make(() -> {
        var states = new IConnectionState[MAX_CONDUIT_TYPES];
        Arrays.fill(states, StaticConnectionStates.DISCONNECTED);
        return states;
    });

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

    /**
     * I know this method isn't perfect, but will do that once more conduits exist
     * @param typeIndex
     * @param end
     */
    public void connectTo(NodeIdentifier nodeIdentifier, Direction direction, int typeIndex, boolean end) {
        if (end) {
            var state = DynamicConnectionState.defaultConnection();
            connectionStates[typeIndex] = state;
            nodeIdentifier.pushState(direction, state.isExtract() ? state.extract() : null, state.isInsert() ? state.insert() : null);
        } else {
            connectionStates[typeIndex] = StaticConnectionStates.CONNECTED;
        }
    }

    public void disconnectFrom(int typeIndex) {
        if (connectionStates[typeIndex] != StaticConnectionStates.DISABLED)
            connectionStates[typeIndex] = StaticConnectionStates.DISCONNECTED;
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

    public void clearType(int index) {
        connectionStates[index] = StaticConnectionStates.DISCONNECTED;
    }

    public boolean isEnd() {
        return Arrays.stream(connectionStates).anyMatch(DynamicConnectionState.class::isInstance);
    }

    public List<IConduitType> getConnectedTypes(ConduitBundle on) {
        List<IConduitType> connected = new ArrayList<>();
        for (int i = 0; i < connectionStates.length; i++) {
            if (connectionStates[i].isConnection()) {
                connected.add(on.getTypes().get(i));
            }
        }
        return connected;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < MAX_CONDUIT_TYPES; i++) {
            CompoundTag element = new CompoundTag();
            IConnectionState state = connectionStates[i];
            element.putBoolean("static", state instanceof StaticConnectionStates);
            if (state instanceof StaticConnectionStates staticState) {
                element.putInt("index", staticState.ordinal());
            } else if (state instanceof DynamicConnectionState dynamicState) {
                element.putBoolean("isExtract", dynamicState.isExtract());
                element.putInt("extract", dynamicState.extract().ordinal());
                element.putBoolean("isInsert", dynamicState.isInsert());
                element.putInt("insert", dynamicState.insert().ordinal());
                element.putInt("redControl", dynamicState.control().ordinal());
                element.putInt("redChannel", dynamicState.redstoneChannel().ordinal());
            }
            tag.put(i + "", element);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        for (int i = 0; i < MAX_CONDUIT_TYPES; i++) {
            CompoundTag nbt = tag.getCompound(i + "");
            if (nbt.getBoolean("static")) {
                connectionStates[i] = StaticConnectionStates.values()[nbt.getInt("index")];
            } else {
                var isInsert = nbt.getBoolean("isInsert");
                var insertIndex = nbt.getInt("insert");
                var isExtract = nbt.getBoolean("isExtract");
                var extractIndex = nbt.getInt("extract");
                var redControl = nbt.getInt("redControl");
                var redChannel = nbt.getInt("redChannel");
                IConnectionState prev = connectionStates[i];
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

    public ConduitConnection deepCopy() {
        ConduitConnection connection = new ConduitConnection();
        //connectionstates are not mutable (enum/record), so reference is fine
        System.arraycopy(connectionStates, 0, connection.connectionStates, 0, MAX_CONDUIT_TYPES);
        return connection;
    }

    public IConnectionState getConnectionState(int index) {
        return connectionStates[index];
    }
    public IConnectionState getConnectionState(IConduitType type, ConduitBundle on) {
        return connectionStates[on.getTypes().indexOf(type)];
    }
    public void setConnectionState(IConduitType type, ConduitBundle on, IConnectionState state) {
        connectionStates[on.getTypes().indexOf(type)] = state;
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
