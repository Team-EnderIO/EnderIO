package com.enderio.modconduits.common.modules.mekanism.chemical;

import com.enderio.conduits.api.network.node.NodeData;
import com.enderio.conduits.api.network.node.legacy.ConduitData;
import com.enderio.conduits.api.network.node.legacy.ConduitDataType;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public class ChemicalConduitData implements ConduitData<ChemicalConduitData> {

    public static final MapCodec<ChemicalConduitData> CODEC = RecordCodecBuilder
            .mapCodec(
                    instance -> instance
                            .group(Codec.BOOL.fieldOf("should_reset").forGetter(i -> i.shouldReset),
                                    ChemicalStack.OPTIONAL_CODEC.optionalFieldOf("locked_fluid", ChemicalStack.EMPTY)
                                            .forGetter(i -> i.lockedChemical))
                            .apply(instance, ChemicalConduitData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChemicalConduitData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, i -> i.shouldReset, ChemicalStack.OPTIONAL_STREAM_CODEC, i -> i.lockedChemical,
            ChemicalConduitData::new);

    private ChemicalStack lockedChemical = ChemicalStack.EMPTY;
    private boolean shouldReset = false;

    public ChemicalConduitData() {
    }

    public ChemicalConduitData(boolean shouldReset, ChemicalStack lockedChemical) {
        this.shouldReset = shouldReset;
        this.lockedChemical = lockedChemical;
    }

    public boolean shouldReset() {
        return shouldReset;
    }

    public void setShouldReset(boolean shouldReset) {
        this.shouldReset = shouldReset;
    }

    public ChemicalStack lockedChemical() {
        return lockedChemical;
    }

    public void setLockedChemical(ChemicalStack lockedChemical) {
        this.lockedChemical = lockedChemical;
    }

    @Override
    public ConduitDataType<ChemicalConduitData> type() {
        return MekanismModule.CHEMICAL_DATA_TYPE.get();
    }

    @Override
    public ChemicalConduitData withClientChanges(ChemicalConduitData guiData) {
        this.shouldReset = guiData.shouldReset;

        // TODO: Soon we will swap to records which will mean this will be a new
        // instance.
        // This API has been designed with this pending change in mind.
        return this;
    }

    @Override
    public @Nullable NodeData toNodeData() {
        return null;
    }

    @Override
    public ChemicalConduitData deepCopy() {
        return new ChemicalConduitData(shouldReset, lockedChemical);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shouldReset, lockedChemical);
    }
}
