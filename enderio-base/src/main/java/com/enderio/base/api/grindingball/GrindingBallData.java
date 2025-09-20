package com.enderio.base.api.grindingball;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;

import java.util.Optional;

public record GrindingBallData(float outputMultiplier, float bonusMultiplier, float powerUse, int durability) {

    public static final Codec<GrindingBallData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.FLOAT.fieldOf("OutputMultiplier").forGetter(GrindingBallData::outputMultiplier),
            Codec.FLOAT.fieldOf("BonusMultiplier").forGetter(GrindingBallData::bonusMultiplier),
            Codec.FLOAT.fieldOf("PowerUse").forGetter(GrindingBallData::powerUse),
            Codec.INT.fieldOf("Durability").forGetter(GrindingBallData::durability)
        ).apply(instance, GrindingBallData::new)
    );

    public static final StreamCodec<ByteBuf, GrindingBallData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        GrindingBallData::outputMultiplier,
        ByteBufCodecs.FLOAT,
        GrindingBallData::bonusMultiplier,
        ByteBufCodecs.FLOAT,
        GrindingBallData::powerUse,
        ByteBufCodecs.INT,
        GrindingBallData::durability,
        GrindingBallData::new
    );

    public static final GrindingBallData IDENTITY = new GrindingBallData(1.0f, 1.0f, 1.0f, 0);

    public boolean isIdentity() {
        return this.equals(IDENTITY);
    }

    private static final Logger LOGGER = LogUtils.getLogger();

    public Tag save(HolderLookup.Provider lookupProvider) {
        if (this.isIdentity()) {
            throw new IllegalStateException("Cannot encode identity GrindingBallData");
        } else {
            return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
        }
    }

    public Tag saveOptional(HolderLookup.Provider lookupProvider) {
        return this.isIdentity() ? save(lookupProvider) : new CompoundTag();
    }

    public static Optional<GrindingBallData> parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag)
            .resultOrPartial(error -> LOGGER.error("Tried to load invalid GrindingBallData: '{}'", error));
    }

    public static GrindingBallData parseOptional(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        return tag.isEmpty() ? IDENTITY : parse(lookupProvider, tag).orElse(IDENTITY);
    }
}
