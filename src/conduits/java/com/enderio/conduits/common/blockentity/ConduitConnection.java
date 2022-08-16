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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConduitConnection {

    private final IConnectionState[] connectionStates = Util.make(() -> {
        var states = new IConnectionState[ConduitBundle.MAX_CONDUIT_TYPES];
        Arrays.fill(states, StaticConnectionStates.DISCONNECTED);
        return states;
    });

    /**
     * shift all behind that one to the back and set that index to null
     * @param index
     */
    public void addType(int index) {
        for (int i = ConduitBundle.MAX_CONDUIT_TYPES-1; i > index; i--) {
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
            connectionStates[typeIndex] = DynamicConnectionState.ofInput();
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
        for (int i = index+1; i < ConduitBundle.MAX_CONDUIT_TYPES; i++) {
            connectionStates[i-1] = connectionStates[i];
        }
    }

    public void clearType(int index) {
        connectionStates[index] = StaticConnectionStates.DISCONNECTED;
    }

    /**
     * @param dataSlots Add dataslots to this list
     */
    public void gatherDataSlots(List<EnderDataSlot<?>> dataSlots) {
        for (int i = 0; i < ConduitBundle.MAX_CONDUIT_TYPES; i++) {
            int finalI = i;
            dataSlots.add(new ConnectionStateDataSlot(
                () -> connectionStates[finalI],
                state -> connectionStates[finalI] = state
            ));
        }
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

    /**
     * filter is not synced, because that will be synced using the container
     */
    public static class ConnectionStateDataSlot extends EnderDataSlot<IConnectionState> {

        public ConnectionStateDataSlot(Supplier<IConnectionState> getter, Consumer<IConnectionState> setter) {
            super(getter, setter, SyncMode.WORLD);
        }

        @Override
        public CompoundTag toFullNBT() {
            CompoundTag tag = new CompoundTag();
            var state = getter().get();
            tag.putBoolean("static", state instanceof StaticConnectionStates);
            if (state instanceof StaticConnectionStates staticState) {
                tag.putInt("index", staticState.ordinal());
            } else if (state instanceof DynamicConnectionState dynamicState) {
                tag.putInt("in", dynamicState.in() != null ? dynamicState.in().ordinal() : -1);
                tag.putInt("out", dynamicState.out() != null ? dynamicState.out().ordinal() : -1);
                tag.putInt("redControl", dynamicState.control().ordinal());
                tag.putInt("redChannel", dynamicState.redstoneChannel().ordinal());
            } else {
                throw new RuntimeException("Sealed Interface was none of it's sealed types");
            }
            return tag;
        }

        @Override
        protected IConnectionState fromNBT(CompoundTag nbt) {
            if (nbt.getBoolean("static")) {
                return StaticConnectionStates.values()[nbt.getInt("index")];
            } else {
                var inIndex = nbt.getInt("in");
                var outIndex = nbt.getInt("out");
                var redControl = nbt.getInt("redControl");
                var redChannel = nbt.getInt("redChannel");
                return new DynamicConnectionState(
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
        System.arraycopy(connectionStates, 0, connection.connectionStates, 0, ConduitBundle.MAX_CONDUIT_TYPES);
        return connection;
    }
}
