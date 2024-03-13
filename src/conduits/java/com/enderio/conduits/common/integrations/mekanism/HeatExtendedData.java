package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.IExtendedConduitData;
import net.minecraft.nbt.CompoundTag;

public class HeatExtendedData implements IExtendedConduitData<HeatExtendedData> {
    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
