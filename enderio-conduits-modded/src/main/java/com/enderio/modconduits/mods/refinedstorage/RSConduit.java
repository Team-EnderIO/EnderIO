package com.enderio.modconduits.mods.refinedstorage;

import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitMenuData;
import com.enderio.conduits.api.ConduitNetwork;
import com.enderio.conduits.api.ConduitNode;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.enderio.conduits.common.conduit.block.ConduitBundleBlockEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.refinedmods.refinedstorage.platform.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        data.setup(level, pos);
    }

    @Override
    public void onRemoved(ConduitNode node, Level level, BlockPos pos) {
        var data = node.getOrCreateData(RSConduitsModule.DATA.get());
        data.setRemoved(level);
    }

    @Override
    public void onConnectTo(ConduitNode selfNode, ConduitNode otherNode) {
        if (otherNode.hasData(RSConduitsModule.DATA.get())) {
            otherNode.getData(RSConduitsModule.DATA.get()).update();
        }
    }

    @Override
    public boolean hasConnectionDelay() {
        return true;
    }

    private static final class Ticker implements ConduitTicker<RSConduit> {

        private static final Ticker INSTANCE = new Ticker();

        @Override
        public void tickGraph(ServerLevel level, RSConduit conduit, ConduitNetwork graph, ColoredRedstoneProvider coloredRedstoneProvider) {

        }

        @Override
        public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
            if (level.getBlockEntity(conduitPos) instanceof ConduitBundleBlockEntity conduit) {
                Holder<Conduit<?>> rsConduit = conduit.getLevel().holderOrThrow(RSConduitsModule.RS2);

                RSNetworkHost data = conduit.getBundle().getNodeFor(rsConduit).getData(RSConduitsModule.DATA.get());
                if (data == null) {
                    return false;
                }
                data.update();

                if (level.getBlockEntity(conduitPos.relative(direction)) instanceof AbstractNetworkNodeContainerBlockEntity<?> containerBlockEntity) {
                    for (var connection : containerBlockEntity.getContainers()) {
                        if (connection.canAcceptIncomingConnection(direction.getOpposite(), data.getBlockState())) {
                            return true;
                        }
                    }
                }

                if (level.getBlockEntity(conduitPos.relative(direction)) instanceof ConduitBundleBlockEntity other) {
                    return other.hasType(rsConduit);
                }
            }
            return false;
        }
    }
}
