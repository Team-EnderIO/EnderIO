package com.enderio.core.common.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

class ValueIOSerializableHolderCodec<A extends ValueIOSerializable> implements Codec<ValueIOSerializableHolder<A>> {

    @Override
    public <T> DataResult<Pair<ValueIOSerializableHolder<A>, T>> decode(DynamicOps<T> ops, T input) {
        return CompoundTag.CODEC.decode(ops, input).map(result -> result.mapFirst(ValueIOSerializableHolder::new));
    }

    @Override
    public <T> DataResult<T> encode(ValueIOSerializableHolder<A> input, DynamicOps<T> ops, T prefix) {
        DataResult<CompoundTag> tag;
        if (input.unpacked != null) {
            tag = input.toCompound(ops);
        } else if (input.serialized != null) {
            tag = DataResult.success(input.serialized);
        } else {
            tag = DataResult.error(() -> "Unable to encode, no unpacked or serialized data available.");
        }

        return tag.flatMap(compound -> CompoundTag.CODEC.encode(compound, ops, prefix));
    }
}
