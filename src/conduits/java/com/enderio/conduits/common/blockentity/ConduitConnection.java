package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.IConduitType;
import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.conduits.common.blockentity.connection.DynamicConnectionState;
import com.enderio.conduits.common.blockentity.connection.IConnectionState;
import com.enderio.conduits.common.blockentity.connection.StaticConnectionStates;
import com.enderio.core.common.blockentity.ColorControl;
import com.enderio.core.common.sync.EnderDataSlot;
import com.enderio.core.common.sync.SyncMode;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
    public void connectTo(int typeIndex, boolean end) {
        if (end)
            connectionStates[typeIndex] = DynamicConnectionState.random();
        else
            connectionStates[typeIndex] = StaticConnectionStates.CONNECTED;
    }

    public void disconnectFrom(int typeIndex) {
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
        return Arrays.stream(connectionStates).anyMatch(con -> con instanceof DynamicConnectionState);
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
                element.putInt("in", dynamicState.in() != null ? dynamicState.in().ordinal() : -1);
                element.putInt("out", dynamicState.out() != null ? dynamicState.out().ordinal() : -1);
                element.putInt("redControl", dynamicState.control().ordinal());
                element.putInt("redChannel", dynamicState.redstoneChannel().ordinal());
                //TODO: Save Item
            } else {
                throw new RuntimeException("Sealed Interface was none of it's sealed types");
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
                var inIndex = nbt.getInt("in");
                var outIndex = nbt.getInt("out");
                var redControl = nbt.getInt("redControl");
                var redChannel = nbt.getInt("redChannel");
                connectionStates[i] = new DynamicConnectionState(
                    inIndex != -1 ? ColorControl.values()[inIndex] : null,
                    outIndex != -1 ? ColorControl.values()[outIndex] : null,
                    RedstoneControl.values()[redControl],
                    ColorControl.values()[redChannel],
                    Items.AIR.getDefaultInstance()
                );
            }
        }
    }

    public ConduitConnection deepCopy() {
        var connection = new ConduitConnection();
        //connectionstates are not mutable (enum/record), so reference is fine
        System.arraycopy(connectionStates, 0, connection.connectionStates, 0, MAX_CONDUIT_TYPES);
        return connection;
    }

    public IConnectionState getConnectionState(int index) {
        return connectionStates[index];
    }
}
