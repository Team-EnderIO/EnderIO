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
