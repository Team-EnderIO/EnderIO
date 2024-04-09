package com.enderio.conduits.common.integrations.refinedstorage;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.IExtendedConduitData;
import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.RSAPIInject;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.util.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class RSNodeHost implements IExtendedConduitData<RSNodeHost>, INetworkNodeProxy<RSNetworkNode> {

    @RSAPIInject
    public static IRSAPI RSAPI;

    private final RSNetworkNode node;
    private final LazyOptional<RSNodeHost> selfCap = LazyOptional.of(() -> this);
    private final Level level;
    private final BlockPos pos;

    public RSNodeHost(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
        this.node = new RSNetworkNode(level, pos);
    }

    @NotNull
    @Override
    public RSNetworkNode getNode() {
        if (level.isClientSide())
            return node;

        INetworkNodeManager manager = RSAPI.getNetworkNodeManager((ServerLevel) level);
        INetworkNode networkNode = manager.getNode(pos);

        if (networkNode == null || !networkNode.getId().equals(RSNetworkNode.ID)) {
            manager.setNode(pos, node);
            manager.markForSaving();
        }

        return node;
    }

    public LazyOptional<RSNodeHost> getSelfCap() {
        return selfCap;
    }

    @Override
    public void onRemoved(IConduitType<?> type, Level level, BlockPos pos) {
        selfCap.invalidate();

        INetworkNodeManager manager = RSAPI.getNetworkNodeManager((ServerLevel) level);

        INetworkNode node = manager.getNode(pos);

        manager.removeNode(pos);
        manager.markForSaving();

        if (node != null && node.getNetwork() != null) {
            node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().getLevel(), node.getNetwork().getPosition());
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        node.write(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        node.read(nbt);
    }
}
