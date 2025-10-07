package com.enderio.enderio.foundation.state;

import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.foundation.lang.EIOLang;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Set;

public record MachineState(MachineStateType type, MutableComponent component) {

    public static final MachineState ACTIVE = new MachineState(MachineStateType.ACTIVE, MachinesLang.STATUS_ACTIVE);
    public static final MachineState IDLE = new MachineState(MachineStateType.IDLE, MachinesLang.STATUS_IDLE);
    public static final MachineState EMPTY_INPUT = new MachineState(MachineStateType.IDLE,
        MachinesLang.STATUS_INPUT_EMPTY);
    public static final MachineState NO_SOURCE = new MachineState(MachineStateType.ERROR,
        MachinesLang.STATUS_DRAIN_NO_SOURCE);
    public static final MachineState EMPTY_TANK = new MachineState(MachineStateType.ERROR,
        MachinesLang.STATUS_EMPTY_TANK);
    public static final MachineState FULL_TANK = new MachineState(MachineStateType.ERROR,
        MachinesLang.STATUS_FULL_TANK);
    public static final MachineState NO_POWER = new MachineState(MachineStateType.ERROR, MachinesLang.STATUS_NO_ENERGY);
    public static final MachineState FULL_POWER = new MachineState(MachineStateType.ERROR,
        MachinesLang.STATUS_ENERGY_FULL);
    public static final MachineState NO_CAPACITOR = new MachineState(MachineStateType.ERROR,
        MachinesLang.STATUS_NO_CAPACITOR);
    public static final MachineState NOT_SOULBOUND = new MachineState(MachineStateType.ERROR,
        EIOLang.TOOLTIP_NO_SOULBOUND);
    public static final MachineState FULL_OUTPUT = new MachineState(MachineStateType.ERROR,
        MachinesLang.STATUS_OUTPUT_FULL);
    public static final MachineState REDSTONE = new MachineState(MachineStateType.DISABLED,
        MachinesLang.STATUS_BLOCKED_REDSTONE);

    public static final Codec<MachineState> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(MachineStateType.CODEC.fieldOf("Type").forGetter(MachineState::type),
                    Codec.STRING.xmap(Component::translatable, Component::getString)
                            .fieldOf("Component")
                            .forGetter(MachineState::component))
                    .apply(instance, MachineState::new));

    public static final StreamCodec<ByteBuf, MachineState> STREAM_CODEC = StreamCodec.composite(
            MachineStateType.STREAM_CODEC, MachineState::type,
            ByteBufCodecs.STRING_UTF8.map(Component::translatable, Component::getString), MachineState::component,
            MachineState::new);

    public static final NetworkDataSlot.CodecType<Set<MachineState>> DATA_SLOT_TYPE = NetworkDataSlot.CodecType
            .createSet(CODEC, STREAM_CODEC.cast());

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MachineState that = (MachineState) obj;
        return type == that.type && component == that.component; // Use identity
    }

    @Override
    public int hashCode() {
        return 31 * type.ordinal() + System.identityHashCode(component); // Only hash instance
    }
}
