package com.enderio.modded_conduits.common.modules.appeng;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import com.enderio.core.common.serialization.ValueIOSerializableCodecs;
import com.enderio.enderio.api.conduits.network.node.NodeData;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jspecify.annotations.Nullable;

public final class MEConduitNodeData implements NodeData, IInWorldGridNodeHost {

    public static final MapCodec<MEConduitNodeData> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(ValueIOSerializableCodecs.DEFERRED_CODEC.fieldOf("main_node").forGetter(data -> Either.right(data.mainNode)))
        .apply(inst, MEConduitNodeData::new));

    private MEConduitNodeData(Either<CompoundTag, ValueIOSerializable> mainNode) {
        // Our codec workaround for this will always deserialize as a CompoundTag.
        this(mainNode.left().orElseThrow(() -> new IllegalArgumentException("mainNode must decode as CompoundTag")));
    }

//    public static final MapCodec<MEConduitNodeData> CODEC = new MapCodec<>() {
//        @Override
//        public <T> Stream<T> keys(DynamicOps<T> ops) {
//            return Stream.of(ops.createString("main_node"));
//        }
//
//        @Override
//        public <T> DataResult<MEConduitNodeData> decode(DynamicOps<T> ops, MapLike<T> input) {
//            var mainNodeTagResult = CompoundTag.CODEC.decode(ops, input.get("main_node"));
//            if (!mainNodeTagResult.hasResultOrPartial()) {
//                return DataResult.error(() -> "Failed to deserialize main_node tag.");
//            }
//
//            CompoundTag mainNodeTag = mainNodeTagResult.getPartialOrThrow().getFirst();
//            var result = new MEConduitNodeData(mainNodeTag);
//
//            if (mainNodeTagResult.isError()) {
//                return DataResult.error(() -> "An error occurred loading main_node tag, partial data returned.", result);
//            }
//
//            return DataResult.success(result);
//        }
//
//        @Override
//        public <T> RecordBuilder<T> encode(MEConduitNodeData input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
//            TagValueOutput output;
//            if (ops instanceof RegistryOps<T> registryOps && registryOps.lookupProvider instanceof RegistryOps.HolderLookupAdapter holderLookupAdapter) {
//                output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, holderLookupAdapter.lookupProvider);
//            } else {
//                output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
//            }
//
//            input.serializeMainNode(output);
//            prefix.add("main_node", CompoundTag.CODEC.encode(output.buildResult(), ops, ops.empty()));
//            return prefix;
//        }
//    };

    public static final NodeDataType<MEConduitNodeData> TYPE = new NodeDataType<>(CODEC, MEConduitNodeData::new);

    @Nullable
    private IManagedGridNode mainNode = null;

    @Nullable
    private CompoundTag savedMainNode = null;

    private AECableType cableType = AECableType.SMART;

    public MEConduitNodeData() {
    }

    private MEConduitNodeData(CompoundTag savedMainNode) {
        this.savedMainNode = savedMainNode;
    }

    @Nullable
    public IManagedGridNode getMainNode() {
        return mainNode;
    }

    public void setMainNode(IManagedGridNode mainNode, boolean isDense) {
        this.mainNode = mainNode;
        this.cableType = isDense ? AECableType.DENSE_SMART : AECableType.SMART;
    }

    public void clearMainNode() {
        this.mainNode = null;
        this.cableType = AECableType.SMART;
    }

    public void loadMainNode(ProblemReporter problemReporter, HolderLookup.Provider registries) {
        if (mainNode == null) {
            throw new IllegalStateException("mainNode cannot be null.");
        }

        if (savedMainNode == null) {
            return;
        }

        ValueInput valueInput = TagValueInput.create(problemReporter, registries, savedMainNode);
        this.mainNode.deserialize(valueInput);
        savedMainNode = null;
    }

    private void serializeMainNode(ValueOutput output) {
        mainNode.serialize(output);
    }

    @Override
    public NodeDataType<?> type() {
        return TYPE;
    }

    @Override
    public @Nullable IGridNode getGridNode(Direction dir) {
        return mainNode != null ? mainNode.getNode() : null;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return cableType;
    }
}
