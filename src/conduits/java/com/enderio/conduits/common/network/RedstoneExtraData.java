package com.enderio.conduits.common.network;

import com.enderio.api.conduit.IExtendedConduitData;
import net.minecraft.nbt.CompoundTag;

public class RedstoneExtraData implements IExtendedConduitData<RedstoneExtraData> {

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

    public RedstoneExtraData deepCopy() {
        RedstoneExtraData redstoneExtraData = new RedstoneExtraData();
        redstoneExtraData.setActive(isActive());
        return redstoneExtraData;
    }
}