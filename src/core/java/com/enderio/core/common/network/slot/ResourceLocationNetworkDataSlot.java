package com.enderio.core.common.network.slot;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ResourceLocationNetworkDataSlot extends NetworkDataSlot<ResourceLocation> {
    public ResourceLocationNetworkDataSlot(Supplier<ResourceLocation> getter, Consumer<ResourceLocation> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(HolderLookup.Provider lookupProvider,ResourceLocation value) {
        return StringTag.valueOf(value.toString());
    }

    @Override
    protected ResourceLocation valueFromNBT(HolderLookup.Provider lookupProvider,Tag nbt) {
        if (nbt instanceof StringTag stringTag) {
            return new ResourceLocation(stringTag.getAsString());
        } else {
            throw new IllegalStateException("Invalid string tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(RegistryFriendlyByteBuf buf, ResourceLocation value) {
        buf.writeResourceLocation(value);
    }

    @Override
    public ResourceLocation valueFromBuffer(RegistryFriendlyByteBuf buf) {
        try {
            return buf.readResourceLocation();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid resourceLocation buffer was passed over the network.");
        }
    }
}
