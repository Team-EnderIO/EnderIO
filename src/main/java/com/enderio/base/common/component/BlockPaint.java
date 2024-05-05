package com.enderio.base.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;

public record BlockPaint(Block paint) {
    public static final Codec<BlockPaint> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("paint").forGetter(BlockPaint::paint)
        ).apply(instance, BlockPaint::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPaint> STREAM_CODEC = ByteBufCodecs.registry(Registries.BLOCK)
        .map(BlockPaint::new, BlockPaint::paint);

    public static BlockPaint of(Block paint) {
        return new BlockPaint(paint);
    }
}
