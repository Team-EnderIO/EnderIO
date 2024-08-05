package com.enderio.conduits.common.conduit.facades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Additional options for a conduit facade.
 * These are stored separately so that it can be an item component alongside the paint.
 * @param doesHideConduits Whether the conduit geometry should not be visible (i.e. glass)
 * @param isHardened Whether the conduit bundle should have a higher blast resistance
 */
public record FacadeOptions(
    boolean doesHideConduits,
    boolean isHardened
) {
    public static final Codec<FacadeOptions> CODEC = RecordCodecBuilder.create(
        inst -> inst.group(
            Codec.BOOL.fieldOf("does_hide_conduits").forGetter(FacadeOptions::doesHideConduits),
            Codec.BOOL.fieldOf("is_hardened").forGetter(FacadeOptions::isHardened)
        ).apply(inst, FacadeOptions::new)
    );

    public static final StreamCodec<ByteBuf, FacadeOptions> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.BOOL,
            FacadeOptions::doesHideConduits,
            ByteBufCodecs.BOOL,
            FacadeOptions::isHardened,
            FacadeOptions::new
        );
}
