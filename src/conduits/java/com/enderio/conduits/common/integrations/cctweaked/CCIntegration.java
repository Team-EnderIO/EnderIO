package com.enderio.conduits.common.integrations.cctweaked;

import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.integration.Integration;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;
import com.enderio.conduits.common.init.EIOConduitTypes;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.redstone.BundledRedstoneProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;

public class CCIntegration implements Integration {

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        ComputerCraftAPI.registerBundledRedstoneProvider(BUNDLE);
    }

    private static final BundledRedstoneProvider BUNDLE = new BundledRedstoneProvider (){

        @Override
        public int getBundledRedstoneOutput(Level world, BlockPos pos, Direction side) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ConduitBlockEntity conduit) {
                ConnectionState connectionState = conduit.getBundle().getConnectionState(side, EIOConduitTypes.Types.REDSTONE.get());
                if (connectionState instanceof DynamicConnectionState dyn && dyn.isInsert()) {
                    ExtendedConduitData<?> extendedConduitData = conduit.getBundle().getNodeFor(EIOConduitTypes.Types.REDSTONE.get()).getExtendedConduitData();
                    if (extendedConduitData instanceof RedstoneExtendedData redstone) {
                        int out = 0;
                        for (ColorControl control : ColorControl.values()) {
                            out |= (redstone.isActive(control) ? 1 : 0) << (getColor(control).getId());
                        }
                        return out;
                    }
                }
            }
            return -1;
        }

        private DyeColor getColor(ColorControl control) {
            return switch (control) {
                case GREEN -> DyeColor.GREEN;
                case BROWN -> DyeColor.BROWN;
                case BLUE -> DyeColor.BLUE;
                case PURPLE -> DyeColor.PURPLE;
                case CYAN -> DyeColor.CYAN;
                case LIGHT_GRAY -> DyeColor.LIGHT_GRAY;
                case GRAY -> DyeColor.GRAY;
                case PINK -> DyeColor.PINK;
                case LIME -> DyeColor.LIME;
                case YELLOW -> DyeColor.YELLOW;
                case LIGHT_BLUE -> DyeColor.LIGHT_BLUE;
                case MAGENTA -> DyeColor.MAGENTA;
                case ORANGE -> DyeColor.ORANGE;
                case WHITE -> DyeColor.WHITE;
                case BLACK -> DyeColor.BLACK;
                case RED -> DyeColor.RED;
            };
        }
    };
}
