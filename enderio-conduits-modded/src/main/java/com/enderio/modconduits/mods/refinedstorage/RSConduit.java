package com.enderio.modconduits.mods.refinedstorage;

import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitMenuData;
import com.enderio.conduits.api.ConduitNetwork;
import com.enderio.conduits.api.ConduitNode;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.neoforge.RefinedStorageNeoForgeApiImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record RSConduit(ResourceLocation texture, Component description) implements Conduit<RSConduit> {
    public static final ConduitMenuData.Simple MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, false);
    public static MapCodec<RSConduit> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(RSConduit::texture),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(RSConduit::description)
        ).apply(builder, RSConduit::new)
    );

    @Override
    public ResourceLocation texture() {
        return texture;
    }

    @Override
    public Component description() {
        return description;
    }

    @Override
    public ConduitType<RSConduit> type() {
        return RSConduitsModule.RS2_CONDUIT.get();
    }

    @Override
    public ConduitTicker<RSConduit> getTicker() {
        return Ticker.INSTANCE;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENU_DATA;
    }

    @Override
    public int compareTo(@NotNull RSConduit o) {
        return 0;
    }

    @Override
    public void onCreated(ConduitNode node, Level level, BlockPos pos, @Nullable Player player) {
        var data = node.getOrCreateData(RSConduitsModule.DATA.get());
        if (data.mainNode == null) {
            data.mainNode = new RSNetworkHost.ConduitRSNode(level, pos);
            data.addContainer(data.mainNode);
            data.initialize(level, () -> {});
        }
    }

    @Override
    public void onRemoved(ConduitNode node, Level level, BlockPos pos) {
        var data = node.getOrCreateData(RSConduitsModule.DATA.get());
        if (data.mainNode != null) {
            data.mainNode.setRemoved(true);
            data.remove(level);
        }
    }

    @Override
    public void onConnectionsUpdated(ConduitNode node, Level level, BlockPos pos, Set<Direction> connectedSides) {
        var data = node.getOrCreateData(RSConduitsModule.DATA.get());
        if (data.mainNode != null) {
            for (Direction direction : connectedSides) {
                NetworkNodeContainerProvider capability = level.getCapability(RefinedStorageNeoForgeApiImpl.INSTANCE.getNetworkNodeContainerProviderCapability(), pos.relative(direction), direction.getOpposite());
                if (capability != null) {
                    for (var connection : capability.getContainers()) {
                        if (connection.canAcceptIncomingConnection(direction.getOpposite(), level.getBlockState(pos))) {
                            data.addContainer(connection);
                            capability.addContainer(data.mainNode);
                        }
                    }
                    capability.update(level);
                }
            }
            data.update(level);
        }
    }

    @Override
    public <K> @Nullable K proxyCapability(BlockCapability<K, Direction> capability, ConduitNode node, Level level, BlockPos pos, @Nullable Direction direction,
        ConduitNode.@Nullable IOState state) {
        if (capability == RefinedStorageNeoForgeApiImpl.INSTANCE.getNetworkNodeContainerProviderCapability()) {
            //noinspection unchecked
            return (K) node.getOrCreateData(RSConduitsModule.DATA.get());
        }
        return null;
    }

    private static final class Ticker implements ConduitTicker<RSConduit> {

        private static final Ticker INSTANCE = new Ticker();

        @Override
        public void tickGraph(ServerLevel level, RSConduit conduit, ConduitNetwork graph, ColoredRedstoneProvider coloredRedstoneProvider) {

        }

        @Override
        public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
            var cap = level.getCapability(RefinedStorageNeoForgeApiImpl.INSTANCE.getNetworkNodeContainerProviderCapability(), conduitPos.relative(direction), direction.getOpposite());
            if (cap != null) {
                for (var connection: cap.getContainers()) {
                    if (connection.canAcceptIncomingConnection(direction.getOpposite(), level.getBlockState(conduitPos))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
