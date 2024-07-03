package com.enderio.conduits.common.integrations.refinedstorage;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RSConduitType extends ConduitType<RSNodeHost> {

    private static ConduitMenuData MENUDATA = new ConduitMenuData.Simple(false, false, false, false, false, false);

    public RSConduitType() {

    }

    @Override
    public Item getConduitItem() {
        return RSIntegration.NORMAL_ITEM.get();
    }

    @Override
    public ConduitTicker<RSNodeHost> getTicker() {
        return RSTicker.INSTANCE;
    }

    @Override
    public ConduitMenuData getMenuData() {
        return MENUDATA;
    }

    @Override
    public RSNodeHost createConduitData(Level level, BlockPos pos) {
        return new RSNodeHost(level, pos);
    }

    @Override
    public <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, RSNodeHost extendedConduitData, Level level, BlockPos pos,
        @Nullable Direction direction, ConduitNode.@Nullable IOState state) {
        if (cap == NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY) {
            return Optional.of(extendedConduitData.getSelfCap().cast());
        }
        return Optional.empty();
    }
}
