package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListDataSlot<T, V extends Tag> extends EnderDataSlot<List<T>> {

    private final Function<T, V> serializer;
    private final Function<V, T> deSerializer;
    public ListDataSlot(Supplier<List<T>> getter, Consumer<List<T>> setter, Function<T, V> serializer, Function<V, T> deSerializer, SyncMode mode) {
        //I can put null here, because I override the only usage of the setter
        super(getter, setter, mode);
        this.serializer = serializer;
        this.deSerializer = deSerializer;
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (T t : getter().get()) {
            listTag.add(serializer.apply(t));
        }
        tag.put("list", listTag);
        return tag;
    }

    @Override
    protected List<T> fromNBT(CompoundTag nbt) {
        List<T> list = new ArrayList<>();
        for (Tag tag: (ListTag)nbt.get("list")) {
            list.add(deSerializer.apply((V)tag));
        }
        return list;
    }
}
