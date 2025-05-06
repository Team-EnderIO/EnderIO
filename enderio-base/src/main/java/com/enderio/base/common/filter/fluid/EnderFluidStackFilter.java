package com.enderio.base.common.filter.fluid;

import com.enderio.base.api.new_filter.FluidStackFilter;
import com.enderio.base.common.item.filter.EnderFluidStackFilterItem;
import com.enderio.core.common.serialization.OrderedListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record EnderFluidStackFilter(NonNullList<FluidStack> matches, boolean isDenyList, boolean shouldCompareComponents) implements FluidStackFilter {

    public static final EnderFluidStackFilter EMPTY = new EnderFluidStackFilter(NonNullList.of(FluidStack.EMPTY), false, false);

    // TODO: 1.22: Rename fields.
    public static final Codec<EnderFluidStackFilter> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            OrderedListCodec.create(256, FluidStack.OPTIONAL_CODEC, FluidStack.EMPTY)
                .fieldOf("fluids")
                .forGetter(EnderFluidStackFilter::matches),
            Codec.BOOL.fieldOf("isInvert").forGetter(EnderFluidStackFilter::isDenyList),
            Codec.BOOL.fieldOf("isNbt").forGetter(EnderFluidStackFilter::shouldCompareComponents))
        .apply(inst, EnderFluidStackFilter::new));

    // @formatter:off
    public static final StreamCodec<RegistryFriendlyByteBuf, EnderFluidStackFilter> STREAM_CODEC = StreamCodec.composite(
        FluidStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)),
        EnderFluidStackFilter::matches,
        ByteBufCodecs.BOOL,
        EnderFluidStackFilter::isDenyList,
        ByteBufCodecs.BOOL,
        EnderFluidStackFilter::shouldCompareComponents,
        EnderFluidStackFilter::new);
    // @formatter:on

    public EnderFluidStackFilter(List<FluidStack> matches, boolean isDenyList, boolean shouldCompareComponents) {
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

            if (shouldCompareComponents ? FluidStack.isSameFluid(match, stack) : FluidStack.isSameFluidSameComponents(match, stack)) {
                return isDenyList ? FluidStack.EMPTY : stack;
            }
        }

        return isDenyList ? stack : FluidStack.EMPTY;
    }
}

