package com.enderio.core.common.network.slot;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SetNetworkDataSlot<T, V extends Tag> extends NetworkDataSlot<Set<T>> {

    private final Function<T, V> serializer;
    private final Function<V,T> deSerializer;
    private final BiConsumer<T, FriendlyByteBuf> toBuffer;
    private final Function<FriendlyByteBuf, T> fromBuffer;

    public SetNetworkDataSlot(Supplier<Set<T>> getter, Consumer<Set<T>> setter,
        Function<T, V> serializer, Function<V, T> deSerializer, BiConsumer<T, FriendlyByteBuf> toBuffer, Function<FriendlyByteBuf, T> fromBuffer) {
        super(getter, setter);
        this.serializer = serializer;
        this.deSerializer = deSerializer;
        this.toBuffer = toBuffer;
        this.fromBuffer = fromBuffer;
    }
    
    @Override
    public Tag serializeValueNBT(Set<T> value) {
        ListTag listTag = new ListTag();
        for (T t : value) {
            listTag.add(serializer.apply(t));
        }
        return listTag;
    }

    @Override
    protected Set<T> valueFromNBT(Tag nbt) {
        if (nbt instanceof ListTag listTag) {
            Set<T> set = new HashSet<>();
            for (Tag tag : listTag) {
                set.add(deSerializer.apply((V) tag));
            }
            return set;
        } else {
            throw new IllegalStateException("Invalid set tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, Set<T> value) {
        buf.writeInt(value.size());
        for (T element: value) {
            toBuffer.accept(element, buf);
        }
    }

    @Override
    protected Set<T> valueFromBuffer(FriendlyByteBuf buf) {
        Set<T> set = new HashSet<>();
        try {
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                set.add(fromBuffer.apply(buf));
            }
            return set;
        } catch (Exception e) {
            throw new IllegalStateException("Invalid list buffer was passed over the network.");
        }
    }
}
