package com.enderio.modconduits.common.modules.mekanism.chemical_filter;

import com.enderio.core.common.serialization.OrderedListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record EnderChemicalFilter(NonNullList<ChemicalStack> matches, boolean isDenyList)
    implements ChemicalFilter {

    public static final EnderChemicalFilter EMPTY = new EnderChemicalFilter(0);

    public static final Codec<EnderChemicalFilter> CODEC = RecordCodecBuilder
        .create(componentInstance -> componentInstance
            .group(
                OrderedListCodec.create(256, ChemicalStack.OPTIONAL_CODEC, ChemicalStack.EMPTY)
                    .fieldOf("chemicals")
                    .forGetter(EnderChemicalFilter::matches),
                Codec.BOOL.fieldOf("isInvert").forGetter(EnderChemicalFilter::isDenyList))
            .apply(componentInstance, EnderChemicalFilter::new));

    // @formatter:off
    public static final StreamCodec<RegistryFriendlyByteBuf, EnderChemicalFilter> STREAM_CODEC = StreamCodec.composite(
        ChemicalStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)),
        EnderChemicalFilter::matches,
        ByteBufCodecs.BOOL,
        EnderChemicalFilter::isDenyList,
        EnderChemicalFilter::new);
    // @formatter:on

    public EnderChemicalFilter(int size) {
        this(NonNullList.withSize(size, ChemicalStack.EMPTY), false);
    }

    public EnderChemicalFilter(List<ChemicalStack> matches, boolean isDenyList) {
        this(NonNullList.withSize(matches.size(), ChemicalStack.EMPTY), isDenyList);

        for (int i = 0; i < matches.size(); i++) {
            this.matches.set(i, matches.get(i));
        }
    }
    
    @Override
    public ChemicalStack test(@Nullable IChemicalHandler target, ChemicalStack stack) {
        for (var match : matches) {
            if (match.isEmpty()) {
                continue;
            }

            if (ChemicalStack.isSameChemical(match, stack)) {
                return isDenyList ? ChemicalStack.EMPTY : stack;
            }
        }

        return isDenyList ? stack : ChemicalStack.EMPTY;
    }
}
