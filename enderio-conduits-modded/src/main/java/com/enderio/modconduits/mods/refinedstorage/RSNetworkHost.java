package com.enderio.modconduits.mods.refinedstorage;

import com.enderio.base.api.network.DumbStreamCodec;
import com.enderio.conduits.api.ConduitData;
import com.enderio.conduits.api.ConduitDataType;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.refinedmods.refinedstorage.api.network.impl.node.grid.GridNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage.platform.api.PlatformApi;
import com.refinedmods.refinedstorage.platform.api.security.SecurityHelper;
import com.refinedmods.refinedstorage.platform.api.support.network.ConnectionSink;
import com.refinedmods.refinedstorage.platform.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.platform.api.support.network.NetworkNodeContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class RSNetworkHost implements InWorldNetworkNodeContainer, ConduitData<RSNetworkHost> {
    public static final MapCodec<RSNetworkHost> CODEC = RecordCodecBuilder.mapCodec(rsNetworkHostInstance ->
        rsNetworkHostInstance.group(Codec.INT.fieldOf("int").forGetter(i -> i.i)
        ).apply(rsNetworkHostInstance, RSNetworkHost::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RSNetworkHost> STREAM_CODEC = DumbStreamCodec.of(RSNetworkHost::new).cast();

    private BlockState state = ConduitBlocks.CONDUIT.get().defaultBlockState();
    private GlobalPos pos;
    private GridNetworkNode node;
    private boolean removed = true;
    private final Set<InWorldNetworkNodeContainer> containers = new HashSet();
    private int i = 0;

    public RSNetworkHost() {
        containers.add(this);
    }

    public RSNetworkHost(int i) {

    }

    public void setup(Level level, BlockPos pos) {
        this.pos = GlobalPos.of(level.dimension(), pos);
        this.node = new GridNetworkNode(0);
        this.containers.add(this);
        PlatformApi.INSTANCE.onNetworkNodeContainerInitialized(this, level, null);
    }

    public void update(Level level) {
        containers.forEach(container -> PlatformApi.INSTANCE.onNetworkNodeContainerUpdated(container, level));
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
        return state;
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(Level level) {
        containers.forEach(container -> PlatformApi.INSTANCE.onNetworkNodeContainerRemoved(container, level));
        this.removed = true;
    }

    @Override
    public GlobalPos getPosition() {
        return pos;
    }

    @Override
    public BlockPos getLocalPosition() {
        return pos.pos();
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
            connectionSink.tryConnectInSameDimension(pos.pos().relative(direction), direction.getOpposite());
        }
    }

    @Override
    public boolean canAcceptIncomingConnection(Direction direction, BlockState blockState) {
        return true;
    }

    public Set<InWorldNetworkNodeContainer> getConnections() {
        return containers;
    }

    public void addConnection(InWorldNetworkNodeContainer connection) {
        containers.add(connection);
    }

    public boolean canBuild(ServerPlayer player) {
        return true;
        //return SecurityHelper.isAllowed(player, PlatformApi.INSTANCE.getBuiltinPermissions().build(), containers);
    }
}
