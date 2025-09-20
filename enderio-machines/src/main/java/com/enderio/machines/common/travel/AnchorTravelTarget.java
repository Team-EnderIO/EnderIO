package com.enderio.machines.common.travel;

import com.enderio.base.api.travel.TravelTarget;
import com.enderio.base.api.travel.TravelTargetSerializer;
import com.enderio.base.api.travel.TravelTargetType;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.init.MachineTravelTargets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record AnchorTravelTarget(BlockPos pos, String name, Item icon, boolean isVisible) implements TravelTarget {

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

    @Override
    public boolean canTravelTo() {
        return isVisible;
    }

    @Override
    public boolean canTeleportTo() {
        return isVisible();
    }

    @Override
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
    public TravelTargetType<?> type() {
        return MachineTravelTargets.TRAVEL_ANCHOR_TYPE.get();
    }

    @Override
    public TravelTargetSerializer<?> serializer() {
        return MachineTravelTargets.TRAVEL_ANCHOR_SERIALIZER.get();
    }

    public static class Serializer implements TravelTargetSerializer<AnchorTravelTarget> {

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
