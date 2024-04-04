package com.enderio.conduits.common.integrations.refinedstorage;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RSConduitType extends TieredConduit<RSNodeHost> {

    public RSConduitType() {
        super(EnderIO.loc("block/conduit/refined_storage"), new ResourceLocation("refinedstorage", "cable"), 0,
            EnderConduitTypes.ICON_TEXTURE, new Vector2i(0, 192));
    }

    @Override
    public Item getConduitItem() {
        return RSIntegration.NORMAL_ITEM.get();
    }

    @Override
    public IConduitTicker getTicker() {
        return RSTicker.INSTANCE;
    }

    @Override
    public IConduitMenuData getMenuData() {
        return RSMenuData.INSTANCE;
    }

    @Override
    public RSNodeHost createExtendedConduitData(Level level, BlockPos pos) {
        return new RSNodeHost(level, pos);
    }

    @Override
    public <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, RSNodeHost extendedConduitData, Level level, BlockPos pos,
        @Nullable Direction direction, Optional<NodeIdentifier.IOState> state) {
        if (cap == NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY) {
            return Optional.of(extendedConduitData.getSelfCap().cast());
        }
        return Optional.empty();
    }
}
