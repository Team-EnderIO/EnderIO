package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.conduits.api.network.ConduitNetworkContext;
import com.enderio.conduits.api.network.ConduitNetworkContextType;
import com.enderio.conduits.api.network.IConduitNetwork;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidConduitNetworkContext implements ConduitNetworkContext<FluidConduitNetworkContext> {

    public static final MapCodec<FluidConduitNetworkContext> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(BuiltInRegistries.FLUID.byNameCodec()
                    .optionalFieldOf("locked_fluid", Fluids.EMPTY)
                    .forGetter(FluidConduitNetworkContext::lockedFluid))
            .apply(instance, FluidConduitNetworkContext::new));

    public static final ConduitNetworkContextType<FluidConduitNetworkContext> TYPE = new ConduitNetworkContextType<>(CODEC,
            FluidConduitNetworkContext::new);

    private Fluid lockedFluid;
    private Fluid lastLockedFluid = Fluids.EMPTY;

    public FluidConduitNetworkContext() {
        this(Fluids.EMPTY);
    }

    public FluidConduitNetworkContext(Fluid lockedFluid) {
        this.lockedFluid = lockedFluid;
    }

    public FluidConduitNetworkContext(Fluid lockedFluid, Fluid lastLockedFluid) {
        this.lockedFluid = lockedFluid;
        this.lastLockedFluid = lastLockedFluid;
    }

    public Fluid lockedFluid() {
        return lockedFluid;
    }

    public Fluid lastLockedFluid() {
        return lastLockedFluid;
    }

    public void clearLastLockedFluid() {
        this.lastLockedFluid = lockedFluid;
    }

    public void setLockedFluid(Fluid lockedFluid) {
        this.lastLockedFluid = this.lockedFluid;
        this.lockedFluid = lockedFluid;
    }

    @Override
    public FluidConduitNetworkContext mergeWith(FluidConduitNetworkContext other) {
        // Merge with the locked fluid, but set the last to empty so the ticker marks
        // the nodes as dirty.
        if (lockedFluid.equals(Fluids.EMPTY)) {
            return new FluidConduitNetworkContext(other.lockedFluid, Fluids.EMPTY);
        }

        return new FluidConduitNetworkContext(lockedFluid, Fluids.EMPTY);
    }

    @Override
    public FluidConduitNetworkContext split(IConduitNetwork selfNetwork, Set<? extends IConduitNetwork> allNetworks) {
        return new FluidConduitNetworkContext(lockedFluid);
    }

    @Override
    public ConduitNetworkContextType<FluidConduitNetworkContext> type() {
        return TYPE;
    }
}
