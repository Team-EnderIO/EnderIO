package com.enderio.machines.common.blockentity.sync;

import com.enderio.api.capability.ICapacitorData;
import com.enderio.core.common.blockentity.sync.EnderDataSlot;
import com.enderio.core.common.blockentity.sync.SyncMode;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.base.common.capacitor.DefaultCapacitorData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

// TODO: Currently syncs based on itemstack, might be wise to find a way to serialize all capacitor datum into the same structure so its guaranteed to be reconstructed properly
//       This would prevent config mismatch breaking the client.
//       Should be done once specialization etc. has been implemented.

/**
 * A data slot that syncs capacitor data between client and server.
 */
public class CapacitorDataSlot extends EnderDataSlot<ICapacitorData> {

    private final Supplier<ItemStack> capacitorItemGetter;

    public CapacitorDataSlot(Supplier<ItemStack> capacitorItemGetter, Consumer<ICapacitorData> setter, SyncMode mode) {
        super(null, setter, mode);
        this.capacitorItemGetter = capacitorItemGetter;
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("Item", capacitorItemGetter.get().serializeNBT());
        return tag;
    }

    @Override
    protected ICapacitorData fromNBT(CompoundTag nbt) {
        return CapacitorUtil.getCapacitorData(ItemStack.of(nbt.getCompound("Item"))).orElse(DefaultCapacitorData.NONE);
    }
}
