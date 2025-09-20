package com.enderio.modconduits.common.modules.appeng;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import com.enderio.conduits.api.network.node.NodeData;
import com.enderio.conduits.api.network.node.NodeDataType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public final class MEConduitNodeData implements NodeData, IInWorldGridNodeHost {

    public static final MapCodec<MEConduitNodeData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(CompoundTag.CODEC.fieldOf("main_node").forGetter(MEConduitNodeData::saveMainNode))
            .apply(instance, MEConduitNodeData::new));

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

    // TODO: 1.22 Remove.
    MEConduitNodeData(@Nullable IManagedGridNode mainNode, @Nullable CompoundTag savedMainNode, boolean isDense) {
        this.mainNode = mainNode;
        this.savedMainNode = savedMainNode;
        this.cableType = isDense ? AECableType.DENSE_SMART : AECableType.SMART;
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

    public void loadMainNode() {
        if (mainNode == null) {
            throw new IllegalStateException("mainNode cannot be null.");
        }

        if (savedMainNode == null) {
            return;
        }

        this.mainNode.loadFromNBT(savedMainNode);
        savedMainNode = null;
    }

    private CompoundTag saveMainNode() {
        var tag = new CompoundTag();
        if (mainNode != null) {
            mainNode.saveToNBT(tag);
        }
        return tag;
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
