package com.enderio.enderio.content.enderface;

import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.enderio.api.poi.EnderPOI;
import com.enderio.enderio.api.poi.EnderPOISerializer;
import com.enderio.enderio.api.poi.EnderPOIType;
import com.enderio.enderio.client.content.machines.gui.screen.EnderfaceScreen;
import com.enderio.enderio.init.EIOTravelTargets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record EnderfaceTravelTarget(BlockPos pos) implements EnderPOI {
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
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean onActivation(Level level, Player player) {
        if (level.isClientSide) {
            Client.openScreen(level, pos);
        }
        return true;
    }

    @Override
    public EnderPOIType<?> type() {
        return EIOTravelTargets.ENDERFACE_TYPE.get();
    }

    @Override
    public EnderPOISerializer<?> serializer() {
        return EIOTravelTargets.ENDERFACE_SERIALIZER.get();
    }

    public static class Serializer implements EnderPOISerializer<EnderfaceTravelTarget> {

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

    //TODO packet
    private static class Client {

        @EnsureSide(EnsureSide.Side.CLIENT)
        public static void openScreen(Level level, BlockPos pos) {
            Minecraft.getInstance().setScreen(new EnderfaceScreen(pos.immutable(), (ClientLevel) level));
        }
    }
}
