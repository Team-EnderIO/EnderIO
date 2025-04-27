package com.enderio.base.api.travel;

import com.enderio.base.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

public interface TravelTarget {
    Codec<TravelTarget> CODEC = EnderIORegistries.TRAVEL_TARGET_SERIALIZERS.byNameCodec()
            .dispatch(TravelTarget::serializer, TravelTargetSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, TravelTarget> STREAM_CODEC = ByteBufCodecs
            .registry(EnderIORegistries.Keys.TRAVEL_TARGET_SERIALIZERS)
            .dispatch(TravelTarget::serializer, TravelTargetSerializer::streamCodec);

    BlockPos pos();

    int item2BlockRange();

    int block2BlockRange();

    /**
     * @deprecated No longer used directly, use canTeleportTo and canJumpTo instead. These are more specific to the possible travel types.
     * @return Whether the target can be travelled to.
     */
    @Deprecated(since = "7.0.12-alpha")
    default boolean canTravelTo() {
        return true;
    }

    /**
     * @return Whether the target can be teleported to.
     */
    default boolean canTeleportTo() {
        return canTravelTo();
    }

    /**
     * @return Whether the target can be jumped to like an elevator.
     */
    default boolean canJumpTo() {
        return canTravelTo();
    }

    /**
     * @apiNote This method is internal, as it is used for an unreleased feature. It is not intended for use by other mods yet, as it will change.
     * @return Whether the target has a special remote interaction.
     */
    @ApiStatus.Internal
    default boolean canInteract() {
        return false;
    }

    /**
     * Interact with the target remotely.
     * @apiNote This method is internal, as it is used for an unreleased feature. It is not intended for use by other mods yet, as it will change.
     * @return true if action was successful
     */
    @ApiStatus.Internal
    default boolean interact(Level level, Player player) {
        return false;
    }

    TravelTargetType<?> type();

    TravelTargetSerializer<?> serializer();
}
