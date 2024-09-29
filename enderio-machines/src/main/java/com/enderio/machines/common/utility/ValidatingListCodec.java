package com.enderio.machines.common.utility;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.List;
import java.util.Optional;

public record ValidatingListCodec<R>(Codec<List<R>> parent, int expectedSize) implements Codec<List<R>> {

    @Override
    public <T> DataResult<Pair<List<R>, T>> decode(DynamicOps<T> ops, T input) {
        var decode = parent.decode(ops, input);
        Optional<Pair<List<R>, T>> result = decode.result();
        if (result.isPresent() && result.get().getFirst().size() != expectedSize) {
            return DataResult.error(() -> "expected size was " + expectedSize + " but actual size is " + result.get().getFirst().size());
        }
        return decode;
    }

    @Override
    public <T> DataResult<T> encode(List<R> input, DynamicOps<T> ops, T prefix) {
        if (input.size() != expectedSize) {
            return DataResult.error(() -> "expected size was " + expectedSize + " but actual size is " + input.size());
        }
        return parent.encode(input, ops, prefix);
    }
}
