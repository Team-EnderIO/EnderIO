package com.enderio.modconduits.common.modules.mekanism.chemical;

import com.enderio.conduits.api.network.ConduitNetworkContext;
import com.enderio.conduits.api.network.ConduitNetworkContextType;
import com.enderio.conduits.api.network.IConduitNetwork;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;

public class ChemicalConduitNetworkContext implements ConduitNetworkContext<ChemicalConduitNetworkContext> {

    public static final MapCodec<ChemicalConduitNetworkContext> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Chemical.CODEC.optionalFieldOf("locked_chemical", MekanismAPI.EMPTY_CHEMICAL)
                    .forGetter(ChemicalConduitNetworkContext::lockedChemical))
            .apply(instance, ChemicalConduitNetworkContext::new));

    public static final ConduitNetworkContextType<ChemicalConduitNetworkContext> TYPE = new ConduitNetworkContextType<>(CODEC,
            ChemicalConduitNetworkContext::new);

    private Chemical lockedChemical;
    private Chemical lastLockedChemical = MekanismAPI.EMPTY_CHEMICAL;

    public ChemicalConduitNetworkContext() {
        this(MekanismAPI.EMPTY_CHEMICAL);
    }

    public ChemicalConduitNetworkContext(Chemical lockedChemical) {
        this.lockedChemical = lockedChemical;
    }

    public ChemicalConduitNetworkContext(Chemical lockedChemical, Chemical lastLockedChemical) {
        this.lockedChemical = lockedChemical;
        this.lastLockedChemical = lastLockedChemical;
    }

    public Chemical lockedChemical() {
        return lockedChemical;
    }

    public Chemical lastLockedChemical() {
        return lastLockedChemical;
    }

    public void clearLastLockedChemical() {
        this.lastLockedChemical = lockedChemical;
    }

    public void setLockedChemical(Chemical lockedChemical) {
        this.lastLockedChemical = this.lockedChemical;
        this.lockedChemical = lockedChemical;
    }

    @Override
    public ChemicalConduitNetworkContext mergeWith(ChemicalConduitNetworkContext other) {
        // Merge with the locked chemical, but set the last to empty so the ticker marks
        // the nodes as dirty.
        if (lockedChemical.isEmptyType()) {
            return new ChemicalConduitNetworkContext(other.lockedChemical, MekanismAPI.EMPTY_CHEMICAL);
        }

        return new ChemicalConduitNetworkContext(lockedChemical, MekanismAPI.EMPTY_CHEMICAL);
    }

    @Override
    public ChemicalConduitNetworkContext split(IConduitNetwork selfNetwork,
            Set<? extends IConduitNetwork> allNetworks) {
        return new ChemicalConduitNetworkContext(lockedChemical);
    }

    @Override
    public ConduitNetworkContextType<ChemicalConduitNetworkContext> type() {
        return TYPE;
    }
}
