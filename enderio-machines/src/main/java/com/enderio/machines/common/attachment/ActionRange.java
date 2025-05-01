package com.enderio.machines.common.attachment;

import com.enderio.base.common.particle.RangeParticleData;
import com.enderio.core.common.network.NetworkDataSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

public record ActionRange(int range, boolean isVisible, int maxRange) {
    public static final Codec<ActionRange> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.INT.fieldOf("range").forGetter(ActionRange::range),
                    Codec.BOOL.fieldOf("isVisible").forGetter(ActionRange::isVisible),
                Codec.INT.fieldOf("maxRange").forGetter(ActionRange::maxRange))
            .apply(instance, ActionRange::new));

    public static final StreamCodec<ByteBuf, ActionRange> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT,
            ActionRange::range, ByteBufCodecs.BOOL, ActionRange::isVisible, ByteBufCodecs.INT, ActionRange::maxRange, ActionRange::new);

    public static NetworkDataSlot.CodecType<ActionRange> DATA_SLOT_TYPE = new NetworkDataSlot.CodecType<>(CODEC,
            STREAM_CODEC.cast());

    public ActionRange visible() {
        return new ActionRange(range, true, maxRange);
    }

    public ActionRange invisible() {
        return new ActionRange(range, false, maxRange);
    }

    public ActionRange increment() {
        return new ActionRange(range + 1, isVisible, maxRange);
    }

    public ActionRange decrement() {
        return new ActionRange(range - 1, isVisible, maxRange);
    }

    public ActionRange clamp(int min, int max) {
        return new ActionRange(Mth.clamp(range(), min, max), isVisible, maxRange);
    }

    public ActionRange setMaxRange(int maxRange) {
        return new ActionRange(range, isVisible, maxRange);
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public void addClientParticle(ClientLevel level, BlockPos pos, String color) {
        if (!isVisible) {
            return;
        }

        if (level.isClientSide()) {
            level.addAlwaysVisibleParticle(new RangeParticleData(range, color), true, pos.getX(), pos.getY(),
                    pos.getZ(), 0, 0, 0);
        }
    }

    public Tag save(HolderLookup.Provider lookupProvider) {
        return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    public static ActionRange parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }
}
