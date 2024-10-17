package com.enderio.modconduits.mods.pneumaticcraft;

import com.enderio.conduits.api.ConduitNetworkContext;
import com.enderio.conduits.api.ConduitNetworkContextType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PressureConduitContext implements ConduitNetworkContext<PressureConduitContext> {

    public static final Codec<PressureConduitContext> CODEC = RecordCodecBuilder.create(
        builder -> builder.group(
            Codec.INT.fieldOf("air").forGetter(i -> i.air)
        ).apply(builder, PressureConduitContext::new)
    );

    private int air = 0;

    public PressureConduitContext() {}

    public PressureConduitContext(int air) {
        this.air = air;
    }

    public int getAir() {
        return air;
    }

    public void setAir(int air) {
        this.air = air;
    }

    @Override
    public PressureConduitContext mergeWith(PressureConduitContext other) {
        return new PressureConduitContext(this.air + other.air);
    }

    @Override
    public PressureConduitContext copy() {
        return new PressureConduitContext(air);
    }

    @Override
    public ConduitNetworkContextType<PressureConduitContext> type() {
        return PneumaticModule.NetworkContexts.PRESSURE_NETWORK.get();
    }
}
