package com.enderio.api.travel;

import com.enderio.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface TravelTarget {
    Codec<TravelTarget> CODEC = EnderIORegistries.TRAVEL_TARGET_SERIALIZERS.byNameCodec().dispatch(TravelTarget::serializer, TravelTargetSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, TravelTarget> STREAM_CODEC = ByteBufCodecs.registry(EnderIORegistries.Keys.TRAVEL_TARGET_SERIALIZERS)
        .dispatch(TravelTarget::serializer, TravelTargetSerializer::streamCodec);

    BlockPos pos();

    int item2BlockRange();

    int block2BlockRange();

    default boolean canTravelTo() {
        return true;
    }

    TravelTargetType<?> type();
    TravelTargetSerializer<?> serializer();
}
