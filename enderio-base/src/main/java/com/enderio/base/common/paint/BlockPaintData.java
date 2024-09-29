package com.enderio.base.common.paint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;

public record BlockPaintData(Block paint) {
    public static final Codec<BlockPaintData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("paint").forGetter(BlockPaintData::paint)
        ).apply(instance, BlockPaintData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPaintData> STREAM_CODEC = ByteBufCodecs.registry(Registries.BLOCK)
        .map(BlockPaintData::new, BlockPaintData::paint);

    public static BlockPaintData of(Block paint) {
        return new BlockPaintData(paint);
    }
}
