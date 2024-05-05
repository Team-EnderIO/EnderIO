package com.enderio.core.common.network.slot;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

// TODO: 1.20.6: I'd like to endeavour to remove NBT slots.
public class NBTSerializingNetworkDataSlot<T> extends NetworkDataSlot<T> {
    private final BiFunction<T, HolderLookup.Provider, CompoundTag> toNBT;
    private final TriConsumer<T, HolderLookup.Provider, CompoundTag> handleNBT;
    private final BiConsumer<T, RegistryFriendlyByteBuf> toBuffer;
    private final Function<RegistryFriendlyByteBuf, T> fromBuffer;

    public NBTSerializingNetworkDataSlot(
        Supplier<T> getter,
        BiFunction<T, HolderLookup.Provider, CompoundTag> toNBT,
        TriConsumer<T, HolderLookup.Provider, CompoundTag> handleNBT,
        BiConsumer<T, RegistryFriendlyByteBuf> toBuffer,
        Function<RegistryFriendlyByteBuf, T> fromBuffer) {

        super(getter, null);
        this.toNBT = toNBT;
        this.handleNBT = handleNBT;
        this.toBuffer = toBuffer;
        this.fromBuffer = fromBuffer;
    }

    @Override
    public Tag serializeValueNBT(HolderLookup.Provider lookupProvider, T value) {
        return toNBT.apply(value, lookupProvider);
    }

    // We can return null here because we override this method's usage
    @Override
    protected T valueFromNBT(HolderLookup.Provider lookupProvider, Tag nbt) {
        return null;
    }

    @Override
    public void fromNBT(HolderLookup.Provider lookupProvider, Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag) {
            handleNBT.accept(getter.get(), lookupProvider, compoundTag);
        } else {
            throw new IllegalStateException("Invalid compound tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(RegistryFriendlyByteBuf buf, T value) {
        toBuffer.accept(value, buf);
    }

    @Override
    public void fromBuffer(RegistryFriendlyByteBuf buf) {
        try {
            fromBuffer.apply(buf);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid compound tag buffer was passed over the network.");
        }
    }

    @Override
    protected T valueFromBuffer(RegistryFriendlyByteBuf buf) {
        return null;
    }
}
