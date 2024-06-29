package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkContextSerializer;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import dev.gigaherz.graph3.ContextDataFactory;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record InternalGraphContext<T extends ConduitNetworkContext<T>>(T context) implements Mergeable<InternalGraphContext<T>> {
    @Override
    public InternalGraphContext<T> mergeWith(InternalGraphContext<T> other) {
        return new InternalGraphContext<>(context.mergeWith(other.context));
    }

    @Override
    public InternalGraphContext<T> splitFor(Graph<InternalGraphContext<T>> graph, Graph<InternalGraphContext<T>> graph1) {
        return new InternalGraphContext<>(context.splitFor(new WrappedConduitNetwork<>(graph), new WrappedConduitNetwork<>(graph1)));
    }

    public boolean canSerialize() {
        return context.serializer() != null;
    }

    public CompoundTag save() {
        var serializer = Objects.requireNonNull(context.serializer(), "This context type cannot be serialized.");
        var serializerKey = Objects.requireNonNull(EnderIORegistries.CONDUIT_NETWORK_CONTEXT_SERIALIZERS.getKey(serializer), "Serializer is not registered!");

        var tag = new CompoundTag();
        tag.putString("Type", serializerKey.toString());
        tag.put("Data", serializer.save(context));
        return tag;
    }

    public static <T extends ConduitNetworkContext<T>> InternalGraphContext<T> of(T context) {
        return new InternalGraphContext<>(context);
    }

    public static <T extends ConduitNetworkContext<T>> ContextDataFactory<InternalGraphContext<T>> factoryFor(ConduitType<?, T, ?> conduitType) {
        return graph -> {
            var context = conduitType.createGraphContext(new WrappedConduitNetwork<>(graph));
            return context == null ? null : new InternalGraphContext<>(context);
        };
    }

    public static <T extends ConduitNetworkContext<T>> ContextDataFactory<InternalGraphContext<T>> factoryForLoad(ConduitType<?, T, ?> conduitType, CompoundTag tag) {
        if (tag.contains("Type")) {
            ResourceLocation serializerKey = ResourceLocation.parse(tag.getString("Type"));
            //noinspection unchecked
            var serializer = (ConduitNetworkContextSerializer<T>)Objects.requireNonNull(EnderIORegistries.CONDUIT_NETWORK_CONTEXT_SERIALIZERS.get(serializerKey),
                "Unable to find conduit network context serializer with key " + serializerKey);

            return graph -> {
                var context = serializer.load(tag.getCompound("Data"));
                return new InternalGraphContext<>(context);
            };
        } else {
            return factoryFor(conduitType);
        }
    }
}
