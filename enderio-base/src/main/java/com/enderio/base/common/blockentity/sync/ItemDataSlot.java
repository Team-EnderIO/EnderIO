package com.enderio.base.common.blockentity.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemDataSlot extends EnderDataSlot<Item> {

    public ItemDataSlot(Supplier<Item> getter, Consumer<Item> setter, SyncMode mode) {
        super(getter, setter, mode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("value", getter().get().getRegistryName().toString());
        return tag;
    }

    @Override
    protected Item fromNBT(CompoundTag nbt) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("value")));
    }
}
