package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IExtendedConduitData;
import net.minecraft.nbt.CompoundTag;

public class RedstoneExtendedData implements IExtendedConduitData<RedstoneExtendedData> {

    private boolean isActive = false;

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("active", isActive);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        isActive = nbt.getBoolean("active");
    }

    @Override
    public boolean syncDataToClient() {
        return true;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    public RedstoneExtendedData deepCopy() {
        RedstoneExtendedData redstoneExtendedData = new RedstoneExtendedData();
        redstoneExtendedData.setActive(isActive());
        return redstoneExtendedData;
    }
}