package com.enderio.conduits.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.IConduitType;
import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.core.common.blockentity.ColorControl;
import com.enderio.core.common.sync.EnderDataSlot;
import com.enderio.core.common.sync.SyncMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConduitConnection {

    private final ConnectionState[] connectionStates = new ConnectionState[ConduitBundle.MAX_CONDUIT_TYPES];

    /**
     * shift all behind that one to the back and set that index to null
     * @param index
     */
    public void addType(int index) {
        for (int i = ConduitBundle.MAX_CONDUIT_TYPES-1; i > index; i--) {
            connectionStates[i] = connectionStates[i-1];
        }
        connectionStates[index] = null;
    }

    /**
     * I know this method isn't perfect, but will do that once more conduits exist
     * @param typeIndex
     * @param end
     */
    public void connectTo(int typeIndex, boolean end) {
        if (end)
            connectionStates[typeIndex] = new ConnectionState(ColorControl.BLUE, null, RedstoneControl.ALWAYS_ACTIVE, null, ItemStack.EMPTY);
        else
            connectionStates[typeIndex] = new ConnectionState(null, null, RedstoneControl.ALWAYS_ACTIVE, null, ItemStack.EMPTY);
    }

    /**
     * remove entry and shift all behind one to the front
     * @param index
     */
    public void removeType(int index) {
        connectionStates[index] = null;
        for (int i = index+1; i < ConduitBundle.MAX_CONDUIT_TYPES; i++) {
            connectionStates[i-1] = connectionStates[i];
        }
    }

    public void clearType(int index) {
        connectionStates[index] = null;
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
        return Arrays.stream(connectionStates).filter(Objects::nonNull).anyMatch(ConnectionState::isEnd);
    }

    public List<IConduitType> getConnectedTypes(ConduitBundle on) {
        List<IConduitType> connected = new ArrayList<>();
        for (int i = 0; i < connectionStates.length; i++) {
            if (connectionStates[i] != null) {
                connected.add(on.getTypes().get(i));
            }
        }
        return connected;
    }

    private record ConnectionState(@Nullable ColorControl in, @Nullable ColorControl out, RedstoneControl control, @Nullable ColorControl redstoneChannel, @UseOnly(LogicalSide.SERVER) ItemStack filter) {
        public boolean isEnd() {
            return in != null || out != null;
        }
    }

    /**
     * filter is not synced, because that will be synced using the container
     */
    public static class ConnectionStateDataSlot extends EnderDataSlot<ConnectionState> {

        public ConnectionStateDataSlot(Supplier<ConnectionState> getter, Consumer<ConnectionState> setter) {
            super(getter, setter, SyncMode.WORLD);
        }

        @Override
        public CompoundTag toFullNBT() {
            CompoundTag tag = new CompoundTag();
            var state = getter().get();
            if (state != null) {
                tag.putInt("in", state.in() != null ? state.in().ordinal() : -1);
                tag.putInt("out", state.out() != null ? state.out().ordinal() : -1);
                tag.putInt("redControl", state.control().ordinal());
                tag.putInt("redChannel", state.redstoneChannel() != null ? state.redstoneChannel().ordinal() : -1);
            }
            return tag;
        }

        @Override
        @Nullable
        protected ConnectionState fromNBT(CompoundTag nbt) {
            if (!nbt.contains("in") || !nbt.contains("out"))
                return null;
            var inIndex = nbt.getInt("in");
            var outIndex = nbt.getInt("out");
            var redControl = nbt.getInt("redControl");
            var redChannel = nbt.getInt("redChannel");
            return new ConnectionState(
                inIndex != -1 ? ColorControl.values()[inIndex] : null,
                outIndex != -1 ? ColorControl.values()[outIndex] : null,
                RedstoneControl.values()[redControl],
                redChannel != -1 ? ColorControl.values()[redChannel] : null,
                Items.AIR.getDefaultInstance()
            );
        }
    }

    public ConduitConnection deepCopy() {
        var connection = new ConduitConnection();
        //connectionstate is a record, so not mutable, so reference is fine
        System.arraycopy(connectionStates, 0, connection.connectionStates, 0, ConduitBundle.MAX_CONDUIT_TYPES);
        return connection;
    }
}
