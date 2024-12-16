package com.enderio.machines.common.network.menu;

import com.enderio.core.common.network.menu.SyncSlot;
import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.ListSlotPayload;
import com.enderio.core.common.network.menu.payload.PairSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayloadType;
import com.enderio.core.common.network.menu.payload.StringSlotPayload;
import com.enderio.machines.common.blockentity.MachineState;
import com.enderio.machines.common.blockentity.MachineStateType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MachineStatesSyncSlot implements SyncSlot {

    private int previousHash;

    public abstract Set<MachineState> get();
    public abstract void set(Set<MachineState> values);

    @Override
    public ChangeType detectChanges() {
        int currentHash = get().hashCode();
        var changeType = currentHash != previousHash ? ChangeType.FULL : ChangeType.NONE;
        previousHash = currentHash;
        return changeType;
    }

    @Override
    public SlotPayload getPayload(RegistryAccess registryAccess, ChangeType changeType) {
        return new ListSlotPayload(
            get().stream().map(s -> new PairSlotPayload(
                new IntSlotPayload(s.type().ordinal()),
                new StringSlotPayload(s.component().getString())
            )).collect(Collectors.toUnmodifiableList())
        );
    }

    @Override
    public void acceptPayload(SlotPayload payload) {
        var states = new HashSet<MachineState>();

        // Gross... Maybe use a registry someday for these :)
        MachineStateType[] machineStateTypes = MachineStateType.values();
        if (payload instanceof ListSlotPayload list) {
            for (var itemPayload : list.contents()) {
                if (itemPayload instanceof PairSlotPayload pair) {
                    if (pair.left().type() == SlotPayloadType.INT && pair.right().type() == SlotPayloadType.STRING) {
                        int machineStateTypeOrdinal = ((IntSlotPayload) pair.left()).value();

                        if (machineStateTypeOrdinal >= 0 && machineStateTypeOrdinal < machineStateTypes.length) {
                            states.add(new MachineState(
                                machineStateTypes[machineStateTypeOrdinal],
                                Component.translatable(((StringSlotPayload)pair.right()).value())
                            ));
                        }
                    }
                }
            }
        }

        set(states);
    }
}
