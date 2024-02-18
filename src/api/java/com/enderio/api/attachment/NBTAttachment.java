package com.enderio.api.attachment;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class NBTAttachment implements INBTSerializable<CompoundTag> {

    private CompoundTag tag;

    // TODO: NEO-PORT: Not happy with this class having an "empty" constructor
    //       Either needs to be removed somehow, or make this safe to have nulls.
    public NBTAttachment() {
    }

    public NBTAttachment(CompoundTag tag) {
        this.tag = tag;
    }

    public CompoundTag getTag() {
        return tag;
    }

    @Override
    public CompoundTag serializeNBT() {
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.tag = nbt;
    }
}
