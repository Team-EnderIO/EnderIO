package com.enderio.base.api.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * This class is in this package, because it's not only used by the item, but also by machines
 */

public record CoordinateSelection(ResourceKey<Level> level, BlockPos pos) {

    public static final Codec<CoordinateSelection> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("Level").forGetter(CoordinateSelection::level),
            BlockPos.CODEC.fieldOf("Pos").forGetter(CoordinateSelection::pos)
        ).apply(instance, CoordinateSelection::new)
    );

    public static final StreamCodec<ByteBuf, CoordinateSelection> STREAM_CODEC = StreamCodec.composite(
        ResourceKey.streamCodec(Registries.DIMENSION),
        CoordinateSelection::level,
        BlockPos.STREAM_CODEC,
        CoordinateSelection::pos,
        CoordinateSelection::new
    );

    public CoordinateSelection(Level level, BlockPos pos) {
        this(level.dimension(), pos);
    }

    /**
     * Get the name of the given level.
     */
    public static String getLevelName(ResourceLocation level) {
        return level.getNamespace().equals("minecraft") ? level.getPath() : level.toString();
    }

    /**
     * Get the name of the level this points to.
     */
    public String getLevelName() {
        return getLevelName(level.location());
    }
}
