package com.enderio.api.conduit.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;

/**
 * Stores which block a facade should mimic.
 */
public record BlockPaintData(Block paint) {
    
    public static final Codec<BlockPaintData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("paint").forGetter(BlockPaintData::paint)
        ).apply(instance, BlockPaintData::new)
    );
    
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeId(BuiltInRegistries.BLOCK, paint);
    }
    
    public static BlockPaintData fromNetwork(FriendlyByteBuf buf) {
        return new BlockPaintData(buf.readById(BuiltInRegistries.BLOCK));
    }
}
