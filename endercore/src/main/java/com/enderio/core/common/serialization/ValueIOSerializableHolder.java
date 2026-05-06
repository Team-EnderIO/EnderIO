package com.enderio.core.common.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Container for a {@link ValueIOSerializable} that can be serialized and deserialized within a Codec using {@link #codec()}.
 * @param <T> the {@link ValueIOSerializable} type to hold.
 */
public class ValueIOSerializableHolder<T extends ValueIOSerializable> implements Supplier<T> {

    private static final Codec<ValueIOSerializableHolder<ValueIOSerializable>> CODEC = new ValueIOSerializableHolderCodec<>();

    @Nullable
    CompoundTag serialized;

    @Nullable
    T unpacked;

    /**
     * Constructor used by the codec during decoding.
     * @param tag the decoded tag.
     */
    ValueIOSerializableHolder(CompoundTag tag) {
        this.serialized = tag;
    }

    /**
     * Constructor used to create a new holder for the first time.
     * @param instance the instance to hold.
     */
    public ValueIOSerializableHolder(T instance) {
        this.unpacked = instance;
    }

    @Override
    public T get() {
        return Objects.requireNonNull(unpacked, "Not yet deserialized");
    }

    public boolean isPresent() {
        return unpacked != null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <VIO extends ValueIOSerializable> Codec<ValueIOSerializableHolder<VIO>> codec() {
        return (Codec)CODEC;
    }

    public void inflate(T newInstance, HolderLookup.Provider registries, ProblemReporter problemReporter) {
        ValueInput input = TagValueInput.create(problemReporter, registries, serialized);
        newInstance.deserialize(input);
        this.unpacked = newInstance;
    }

    <T> DataResult<CompoundTag> toCompound(DynamicOps<T> ops) {
        ProblemReporter.Collector reporter = new ProblemReporter.Collector();
        TagValueOutput output = ops instanceof RegistryOps<T> registryOps ?
            TagValueOutput.createWithContext(reporter, CommonHooks.extractLookupProvider(registryOps)) :
            TagValueOutput.createWithoutContext(reporter);
        Objects.requireNonNull(unpacked).serialize(output);
        CompoundTag buildResult = output.buildResult();
        if (reporter.isEmpty()) {
            // TODO: Consider potential effects
            serialized = buildResult;//update the serialized value only on success (?)
            return DataResult.success(buildResult);
        } else if (!buildResult.isEmpty()) {
            return DataResult.error(reporter::getTreeReport, buildResult);
        } else {
            return DataResult.error(reporter::getTreeReport);
        }
    }
}
