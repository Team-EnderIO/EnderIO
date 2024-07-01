package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkContextSerializer;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record ConduitGraphContext(ConduitNetworkContext<?> context) implements Mergeable<ConduitGraphContext> {

    @Override
    public ConduitGraphContext mergeWith(ConduitGraphContext other) {
        return new ConduitGraphContext(context.mergeWith(other.castContext()));
    }

    @Override
    public ConduitGraphContext copy() {
        return new ConduitGraphContext(context.copy());
    }

    private <Z extends ConduitNetworkContext<Z>> Z castContext(){
        //noinspection unchecked
        return (Z)context;
    }

    public boolean canSerialize() {
        return context.serializer() != null;
    }

    public CompoundTag save() {
        var serializer = Objects.requireNonNull(context.serializer(), "This context type cannot be serialized.");
        var serializerKey = Objects.requireNonNull(EnderIORegistries.CONDUIT_NETWORK_CONTEXT_SERIALIZERS.getKey(serializer), "Serializer is not registered!");

        var tag = new CompoundTag();
        tag.putString("Type", serializerKey.toString());
        tag.put("Data", serializer.save(castContext()));
        return tag;
    }

    public static <T extends ConduitNetworkContext<T>> ConduitGraphContext of(T context) {
        return new ConduitGraphContext(context);
    }

    @Nullable
    public static ConduitGraphContext createNetworkContext(Holder<ConduitType<?, ?, ?>> conduitType, Graph<ConduitGraphContext> graph) {
        ConduitNetworkContext<?> context = createNetworkContext(conduitType.value(), graph);
        return context == null ? null : new ConduitGraphContext(context);
    }

    @Nullable
    private static <T extends ConduitNetworkContext<T>> T createNetworkContext(ConduitType<?, T, ?> conduitType,
        Graph<ConduitGraphContext> graph) {
        return conduitType.createNetworkContext(new WrappedConduitNetwork<>(graph));
    }

    public static ConduitGraphContext loadNetworkContext(Holder<ConduitType<?, ?, ?>> conduitType, Graph<ConduitGraphContext> graph,
        CompoundTag contextTag) {
        ConduitNetworkContext<?> context = loadNetworkContext(conduitType.value(), contextTag);
        return new ConduitGraphContext(context);
    }

    private static <T extends ConduitNetworkContext<T>> T loadNetworkContext(ConduitType<?, T, ?> conduitType, CompoundTag contextTag) {
        ResourceLocation serializerKey = ResourceLocation.parse(contextTag.getString("Type"));
        //noinspection unchecked
        var serializer = (ConduitNetworkContextSerializer<T>)Objects.requireNonNull(EnderIORegistries.CONDUIT_NETWORK_CONTEXT_SERIALIZERS.get(serializerKey),
            "Unable to find conduit network context serializer with key " + serializerKey);

        return serializer.load(contextTag.getCompound("Data"));
    }
}
