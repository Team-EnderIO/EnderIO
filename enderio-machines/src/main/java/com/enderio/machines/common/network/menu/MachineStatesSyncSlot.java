package com.enderio.machines.common.network.menu;

import com.enderio.core.common.network.menu.SyncSlot;
import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.ListSlotPayload;
import com.enderio.core.common.network.menu.payload.PairSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayloadType;
import com.enderio.core.common.network.menu.payload.StringSlotPayload;
import com.enderio.machines.common.blocks.base.state.MachineState;
import com.enderio.machines.common.blocks.base.state.MachineStateType;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public abstract class MachineStatesSyncSlot implements SyncSlot {

    public static MachineStatesSyncSlot standalone() {
        return new MachineStatesSyncSlot() {
            private Set<MachineState> value = Set.of();

            @Override
            public Set<MachineState> get() {
                return value;
            }

            @Override
            public void set(Set<MachineState> value) {
                this.value = value;
            }
        };
    }

    public static MachineStatesSyncSlot simple(Supplier<Set<MachineState>> getter, Consumer<Set<MachineState>> setter) {
        return new MachineStatesSyncSlot() {

            @Override
            public Set<MachineState> get() {
                return getter.get();
            }

            @Override
            public void set(Set<MachineState> value) {
                setter.accept(value);
            }
        };
    }

    public static MachineStatesSyncSlot readOnly(Supplier<Set<MachineState>> getter) {
        return new MachineStatesSyncSlot() {

            @Override
            public Set<MachineState> get() {
                return getter.get();
            }

            @Override
            public void set(Set<MachineState> value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private int previousHash;

    public abstract Set<MachineState> get();

    public abstract void set(Set<MachineState> value);

    @Override
    public ChangeType detectChanges() {
        int currentHash = get().hashCode();
        var changeType = currentHash != previousHash ? ChangeType.FULL : ChangeType.NONE;
        previousHash = currentHash;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        return new ListSlotPayload(get().stream()
                .map(s -> new PairSlotPayload(new IntSlotPayload(s.type().ordinal()),
                        new StringSlotPayload(s.component().getString())))
                .collect(Collectors.toUnmodifiableList()));
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        var states = new HashSet<MachineState>();

        // Gross... Maybe use a registry someday for these :)
        MachineStateType[] machineStateTypes = MachineStateType.values();
        if (payload instanceof ListSlotPayload list) {
            for (var itemPayload : list.contents()) {
                if (itemPayload instanceof PairSlotPayload pair) {
                    if (pair.left().type() == SlotPayloadType.INT && pair.right().type() == SlotPayloadType.STRING) {
                        int machineStateTypeOrdinal = ((IntSlotPayload) pair.left()).value();

                        if (machineStateTypeOrdinal >= 0 && machineStateTypeOrdinal < machineStateTypes.length) {
                            states.add(new MachineState(machineStateTypes[machineStateTypeOrdinal],
                                    Component.translatable(((StringSlotPayload) pair.right()).value())));
                        }
                    }
                }
            }
        }

        set(states);
    }
}
