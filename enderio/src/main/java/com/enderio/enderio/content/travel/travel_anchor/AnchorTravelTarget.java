package com.enderio.enderio.content.travel.travel_anchor;

import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.enderio.api.poi.EnderPOI;
import com.enderio.enderio.api.poi.EnderPOISerializer;
import com.enderio.enderio.api.poi.EnderPOIType;
import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.content.travel.TravelHandler;
import com.enderio.enderio.foundation.network.packets.ServerboundRequestTravelPacket;
import com.enderio.enderio.init.EIOTravelTargets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public record AnchorTravelTarget(BlockPos pos, String name, Item icon, boolean isVisible) implements EnderPOI {

    public static final NetworkDataSlot.CodecType<AnchorTravelTarget> DATA_SLOT_TYPE = new NetworkDataSlot.CodecType<>(
            Serializer.CODEC.codec(), Serializer.STREAM_CODEC);

    public AnchorTravelTarget withName(String name) {
        return new AnchorTravelTarget(pos, name, icon, isVisible);
    }

    public AnchorTravelTarget withIcon(Item icon) {
        return new AnchorTravelTarget(pos, name, icon, isVisible);
    }

    public AnchorTravelTarget withVisible(boolean isVisible) {
        return new AnchorTravelTarget(pos, name, icon, isVisible);
    }

    //@Override
    public boolean canJumpTo() {
        // TODO: Protected & Private Anchors
        return true;
    }

    @Override
    public int item2BlockRange() {
        return BaseConfig.COMMON.ITEMS.TRAVELLING_TO_BLOCK_RANGE.get();
    }

    @Override
    public int block2BlockRange() {
        return BaseConfig.COMMON.ITEMS.TRAVELLING_BLOCK_TO_BLOCK_RANGE.get();
    }

    @Override
    public boolean isActive() {
        return isVisible;
    }

    @Override
    public boolean onActivation(Level level, Player player) {
        Optional<Double> height = TravelHandler.isTeleportPositionClear(level, this.pos());
        if (height.isEmpty()) {
            return false;
        }
        BlockPos blockPos = this.pos();
        Vec3 teleportPosition = new Vec3(blockPos.getX() + 0.5f, blockPos.getY() + height.get() + 1,
            blockPos.getZ() + 0.5f);
        teleportPosition = TravelHandler.teleportEvent(player, teleportPosition).orElse(null);
        if (teleportPosition != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                player.teleportTo(teleportPosition.x(), teleportPosition.y(), teleportPosition.z());
                // Stop "moved too quickly" warnings
                serverPlayer.connection.resetPosition();
                player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.75F, 1F);
            } else {
                PacketDistributor.sendToServer(new ServerboundRequestTravelPacket(this.pos()));
            }

            player.resetFallDistance();
            return true;
        }
        return false;
    }

    @Override
    public EnderPOIType<?> type() {
        return EIOTravelTargets.TRAVEL_ANCHOR_TYPE.get();
    }

    @Override
    public EnderPOISerializer<?> serializer() {
        return EIOTravelTargets.TRAVEL_ANCHOR_SERIALIZER.get();
    }

    public static class Serializer implements EnderPOISerializer<AnchorTravelTarget> {

        public static final MapCodec<AnchorTravelTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(BlockPos.CODEC.fieldOf("pos").forGetter(AnchorTravelTarget::pos),
                        Codec.STRING.fieldOf("name").forGetter(AnchorTravelTarget::name),
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("icon").forGetter(AnchorTravelTarget::icon),
                        Codec.BOOL.fieldOf("is_visible").forGetter(AnchorTravelTarget::isVisible))
                .apply(instance, AnchorTravelTarget::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AnchorTravelTarget> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, AnchorTravelTarget::pos, ByteBufCodecs.STRING_UTF8, AnchorTravelTarget::name,
                ByteBufCodecs.registry(Registries.ITEM), AnchorTravelTarget::icon, ByteBufCodecs.BOOL,
                AnchorTravelTarget::isVisible, AnchorTravelTarget::new);

        @Override
        public MapCodec<AnchorTravelTarget> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AnchorTravelTarget> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
