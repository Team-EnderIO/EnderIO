package com.enderio.base.common.filter.fluid;

import com.enderio.base.api.filter.FluidFilter;
import com.enderio.core.common.serialization.OrderedListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public record EnderFluidFilter(NonNullList<FluidStack> matches, boolean isDenyList, boolean shouldCompareComponents)
        implements FluidFilter {

    public static final EnderFluidFilter EMPTY = new EnderFluidFilter(0);

    // TODO: 1.22: Rename fields.
    public static final Codec<EnderFluidFilter> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(OrderedListCodec.create(256, FluidStack.OPTIONAL_CODEC, FluidStack.EMPTY)
                    .fieldOf("fluids")
                    .forGetter(EnderFluidFilter::matches),
                    Codec.BOOL.fieldOf("isInvert").forGetter(EnderFluidFilter::isDenyList),
                    Codec.BOOL.fieldOf("isNbt").forGetter(EnderFluidFilter::shouldCompareComponents))
            .apply(inst, EnderFluidFilter::new));

    // @formatter:off
    public static final StreamCodec<RegistryFriendlyByteBuf, EnderFluidFilter> STREAM_CODEC = StreamCodec.composite(
        FluidStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)),
        EnderFluidFilter::matches,
        ByteBufCodecs.BOOL,
        EnderFluidFilter::isDenyList,
        ByteBufCodecs.BOOL,
        EnderFluidFilter::shouldCompareComponents,
        EnderFluidFilter::new);
    // @formatter:on

    public EnderFluidFilter(int size) {
        this(NonNullList.withSize(size, FluidStack.EMPTY), false, false);
    }

    public EnderFluidFilter(List<FluidStack> matches, boolean isDenyList, boolean shouldCompareComponents) {
        this(NonNullList.withSize(matches.size(), FluidStack.EMPTY), isDenyList, shouldCompareComponents);

        for (int i = 0; i < matches.size(); i++) {
            this.matches.set(i, matches.get(i));
        }
    }

    @Override
    public FluidStack test(@Nullable IFluidHandler target, FluidStack stack) {
        for (var match : matches) {
            if (match.isEmpty()) {
                continue;
            }

            if (shouldCompareComponents ? FluidStack.isSameFluidSameComponents(match, stack) : FluidStack.isSameFluid(match, stack)) {
                return isDenyList ? FluidStack.EMPTY : stack;
            }
        }

        return isDenyList ? stack : FluidStack.EMPTY;
    }
}
