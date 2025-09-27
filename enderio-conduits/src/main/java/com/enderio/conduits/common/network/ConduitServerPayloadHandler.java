package com.enderio.conduits.common.network;

import com.enderio.conduits.api.ConduitCapabilities;
import com.enderio.conduits.api.bundle.ConduitBundle;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.enderio.conduits.common.conduit.menu.ConduitMenu;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitNetworkContext;
import com.enderio.conduits.common.init.ConduitComponents;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.conduits.common.items.ConduitProbeItem;
import com.enderio.conduits.common.redstone.DoubleRedstoneChannel;
import com.enderio.conduits.common.redstone.RedstoneCountFilter;
import com.enderio.conduits.common.redstone.RedstoneTimerFilter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
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
            var player = context.player();
            var level = player.level();
            var pos = packet.pos();

            // Ensure player can break this block
            if (!player.canInteractWithBlock(pos, 1.0)) {
                return;
            }

            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBundleBlockEntity conduitBundle) {
                // Check for safety.
                if (!conduitBundle.hasConduitStrict(packet.conduit())) {
                    return;
                }

                // Remove the conduit from the bundle
                conduitBundle.removeConduit(packet.conduit(), player, droppedItem ->
                    level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), droppedItem.copy())));

                // If the bundle is empty, destroy it.
                if (conduitBundle.isEmpty()) {
                    level.removeBlock(pos, false);
                }
            }
        });
    }
}
