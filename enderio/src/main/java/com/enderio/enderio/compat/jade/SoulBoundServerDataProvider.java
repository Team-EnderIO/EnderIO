package com.enderio.enderio.compat.jade;

import com.enderio.enderio.api.EnderIOCapabilities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.StreamServerDataProvider;

public class SoulBoundServerDataProvider implements StreamServerDataProvider<BlockAccessor, EntityType<?>> {
    public static final SoulBoundServerDataProvider INSTANCE = new SoulBoundServerDataProvider();

    @Override
    public @Nullable EntityType<?> streamData(BlockAccessor blockAccessor) {
        var soulBindable = blockAccessor.getLevel().getCapability(EnderIOCapabilities.SOUL_BINDABLE_BLOCK, blockAccessor.getPosition());
        if (soulBindable != null && soulBindable.hasSoul()) {
            return soulBindable.getBoundSoul().entityType();
        }

        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntityType<?>> streamCodec() {
        return EntityType.STREAM_CODEC;
    }

    @Override
    public Identifier getUid() {
        return EIOJadePlugin.SOUL_BOUND_COMPONENT;
    }
}
