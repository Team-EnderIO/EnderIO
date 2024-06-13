package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitData;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class FluidConduitData implements ConduitData<FluidConduitData> {

    public final boolean isMultiFluid;

    @Nullable
    Fluid lockedFluid = null;
    boolean shouldReset = false;

    public FluidConduitData(boolean isMultiFluid) {
        this.isMultiFluid = isMultiFluid;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public FluidConduitData(boolean isMultiFluid, boolean shouldReset, Optional<Fluid> fluid) {
        this.isMultiFluid = isMultiFluid;
        this.shouldReset = shouldReset;
        this.lockedFluid = isMultiFluid
            ? fluid.orElse(null)
            : null;
    }

    @Nullable
    public Fluid lockedFluid() {
        return lockedFluid;
    }

    public boolean shouldReset() {
        return shouldReset;
    }

    @Override
    public void applyClientChanges(FluidConduitData guiData) {
        this.shouldReset = guiData.shouldReset;
    }

    @Override
    public ConduitDataSerializer<FluidConduitData> serializer() {
        return EIOConduitTypes.Serializers.FLUID.get();
    }

    @Override
    public void onConnectTo(FluidConduitData otherData) {
        if (lockedFluid != null) {
            if (otherData.lockedFluid != null && lockedFluid != otherData.lockedFluid) {
                EnderIO.LOGGER.warn("incompatible fluid conduits merged");
            }
            otherData.setLockedFluid(lockedFluid);
        } else if (otherData.lockedFluid != null) {
            setLockedFluid(otherData.lockedFluid);
        }
    }

    @Override
    public boolean canConnectTo(FluidConduitData otherData) {
        return lockedFluid == null || otherData.lockedFluid == null || lockedFluid == otherData.lockedFluid;
    }

    private void setLockedFluid(@Nullable Fluid lockedFluid) {
        this.lockedFluid = lockedFluid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isMultiFluid, shouldReset, lockedFluid);
    }

    public static class Serializer implements ConduitDataSerializer<FluidConduitData> {
        public static MapCodec<FluidConduitData> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Codec.BOOL.fieldOf("is_multi_fluid").forGetter(i -> i.isMultiFluid),
                Codec.BOOL.fieldOf("should_reset").forGetter(i -> i.shouldReset),
                BuiltInRegistries.FLUID.byNameCodec()
                    .optionalFieldOf("locked_fluid")
                    .forGetter(i -> Optional.ofNullable(i.lockedFluid))
            ).apply(instance, FluidConduitData::new)
        );

        public static StreamCodec<RegistryFriendlyByteBuf, FluidConduitData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            i -> i.isMultiFluid,
            ByteBufCodecs.BOOL,
            i -> i.shouldReset,
            ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.FLUID)),
            i -> Optional.ofNullable(i.lockedFluid),
            FluidConduitData::new
        );

        @Override
        public MapCodec<FluidConduitData> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FluidConduitData> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
