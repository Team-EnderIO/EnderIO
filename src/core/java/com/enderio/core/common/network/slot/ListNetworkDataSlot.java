package com.enderio.core.common.network.slot;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListNetworkDataSlot<T, V extends Tag> extends NetworkDataSlot<List<T>> {

    private final Function<T, V> serializer;
    private final Function<V, T> deSerializer;

    public ListNetworkDataSlot(Supplier<List<T>> getter, Consumer<List<T>> setter,
        Function<T, V> serializer, Function<V, T> deSerializer) {
        //I can put null here, because I override the only usage of the setter
        super(getter, setter);
        this.serializer = serializer;
        this.deSerializer = deSerializer;
    }

    @Override
    public Tag serializeValueNBT(List<T> value) {
        ListTag listTag = new ListTag();
        for (T t : value) {
            listTag.add(serializer.apply(t));
        }
        return listTag;
    }

    @Override
    protected List<T> valueFromNBT(Tag nbt) {
        if (nbt instanceof ListTag listTag) {
            List<T> list = new ArrayList<>();
            for (Tag tag : listTag) {
                list.add(deSerializer.apply((V) tag));
            }
            return list;
        } else {
            throw new IllegalStateException("Invalid list tag was passed over the network.");
        }
    }
}
