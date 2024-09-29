package com.enderio.modconduits.mods.mekanism;

import com.enderio.conduits.api.ConduitData;
import com.enderio.conduits.api.ConduitDataType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public class ChemicalConduitData implements ConduitData<ChemicalConduitData> {

    public static MapCodec<ChemicalConduitData> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codec.BOOL.fieldOf("should_reset").forGetter(i -> i.shouldReset),
            ChemicalStack.OPTIONAL_CODEC
                .optionalFieldOf("locked_fluid")
                .forGetter(i -> Optional.of(i.lockedChemical))
        ).apply(instance, ChemicalConduitData::new)
    );

    public static StreamCodec<RegistryFriendlyByteBuf, ChemicalConduitData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        i -> i.shouldReset,
        ByteBufCodecs.optional(ChemicalStack.OPTIONAL_STREAM_CODEC),
        i -> Optional.of(i.lockedChemical),
        ChemicalConduitData::new
    );

    ChemicalStack lockedChemical = ChemicalStack.EMPTY;
    boolean shouldReset = false;

    public ChemicalConduitData() {
    }

    public ChemicalConduitData(boolean shouldReset, Optional<ChemicalStack> lockedChemical) {
        this.shouldReset = shouldReset;
        this.lockedChemical = lockedChemical.orElse(ChemicalStack.EMPTY);
    }

    @Override
    public ConduitDataType<ChemicalConduitData> type() {
        return MekanismModule.CHEMICAL_DATA_TYPE.get();
    }

    @Override
    public ChemicalConduitData withClientChanges(ChemicalConduitData guiData) {
        return new ChemicalConduitData(guiData.shouldReset, Optional.ofNullable(lockedChemical));
    }

    @Override
    public ChemicalConduitData deepCopy() {
        return new ChemicalConduitData(shouldReset, Optional.ofNullable(lockedChemical));
    }

    public void setlockedChemical(ChemicalStack lockedChemical) {
        this.lockedChemical = lockedChemical;
    }
}
