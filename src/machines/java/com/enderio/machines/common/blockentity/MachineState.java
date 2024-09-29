package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.lang.MachineLang;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record MachineState(MachineStateType type, MutableComponent component) {

    public static final MachineState ACTIVE = new MachineState(MachineStateType.ACTIVE, MachineLang.TOOLTIP_ACTIVE);
    public static final MachineState IDLE = new MachineState(MachineStateType.IDLE, MachineLang.TOOLTIP_IDLE);
    public static final MachineState EMPTY_INPUT = new MachineState(MachineStateType.IDLE, MachineLang.TOOLTIP_INPUT_EMPTY);
    public static final MachineState NO_SOURCE = new MachineState(MachineStateType.ERROR, MachineLang.TOOLTIP_NO_SOURCE);
    public static final MachineState EMPTY_TANK = new MachineState(MachineStateType.ERROR, MachineLang.TOOLTIP_EMPTY_TANK);
    public static final MachineState FULL_TANK = new MachineState(MachineStateType.ERROR, MachineLang.TOOLTIP_FULL_TANK);
    public static final MachineState NO_POWER = new MachineState(MachineStateType.ERROR, MachineLang.TOOLTIP_NO_POWER);
    public static final MachineState FULL_POWER = new MachineState(MachineStateType.ERROR, MachineLang.TOOLTIP_FULL_POWER);
    public static final MachineState NO_CAPACITOR = new MachineState(MachineStateType.ERROR, MachineLang.TOOLTIP_NO_CAPACITOR);
    public static final MachineState NOT_SOULBOUND = new MachineState(MachineStateType.ERROR, MachineLang.TOOLTIP_NO_SOULBOUND);
    public static final MachineState FULL_OUTPUT = new MachineState(MachineStateType.ERROR, MachineLang.TOOLTIP_OUTPUT_FULL);
    public static final MachineState REDSTONE = new MachineState(MachineStateType.DISABLED, MachineLang.TOOLTIP_BLOCKED_RESTONE);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MachineState that = (MachineState) o;
        return type == that.type && component == that.component; //Use identity
    }

    @Override
    public int hashCode() {
        int result = type.ordinal();
        result = 31 * result + System.identityHashCode(component); //Only hash instance
        return result;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Type", type.name());
        nbt.putString("Compound", component.getString());
        tag.put("MachineState", nbt);
        return tag;
    }

    public static MachineState fromNBT(CompoundTag tag) {
        if (tag.contains("MachineState")) {
            CompoundTag nbt = tag.getCompound("MachineState");
            if (nbt.contains("Type") && nbt.contains("Compound")) {
                return new MachineState(MachineStateType.valueOf(nbt.getString("Type")), Component.translatable(nbt.getString("Compound")));
            }
        }
        return new MachineState(MachineStateType.ACTIVE, MachineLang.TOOLTIP_ACTIVE);
    }

    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeUtf(type.name());
        buf.writeUtf(component.getString());
    }

    public static MachineState fromBuffer(FriendlyByteBuf buf) {
        return new MachineState(MachineStateType.valueOf(buf.readUtf()), Component.translatable(buf.readUtf()));
    }
}
