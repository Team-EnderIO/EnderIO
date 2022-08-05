package com.enderio.conduits.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.core.common.blockentity.ColorControl;
import com.enderio.core.common.sync.EnderDataSlot;
import com.enderio.core.common.sync.SyncMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConduitConnection {

    private final ConduitBundle bundle;
    private final ConnectionState[] connectionStates = new ConnectionState[ConduitBundle.MAX_CONDUIT_TYPES];

    public ConduitConnection(ConduitBundle bundle) {
        this.bundle = bundle;
    }

    /**
     * shift all behind that one to the back and set that index to null
     * @param index
     */
    public void addType(int index) {
        for (int i = index; i < ConduitBundle.MAX_CONDUIT_TYPES; i++) {
            connectionStates[i+1] = connectionStates[i];
        }
        connectionStates[index] = null;
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

    private boolean isEnd() {
        return Arrays.stream(connectionStates).filter(Objects::nonNull).anyMatch(ConnectionState::isEnd);
    }

    private record ConnectionState(@Nullable ColorControl in, @Nullable ColorControl out, RedstoneControl control, @Nullable ColorControl redstoneChannel, @UseOnly(LogicalSide.SERVER) ItemStack filter) {
        public boolean isEnd() {
            return in != null || out != null;
        }
    }

    /**
     * filter is not synced, because that will be synced using the container
     */
    private class ConnectionStateDataSlot extends EnderDataSlot<ConnectionState> {

        public ConnectionStateDataSlot(Supplier<ConnectionState> getter, Consumer<ConnectionState> setter) {
            super(getter, setter, SyncMode.WORLD);
        }

        @Override
        public CompoundTag toFullNBT() {
            CompoundTag tag = new CompoundTag();
            var state = getter().get();
            if (state != null) {
                tag.putInt("in",state.in() != null ? state.in().ordinal() : -1);
                tag.putInt("out",state.out() != null ? state.out().ordinal() : -1);
                tag.putInt("redControl",state.control().ordinal());
                tag.putInt("redChannel",state.redstoneChannel() != null ? state.redstoneChannel().ordinal() : -1);
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
}
