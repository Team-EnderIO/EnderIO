package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkContextSerializer;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class EnergyConduitNetworkContext implements ConduitNetworkContext<EnergyConduitNetworkContext> {

    private int energyStored = 0;
    private int energyInsertedThisTick = 0;
    private int rotatingIndex = 0;

    public EnergyConduitNetworkContext() {
    }

    public EnergyConduitNetworkContext(int energyStored) {
        this.energyStored = energyStored;
    }

    public EnergyConduitNetworkContext(int energyStored, int rotatingIndex) {
        this.energyStored = energyStored;
        this.rotatingIndex = rotatingIndex;
    }

    @Override
    @Nullable
    public ConduitNetworkContextSerializer<EnergyConduitNetworkContext> serializer() {
        return Serializer.INSTANCE;
    }

    public int energyStored() {
        return energyStored;
    }

    public void setEnergyStored(int energyStored) {
        this.energyStored = energyStored;
    }

    public int energyInsertedThisTick() {
        return energyInsertedThisTick;
    }

    public void setEnergyInsertedThisTick(int energyInsertedThisTick) {
        this.energyInsertedThisTick = energyInsertedThisTick;
    }

    public int rotatingIndex() {
        return rotatingIndex;
    }

    public void setRotatingIndex(int rotatingIndex) {
        this.rotatingIndex = rotatingIndex;
    }

    @Override
    public EnergyConduitNetworkContext mergeWith(EnergyConduitNetworkContext other) {
        return new EnergyConduitNetworkContext(this.energyStored + other.energyStored);
    }

    @Override
    public EnergyConduitNetworkContext splitFor(ConduitNetwork<EnergyConduitNetworkContext, ?> selfGraph, ConduitNetwork<EnergyConduitNetworkContext, ?> otherGraph) {
        if (selfGraph.getNodes().isEmpty()) {
            return new EnergyConduitNetworkContext();
        }

        if (otherGraph.getNodes().isEmpty()) {
            return new EnergyConduitNetworkContext(this.energyStored);
        }

        float proportion = (float) selfGraph.getNodes().size() / (selfGraph.getNodes().size() + otherGraph.getNodes().size());
        return new EnergyConduitNetworkContext((int) (this.energyStored * proportion));
    }

    public static class Serializer implements ConduitNetworkContextSerializer<EnergyConduitNetworkContext> {

        public static Serializer INSTANCE = new Serializer();

        private static final String KEY_ENERGY_STORED = "EnergyStored";
        private static final String KEY_ROTATING_INDEX = "RotatingIndex";

        @Override
        public CompoundTag save(EnergyConduitNetworkContext context) {
            CompoundTag tag = new CompoundTag();
            tag.putInt(KEY_ENERGY_STORED, context.energyStored);
            tag.putInt(KEY_ROTATING_INDEX, context.rotatingIndex);
            return tag;
        }

        @Override
        public EnergyConduitNetworkContext load(CompoundTag tag) {
            int energyStored = tag.contains(KEY_ENERGY_STORED) ? tag.getInt(KEY_ENERGY_STORED) : 0;
            int rotatingIndex = tag.contains(KEY_ROTATING_INDEX) ? tag.getInt(KEY_ROTATING_INDEX) : 0;
            return new EnergyConduitNetworkContext(energyStored, rotatingIndex);
        }
    }
}
