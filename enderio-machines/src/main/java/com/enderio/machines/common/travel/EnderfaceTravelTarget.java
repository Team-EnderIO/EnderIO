package com.enderio.machines.common.travel;

import com.enderio.base.api.travel.TravelTarget;
import com.enderio.base.api.travel.TravelTargetSerializer;
import com.enderio.base.api.travel.TravelTargetType;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.init.MachineTravelTargets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record EnderfaceTravelTarget(BlockPos pos) implements TravelTarget {
    public static NetworkDataSlot.CodecType<EnderfaceTravelTarget> DATA_SLOT_TYPE = new NetworkDataSlot.CodecType<>(
            EnderfaceTravelTarget.Serializer.CODEC.codec(), EnderfaceTravelTarget.Serializer.STREAM_CODEC);

    @Override
    public int item2BlockRange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int block2BlockRange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public TravelTargetType<?> type() {
        return MachineTravelTargets.ENDERFACE_TYPE.get();
    }

    @Override
    public TravelTargetSerializer<?> serializer() {
        return MachineTravelTargets.ENDERFACE_SERIALIZER.get();
    }

    @Override
    public boolean canTeleportTo() {
        return false;
    }

    @Override
    public boolean canTravelTo() {
        return false;
    }

    @Override
    public boolean canJumpTo() {
        return false;
    }

    @Override
    public boolean canInteract() {
        return true;
    }

    @Override
    public boolean interact(Level level, Player player) {
        if (level.isClientSide) {
            // TODO: Causes classload errors on dedicated servers.
//            Minecraft.getInstance().setScreen(new EnderfaceScreen(pos.immutable(), Minecraft.getInstance().level));
        }
        return true;
    }

    public static class Serializer implements TravelTargetSerializer<EnderfaceTravelTarget> {

        public static MapCodec<EnderfaceTravelTarget> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(EnderfaceTravelTarget::pos))
                        .apply(instance, EnderfaceTravelTarget::new));

        public static StreamCodec<RegistryFriendlyByteBuf, EnderfaceTravelTarget> STREAM_CODEC = StreamCodec
                .composite(BlockPos.STREAM_CODEC, EnderfaceTravelTarget::pos, EnderfaceTravelTarget::new);

        @Override
        public MapCodec<EnderfaceTravelTarget> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EnderfaceTravelTarget> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
