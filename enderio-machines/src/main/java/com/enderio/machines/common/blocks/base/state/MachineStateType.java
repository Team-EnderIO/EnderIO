package com.enderio.machines.common.blocks.base.state;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum MachineStateType implements StringRepresentable {

    ACTIVE(0, "active", 0), IDLE(1, "idle", 1), ERROR(2, "error", 2), DISABLED(3, "disabled", 3);

    public static final Codec<MachineStateType> CODEC = StringRepresentable.fromEnum(MachineStateType::values);
    public static final IntFunction<MachineStateType> BY_ID = ByIdMap.continuous(key -> key.id, values(),
            ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, MachineStateType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private final int id;
    private final String name;
    private final int priority;

    MachineStateType(int id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
