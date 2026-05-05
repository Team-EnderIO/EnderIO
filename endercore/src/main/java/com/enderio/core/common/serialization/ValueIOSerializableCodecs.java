package com.enderio.core.common.serialization;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.function.Supplier;

public class ValueIOSerializableCodecs {
    private static final Codec<ValueIOSerializable> ONE_WAY_VALUE_IO_SERIALIZABLE_CODEC = Codec.of(createEncoder(),
        Decoder.error("Cannot decode ValueIOSerializable directly"));

    /**
     * Will use a {@link ValueIOSerializable} to create and serialize a {@link CompoundTag}, and will deserialize back as {@link CompoundTag} for use later.
     */
    public static final Codec<Either<CompoundTag, ValueIOSerializable>> DEFERRED_CODEC = Codec.either(CompoundTag.CODEC, ONE_WAY_VALUE_IO_SERIALIZABLE_CODEC);

    /**
     * Creates a codec that can serialize and deserialize instances of {@link ValueIOSerializable}.
     * @param factory a factory to create the new instance of {@link A}
     * @return a codec to serialize instances of {@link A}
     * @param <A> the {@link ValueIOSerializable} type to be serialized.
     */
    public static <A extends ValueIOSerializable> Codec<A> createCodec(Supplier<A> factory) {
        return Codec.of(createEncoder(), new Decoder<>() {

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                if (!(ops instanceof RegistryOps<T> registryOps && registryOps.lookupProvider instanceof RegistryOps.HolderLookupAdapter holderLookupAdapter)) {
                    return DataResult.error(() -> "Registries required to decode ValueIOSerializable.");
                }

                var compoundTagResult = CompoundTag.CODEC.decode(ops, input);
                if (!compoundTagResult.hasResultOrPartial()) {
                    return DataResult.error(() -> "Failed to deserialize CompoundTag.");
                }

                CompoundTag compoundTag = compoundTagResult.getPartialOrThrow().getFirst();
                var result = factory.get();

                var problemReporter = new ProblemReporter.Collector();
                ValueInput valueInput = TagValueInput.create(problemReporter, holderLookupAdapter.lookupProvider, compoundTag);
                result.deserialize(valueInput);
                return DataResult.success(Pair.of(result, ops.empty()));
            }
        });
    }

    private static <A extends ValueIOSerializable> Encoder<A> createEncoder() {
        return new Encoder<>() {
            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                TagValueOutput output;
                if (ops instanceof RegistryOps<T> registryOps && registryOps.lookupProvider instanceof RegistryOps.HolderLookupAdapter holderLookupAdapter) {
                    output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, holderLookupAdapter.lookupProvider);
                } else {
                    output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
                }

                input.serialize(output);
                return CompoundTag.CODEC.encode(output.buildResult(), ops, ops.empty());
            }
        };
    }
}
