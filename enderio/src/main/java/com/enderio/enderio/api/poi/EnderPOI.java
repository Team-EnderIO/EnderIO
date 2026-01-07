package com.enderio.enderio.api.poi;

import com.enderio.enderio.api.EnderIORegistries;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface EnderPOI {
    Codec<EnderPOI> CODEC = EnderIORegistries.TRAVEL_TARGET_SERIALIZERS.byNameCodec()
        .dispatch(EnderPOI::serializer, EnderPOISerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, EnderPOI> STREAM_CODEC = ByteBufCodecs
        .registry(EnderIORegistries.Keys.TRAVEL_TARGET_SERIALIZERS)
        .dispatch(EnderPOI::serializer, EnderPOISerializer::streamCodec);

    BlockPos pos();

    int item2BlockRange();

    int block2BlockRange();

    boolean isActive(Player player);

    boolean onActivation(Level level, Player player);

    EnderPOIType<?> type();

    EnderPOISerializer<?> serializer();
}
