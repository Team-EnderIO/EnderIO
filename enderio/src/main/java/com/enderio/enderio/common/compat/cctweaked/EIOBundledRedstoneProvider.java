package com.enderio.enderio.common.compat.cctweaked;

import com.enderio.enderio.api.conduits.ConduitRedstoneSignalAware;
import com.enderio.enderio.common.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.common.conduits.type.redstone.RedstoneConduitConnectionConfig;
import com.enderio.enderio.common.conduits.type.redstone.RedstoneConduitNetworkContext;
import com.enderio.enderio.conduits.common.init.ConduitTypes;
import dan200.computercraft.api.redstone.BundledRedstoneProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EIOBundledRedstoneProvider implements BundledRedstoneProvider {

    public static EIOBundledRedstoneProvider INSTANCE = new EIOBundledRedstoneProvider();

    @Override
    public int getBundledRedstoneOutput(Level world, BlockPos pos, Direction side) {
        BlockEntity be = world.getBlockEntity(pos);

        if (be instanceof ConduitBundleBlockEntity conduit) {
            var redstoneConduit = conduit.getConduitByType(ConduitTypes.REDSTONE.get());
            if (redstoneConduit == null) {
                return -1;
            }

            if (!conduit.getConnectionStatus(redstoneConduit, side).isEndpoint()) {
                return -1;
            }

            var config = conduit.getConnectionConfig(redstoneConduit, side, RedstoneConduitConnectionConfig.TYPE);
            if (!config.canInsert(ConduitRedstoneSignalAware.NONE)) {
                return -1;
            }

            var node = conduit.getConduitNode(redstoneConduit);
            var network = node.getNetwork();
            if (network == null) {
                return -1;
            }

            var context = network.getContext(RedstoneConduitNetworkContext.TYPE);
            if (context == null) {
                return -1;
            }

            int out = 0;

            for (DyeColor color : DyeColor.values()) {
                out |= (context.isActive(color) ? 1 : 0) << color.getId();
            }
            return out;
        }

        return -1;
    }
}
