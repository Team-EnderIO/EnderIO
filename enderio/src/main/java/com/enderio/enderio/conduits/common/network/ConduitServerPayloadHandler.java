package com.enderio.enderio.conduits.common.network;

import com.enderio.enderio.api.conduits.ConduitCapabilities;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.conduits.common.conduit.menu.ConduitMenu;
import com.enderio.enderio.conduits.common.conduit.type.fluid.FluidConduitNetworkContext;
import com.enderio.enderio.conduits.common.init.ConduitItems;
import com.enderio.enderio.conduits.common.init.ConduitTypes;
import com.enderio.enderio.conduits.common.items.ConduitProbeItem;
import com.enderio.enderio.conduits.common.redstone.DoubleRedstoneChannel;
import com.enderio.enderio.conduits.common.redstone.RedstoneCountFilter;
import com.enderio.enderio.conduits.common.redstone.RedstoneTimerFilter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ConduitServerPayloadHandler {
    private static final ConduitServerPayloadHandler INSTANCE = new ConduitServerPayloadHandler();

    public static ConduitServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleDoubleChannelFilter(DoubleChannelPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemStack mainHandItem = context.player().getMainHandItem();
            Object channels = mainHandItem.getCapability(ConduitCapabilities.REDSTONE_INSERT_FILTER);
            if (channels == null) {
                channels = mainHandItem.getCapability(ConduitCapabilities.REDSTONE_EXTRACT_FILTER);
            }

            if (channels instanceof DoubleRedstoneChannel doubleRedstoneChannel) {
                doubleRedstoneChannel.setChannels(packet.channel1(), packet.channel2());
            }
        });
    }

    public void handleTimerFilter(TimerFilterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemStack mainHandItem = context.player().getMainHandItem();
            var channels = mainHandItem.getCapability(ConduitCapabilities.REDSTONE_EXTRACT_FILTER);
            if (channels instanceof RedstoneTimerFilter timer) {
                timer.setTimer(packet.ticks(), packet.maxTicks());
            }
        });
    }

    public void handleCountFilter(CountFilterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemStack mainHandItem = context.player().getMainHandItem();
            var channels = mainHandItem.getCapability(ConduitCapabilities.REDSTONE_INSERT_FILTER);
            if (channels instanceof RedstoneCountFilter count) {
                count.setState(packet);
            }
        });
    }

    public void handle(C2SClearLockedFluidPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            var be = level.getBlockEntity(packet.pos());
            if (be instanceof ConduitBundle conduitBundle) {
                var fluidConduit = conduitBundle.getConduitByType(ConduitTypes.FLUID.get());
                if (fluidConduit != null) {
                    var node = conduitBundle.getConduitNode(fluidConduit);

                    var network = node.getNetwork();
                    if (network != null) {
                        var networkContext = network.getContext(FluidConduitNetworkContext.TYPE);
                        if (networkContext != null) {
                            networkContext.setLockedFluid(Fluids.EMPTY);
                        }
                    }
                }
            }
        });
    }

    public void handle(C2SOpenConduitFilterMenu packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.containerId() == context.player().containerMenu.containerId) {
                // TODO: Spectator viewing filter menus is broken lol
                if (!context.player().isSpectator() && context.player().containerMenu instanceof ConduitMenu menu) {
                    menu.tryOpenFilterMenu(packet.slot());
                }
            }
        });
    }

    public void handle(C2SSyncProbeStatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemStack heldStack = context.player().getMainHandItem();

            // Sanity check before updating item
            if (heldStack.is(ConduitItems.CONDUIT_PROBE)) {
                ConduitProbeItem.setState(context.player(), heldStack, packet.state());
            }
        });
    }

    public void handle(C2SBreakConduitPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = (ServerPlayer)context.player();
            var level = player.level();
            var pos = packet.pos();

            // Ensure player can break this block
            if (!player.canInteractWithBlock(pos, 1.0)) {
                return;
            }

            var blockstate = level.getBlockState(pos);
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBundleBlockEntity conduitBundle) {
                // Check for safety.
                if (!conduitBundle.hasConduitStrict(packet.conduit())) {
                    return;
                }

                // Fire block break event.
                BlockEvent.BreakEvent event = CommonHooks.fireBlockBreak(level, player.gameMode.getGameModeForPlayer(), player, pos, blockstate);
                if (event.isCanceled()) {
                    // Send block entity data back
                    level.sendBlockUpdated(pos, blockstate, blockstate, Block.UPDATE_ALL);
                    return;
                }

                // Remove the conduit from the bundle
                conduitBundle.removeConduit(packet.conduit(), droppedItem -> {
                    if (!player.getAbilities().instabuild) {
                        var center = pos.getCenter();
                        level.addFreshEntity(new ItemEntity(level, center.x, center.y, center.z, droppedItem.copy()));
                    }
                });

                // If the bundle is empty, destroy it.
                if (conduitBundle.isEmpty()) {
                    level.removeBlock(pos, false);
                }
            }
        });
    }

    public void handle(C2SRemoveConduitFacadePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = (ServerPlayer)context.player();
            var level = player.level();
            var pos = packet.pos();

            // Ensure player can break this block
            if (!player.canInteractWithBlock(pos, 1.0)) {
                return;
            }

            var blockState = level.getBlockState(pos);
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBundleBlockEntity conduitBundle) {
                // Fire block break event.
                BlockEvent.BreakEvent event = CommonHooks.fireBlockBreak(level, player.gameMode.getGameModeForPlayer(), player, pos, blockState);
                if (event.isCanceled()) {
                    // Send block entity data back
                    level.sendBlockUpdated(pos, blockState, blockState, Block.UPDATE_ALL);
                    return;
                }

                if (!player.getAbilities().instabuild) {
                    conduitBundle.dropFacadeItem();
                }

                int lightLevelBefore = level.getLightEmission(pos);
                conduitBundle.setFacadeProvider(ItemStack.EMPTY);

                // Handle light update
                if (lightLevelBefore != level.getLightEmission(pos)) {
                    level.getLightEngine().checkBlock(pos);
                }

                // If the bundle is empty, destroy it.
                if (conduitBundle.isEmpty()) {
                    level.removeBlock(pos, false);
                }
            }
        });
    }
}
