package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.lang.MachineLang;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record MachineState(MachineStateType type, MutableComponent component) {
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MachineState that = (MachineState) o;

        if (type != that.type)
            return false;
        return component.equals(that.component);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + component.hashCode();
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
        return new MachineState(MachineStateType.READY, MachineLang.TOOLTIP_ACTIVE);
    }

    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeUtf(type.name());
        buf.writeUtf(component.getString());
    }

    public static MachineState fromBuffer(FriendlyByteBuf buf) {
        return new MachineState(MachineStateType.valueOf(buf.readUtf()), Component.translatable(buf.readUtf()));
    }
}
