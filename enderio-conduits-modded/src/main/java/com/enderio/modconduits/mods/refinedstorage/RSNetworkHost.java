package com.enderio.modconduits.mods.refinedstorage;

import com.enderio.base.api.network.DumbStreamCodec;
import com.enderio.conduits.api.ConduitData;
import com.enderio.conduits.api.ConduitDataType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.refinedmods.refinedstorage.api.network.impl.node.grid.GridNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage.platform.api.PlatformApi;
import com.refinedmods.refinedstorage.platform.api.support.network.ConnectionSink;
import com.refinedmods.refinedstorage.platform.api.support.network.InWorldNetworkNodeContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RSNetworkHost implements InWorldNetworkNodeContainer, ConduitData<RSNetworkHost> {
    public static final MapCodec<RSNetworkHost> CODEC = RecordCodecBuilder.mapCodec(rsNetworkHostInstance ->
        rsNetworkHostInstance.group(
            BlockPos.CODEC.optionalFieldOf("pos", BlockPos.ZERO).forGetter(i -> i.pos)
        ).apply(rsNetworkHostInstance, RSNetworkHost::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RSNetworkHost> STREAM_CODEC = DumbStreamCodec.of(RSNetworkHost::new).cast();

    private Level level;
    private BlockPos pos = BlockPos.ZERO;
    private GridNetworkNode node;
    private boolean removed = true;

    public RSNetworkHost() {

    }

    public RSNetworkHost(BlockPos pos) {

    }

    public void setup(Level level, BlockPos pos) {
        this.removed = false;
        this.level = level;
        this.pos = pos;
        this.node = new GridNetworkNode(0);
        PlatformApi.INSTANCE.onNetworkNodeContainerInitialized(this, level, null);
    }

    public void update() {
        if (node != null && level != null) {
            PlatformApi.INSTANCE.onNetworkNodeContainerUpdated(this, level);
        }
    }

    @Override
    public RSNetworkHost deepCopy() {
        return new RSNetworkHost();
    }

    @Override
    public ConduitDataType<RSNetworkHost> type() {
        return RSConduitsModule.DATA.get();
    }

    @Override
    public BlockState getBlockState() {
        return level.getBlockState(pos);
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(Level level) {
        if (node != null) {
            PlatformApi.INSTANCE.onNetworkNodeContainerRemoved(this, level);
        }
        this.removed = true;
    }

    @Override
    public GlobalPos getPosition() {
        return GlobalPos.of(level.dimension(), pos);
    }

    @Override
    public BlockPos getLocalPosition() {
        return pos;
    }

    @Override
    public String getName() {
        return "Refined Storage Conduit";
    }

    @Override
    public NetworkNode getNode() {
        return node;
    }

    @Override
    public void addOutgoingConnections(ConnectionSink connectionSink) {
        for (Direction direction : Direction.values()) {
            connectionSink.tryConnectInSameDimension(pos.relative(direction), direction.getOpposite());
        }
    }

    @Override
    public boolean canAcceptIncomingConnection(Direction direction, BlockState blockState) {
        return true;
    }
}
