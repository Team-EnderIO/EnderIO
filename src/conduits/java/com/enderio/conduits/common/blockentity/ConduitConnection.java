package com.enderio.conduits.common.blockentity;

import com.enderio.core.common.blockentity.ColorControl;
import com.enderio.core.common.sync.EnderDataSlot;
import com.enderio.core.common.sync.IntegerDataSlot;
import com.enderio.core.common.sync.NullableEnumDataSlot;
import com.enderio.core.common.sync.SyncMode;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ConduitConnection {

    private final ConduitCore core;
    private final ConnectionState[] connectionStates = new ConnectionState[ConduitCore.MAX_CONDUIT_TYPES];

    public ConduitConnection(ConduitCore core) {
        this.core = core;
    }

    public void removeType(int index) {
        connectionStates[index] = null;
        for (int i = index+1; i < ConduitCore.MAX_CONDUIT_TYPES; i++) {
            connectionStates[i-1] = connectionStates[i];
        }
    }

    /**
     * @param dataSlots Add dataslots to this list
     */
    public void gatherDataSlots(List<EnderDataSlot<?>> dataSlots) {
        for (int i = 0; i < ConduitCore.MAX_CONDUIT_TYPES; i++) {
            int finalI = i;
            dataSlots.add(new NullableEnumDataSlot<>(
                () -> Optional.ofNullable(connectionStates[finalI]).map(ConnectionState::in).orElse(null),
                value -> connectionStates[finalI] = new ConnectionState(value, Optional.ofNullable(connectionStates[finalI]).map(ConnectionState::out).orElse(null)),
                ColorControl.class,
                SyncMode.WORLD)
            );
            dataSlots.add(new NullableEnumDataSlot<>(
                () -> Optional.ofNullable(connectionStates[finalI]).map(ConnectionState::out).orElse(null),
                value -> connectionStates[finalI] = new ConnectionState(Optional.ofNullable(connectionStates[finalI]).map(ConnectionState::in).orElse(null), value),
                ColorControl.class,
                SyncMode.WORLD)
            );
        }
    }

    private boolean isEnd() {
        return Arrays.stream(connectionStates).filter(Objects::nonNull).anyMatch(ConnectionState::isEnd);
    }

    private record ConnectionState(@Nullable ColorControl in, @Nullable ColorControl out) {
        public boolean isEnd() {
            return in != null || out != null;
        }
    }
}
