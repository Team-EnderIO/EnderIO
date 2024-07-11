package com.enderio.base.api.io;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum IOMode implements StringRepresentable {
    /**
     * No specific configuration, allows external input and output but doesn't pull or push itself.
     */
    NONE(0, "none", true, true, false, true),

    /**
     * Only pushes outputs allows both external pulling and the machine pushes itself.
     * For example conduits can pull themselves, however putting a chest next to the machine will cause it to push items to the chest.
     *
     * @apiNote Each machine determines what this means this for energy. Some may ignore it.
     */
    PUSH(1, "push", false, true, true, true),

    /**
     * Only pulls inputs, allowing both external pushing and the machine pulling itself.
     * For example conduits can push into the machine themselves, but a chest next to the machine will also be pulled from.
     *
     * @apiNote Each machine determines what this means this for energy. Some may ignore it.
     */
    PULL(2, "pull", true, false, true, true),

    /**
     * Allow both pulling and pushing by both the machine and external blocks.
     *
     * @apiNote Each machine determines what this means this for energy. Some may ignore it.
     */
    BOTH(3, "both", true, true, true, true),

    /**
     * Disallow any side access for all resources (including energy).
     *
     * @apiNote All machines will disallow power access for this side.
     */
    DISABLED(4, "disable", false, false, false, false);

    public static final Codec<IOMode> CODEC = StringRepresentable.fromEnum(IOMode::values);
    public static final IntFunction<IOMode> BY_ID = ByIdMap.continuous(key -> key.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, IOMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private final int id;
    private final String name;
    private final boolean input;
    private final boolean output;
    private final boolean force;
    private final boolean canConnect;

    IOMode(int id, String name, boolean input, boolean output, boolean force, boolean canConnect) {
        this.id = id;
        this.name = name;
        this.input = input;
        this.output = output;
        this.force = force;
        this.canConnect = canConnect;
    }

    /**
     * Can resources be input via this side.
     */
    public boolean canInput() {
        return input;
    }

    /**
     * Can resources be output via this side.
     */
    public boolean canOutput() {
        return output;
    }

    /**
     * Whether this side can be connected to by external blocks.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canConnect() {
        return canConnect;
    }

    /**
     * Can resources be pushed out this side.
     *
     * @implNote This can be used by machines to determine if it should push resources out.
     */
    public boolean canPush() {
        return canOutput() && canForce();
    }

    /**
     * Can resources be pulled in this side.
     *
     * @implNote This can be used by machines to determine if it should pull resources in.
     */
    public boolean canPull() {
        return canInput() && canForce();
    }

    /**
     * Whether the machine can force resources in/out this side.
     */
    public boolean canForce() {
        return force;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
